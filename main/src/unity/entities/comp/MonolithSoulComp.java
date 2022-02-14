package unity.entities.comp;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.content.effects.*;
import unity.content.units.*;
import unity.entities.*;
import unity.gen.*;
import unity.mod.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
@EntityDef({Unitc.class, MonolithSoulc.class, Trailc.class, Factionc.class})
@EntityComponent
abstract class MonolithSoulComp implements Unitc, Trailc, Factionc{
    @Import UnitType type;
    @Import Team team;
    @Import float x, y, rotation, health, maxHealth, elevation;
    @Import Trail trail;

    @ReadOnly transient boolean corporeal;
    @ReadOnly transient float joinTime;
    @ReadOnly transient Teamc joinTarget;
    @ReadOnly transient float ringRotation;
    @ReadOnly transient Seq<Tile> forms = new Seq<>(5);
    @ReadOnly transient float formProgress;

    @Override
    public Faction faction(){
        return Faction.monolith;
    }

    @Override
    public void add(){
        health = Math.min(maxHealth / 2f, health);
    }

    @Override
    @MethodPriority(-1)
    public void update(){
        if(!corporeal){
            health = Mathf.clamp(health + (joining() ? -0.2f : lifeDelta()) * Time.delta, 0f, maxHealth);
            joinTime = (joinTarget == null || !joinTarget.isAdded()) ? Mathf.lerpDelta(joinTime, 0f, 0.1f) : Mathf.approachDelta(joinTime, 1f, 0.008f);
            formProgress = Mathf.lerpDelta(formProgress, forms.any() ? (health / maxHealth) : 0f, 0.17f);
            ringRotation = Mathf.slerp(ringRotation, joinTarget == null ? rotation : angleTo(joinTarget), 0.08f);

            if(!joinValid(joinTarget)) joinTarget = null;
            forms.removeAll(t -> !formValid(t));

            if(isLocal()){
                //TODO input handling for mobile users :c
                if(!mobile){
                    float mx = Core.input.mouseWorldX(), my = Core.input.mouseWorldY();

                    if(Core.input.keyTap(Binding.select)){
                        Tile tile = world.tileWorld(mx, my);
                        if(tile != null) form(tile);
                    }else if(Core.input.keyTap(Binding.break_block)){
                        Teamc target = Units.closest(team, mx, my, 1f, this::joinValid);
                        if(target == null) target = world.buildWorld(mx, my);

                        if(target != null && target.team() == team) join(target);
                    }
                }
            }
        }else if(health <= maxHealth * 0.5f){
            DeathFx.monolithSoulCrack.at(x, y, rotation);

            corporeal = false;
            joinTarget = null;
            forms.clear();
            formProgress = 0f;
        }

        if(isValid()){
            if(Mathf.equal(joinTime, 1f) && joinValid(joinTarget)){
                kill();
                DeathFx.monolithSoulJoin.at(x, y, ringRotation, this);

                LineFx.monolithSoulTransfer.at(x, y, rotation, joinTarget);
                Time.run(LineFx.monolithSoulTransfer.lifetime, Soul.toSoul(joinTarget)::join);
            }else if(!corporeal && Mathf.equal(health, maxHealth)){
                corporeal = true;
                joinTime = 0f;
            }
        }
    }

    @Override
    public void destroy(){
        if(!isAdded()) return;
        TrailFx.trailFadeLow.at(x, y, (type.engineSize + Mathf.absin(Time.time, 2f, type.engineSize / 4f) * elevation) * type.trailScl, trail.copy());
    }

    void join(Teamc other){
        if(!joinValid(other)) return;

        if(forms.any()) forms.clear();
        joinTarget = other;
    }

    void form(Tile tile){
        if(forms.contains(tile)){
            forms.remove(tile);
            return;
        }

        if(!formValid(tile)) return;
        if(forms.size >= 5) forms.remove(0);

        joinTarget = null;
        forms.add(tile);
    }

    boolean joinValid(Teamc other){
        Soul soul = Soul.toSoul(other);
        return soul != null && other.isAdded() && soul.acceptSoul(1) >= 1 && within(other, type.range + (other instanceof Unit unit ? (unit.hitSize / 2f) : other instanceof Building build ? (build.hitSize() / 2f) : 0f));
    }

    boolean formValid(Tile tile){
        if(tile.synthetic() || Mathf.dst(x, y, tile.getX(), tile.getY()) > type.range) return false;
        return FactionMeta.map(tile.solid() ? tile.block() : tile.floor()) == Faction.monolith;
    }

    boolean joining(){
        return joinTarget != null && joinTarget.isAdded();
    }

    boolean forming(){
        return forms.any();
    }

    float lifeDelta(){
        return (forms.size - 2.5f) * 0.18f;
    }

    static MonolithSoul create(Team team){
        return (MonolithSoul)MonolithUnitTypes.monolithSoul.create(team);
    }
}
