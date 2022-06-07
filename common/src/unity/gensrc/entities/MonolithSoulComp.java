package unity.gensrc.entities;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.gen.entities.*;
import unity.mod.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
@EntityComponent
@EntityDef({MonolithSoulc.class, Unitc.class, Factionc.class})
abstract class MonolithSoulComp implements Unitc, Factionc{
    // To be filled later.
    static final Func<Team, MonolithSoul>[] constructors = new Func[4];

    @Import float x, y, rotation, hitSize, health, maxHealth;
    @Import Team team;
    @Import UnitType type;

    @SyncLocal boolean corporeal;
    @SyncField(false) @SyncLocal float ringRotation;

    @SyncField(true) @SyncLocal float joinTime;
    @SyncLocal Healthc joinTarget;

    @SyncLocal Seq<Tile> forms = new Seq<>(5);
    @SyncField(true) @SyncLocal float formProgress;

    private transient MonolithSoulProps props;
    private transient boolean wasCorporeal;

    @Override
    public Faction faction(){
        return Faction.monolith;
    }

    @Override
    public void add(){
        health = Math.min(maxHealth / 2f, health);
    }

    @Override
    public void setType(UnitType type){
        if(type instanceof PUUnitTypeCommon def) props = def.propReq(MonolithSoulProps.class);
    }

    @Override
    public void afterSync(){
        if(!isLocal()){
            if(wasCorporeal && !corporeal) crack();
            wasCorporeal = corporeal;
        }
    }

    @Override
    @MethodPriority(-1)
    public void update(){
        ringRotation = Mathf.slerp(ringRotation, joining() ? rotation : angleTo(joinTarget), props.ringRotateSpeed);
        if(isLocal()){
            if(!mobile){
                float mx = Core.input.mouseWorldX(), my = Core.input.mouseWorldY();
                if(Core.input.keyTap(Binding.select)){
                    Tile tile = world.tileWorld(mx, my);
                    if(tile != null) form(tile);
                }else if(Core.input.keyTap(Binding.deselect)){
                    Teamc target = Units.closest(team, mx, my, 1f, other -> !joinInvalid(other));
                    if(target == null) target = world.buildWorld(mx, my);

                    if(target instanceof Healthc h && target.team() == team) join(h);
                }
            }
        }

        if(!net.client() || isLocal()){
            if(!corporeal){
                if(joinInvalid(joinTarget)) joinTarget = null;
                forms.removeAll(this::formInvalid);

                health = Mathf.clamp(health + (joining() ? props.healthJoinDelta : lifeDelta()), 0f, maxHealth);
                joinTime = joining() ? Mathf.lerpDelta(joinTime, 0f, props.joinCooldown) : Mathf.approachDelta(joinTime, 1f, props.joinWarmup);
                formProgress = Mathf.lerpDelta(formProgress, forming() ? (health / maxHealth) : 0f, props.formWarmup);
            }else if(health <= maxHealth * 0.5f){
                crack();
            }

            if(isValid()){
                if(Mathf.equal(joinTime, 1f) && !joinInvalid(joinTarget)){
                    split(SoulHolder.toSoul(joinTarget).acceptSoul(props.transferAmount));
                    if(isPlayer()){
                        Player player = getPlayer();
                        if(joinTarget instanceof ControlBlock block && !block.isControlled() && block.canControl()){
                            block.unit().controller(player);
                        }else if(joinTarget instanceof Unit unit && !unit.isPlayer() && unit.type.playerControllable){
                            unit.controller(player);
                        }
                    }

                    //DeathFx.monolithSoulJoin.at(x, y, ringRotation, this);
                    props.joinEffect.at(x, y, ringRotation, this);

                    //LineFx.monolithSoulTransfer.at(x, y, rotation, joinTarget);
                    props.transferEffect.at(x, y, rotation, joinTarget);
                    Time.run(props.transferDelay, () -> {
                        if(joinTarget.isValid()){
                            SoulHolder.toSoul(joinTarget).transferSoul(props.transferAmount);
                            props.joinSound.at(x, y, Mathf.random(props.joinPitchMin, props.joinPitchMax));
                        }
                    });
                }else if(!corporeal && Mathf.equal(health, maxHealth)){
                    corporeal = true;
                    joinTime = 0f;
                }
            }
        }
    }

    void crack(){
        props.crackEffect.at(x, y, rotation);
        props.crackSound.at(x, y, Mathf.random(props.crackPitchMin, props.crackPitchMax));

        corporeal = false;
        joinTarget = null;
        forms.clear();
        formProgress = 0f;
        joinTime = 0f;
    }

    void join(Healthc other){
        if(joinInvalid(other)) return;

        if(forms.any()) forms.clear();
        joinTarget = other;
    }

    void form(Tile tile){
        if(forms.contains(tile)){
            forms.remove(tile);
            return;
        }

        if(formInvalid(tile)) return;
        if(forms.size >= props.formAmount) forms.remove(0);

        joinTarget = null;
        forms.add(tile);
    }

    boolean joinInvalid(Healthc other){
        SoulHolder soul = SoulHolder.toSoul(other);
        return
            soul == null || !other.isAdded() || soul.acceptSoul(1) < 1 ||
            !within(other, type.range + (other instanceof Unit unit
                ? (unit.hitSize / 2f) : other instanceof Building build
                ? (build.hitSize() / 2f) : 0f
            ));
    }

    boolean formInvalid(Tile tile){
        if(tile.synthetic() || Mathf.dst(x, y, tile.getX(), tile.getY()) > type.range) return true;
        return FactionRegistry.faction(tile.solid() ? tile.block() : tile.floor()) != Faction.monolith;
    }

    boolean joining(){
        return joinTarget != null && joinTarget.isValid();
    }

    boolean forming(){
        return forms.any();
    }

    float lifeDelta(){
        return (forms.size - props.formAmount / 2f) * props.formDelta;
    }

    void split(int withdraw){
        kill();
        if(net.server() || !net.active()) SoulHolder.spread(team, props.transferAmount - withdraw, this::branch);
    }

    private void branch(MonolithSoul soul){
        Tmp.v1.trns(Mathf.random(360f), hitSize);
        soul.set(x + Tmp.v1.x, y + Tmp.v1.y);

        Tmp.v1.trns(Mathf.random(360f), Mathf.random(props.splitVelMin, props.splitVelMax));
        soul.rotation = Tmp.v1.angle();
        soul.vel.set(Tmp.v1);
        soul.add();
    }
}
