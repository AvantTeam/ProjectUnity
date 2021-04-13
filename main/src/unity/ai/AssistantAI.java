package unity.ai;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.CoreBlock.*;
import mindustry.world.meta.*;
import unity.entities.comp.*;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class AssistantAI extends FlyingAI{
    protected static IntMap<ObjectSet<Unit>> hooks = new IntMap<>();

    protected Teamc user;

    protected final Seq<Assistance> services;
    protected final Interval timer = new Interval(2);
    protected Assistance current;

    public AssistantAI(Assistance... services){
        this.services = Seq.with(services).sort(Assistance::ordinal);
    }

    public static Prov<AssistantAI> create(Assistance... services){
        return () -> new AssistantAI(services);
    }

    @Override
    public void updateUnit(){
        updateAssistance();
        updateVisuals();
        updateTargeting();
        updateMovement();
    }

    public void updateAssistance(){
        if(timer.get(0, 5f)){
            for(Assistance service : services){
                if(current != service && service.predicate.get(this)){
                    current = service;
                    current.init(this);
                    break;
                }
            }
        }

        if((
            user == null ||
            !user.isAdded() ||
            user instanceof Unit unit
            ?   !unit.isPlayer()
            :   true
        ) && timer.get(1, 5f)){
            updateUser();
        }

        if(current != null){
            current.update(this);
        }
    }

    @Override
    protected void updateVisuals(){
        if(target != null){
            unit.lookAt(unit.prefRotation());
        }else if(current != null && current.updateVisuals.get(this)){
            if(current.preserveVisuals){
                if(target != null || user == null){
                    unit.lookAt(unit.prefRotation());
                }else if(user instanceof Rotc rot){
                    unit.lookAt(rot.rotation());
                }
            }
            current.updateVisuals(this);
        }else if(target != null || user == null){
            unit.lookAt(unit.prefRotation());
        }else if(user instanceof Rotc rot){
            unit.lookAt(rot.rotation());
        }
    }

    @Override
    protected void updateTargeting(){
        super.updateTargeting();
        if(current != null && current.updateTargetting.get(this)){
            current.updateTargetting(this);
        }
    }

    @Override
    public void updateMovement(){
        if(target != null){
            attack(120f);
        }else if(current != null && current.updateMovement.get(this)){
            if(current.preserveMovement){
                if(target != null){
                    attack(120f);
                }else if(user != null){
                    circle(user, unit.type.range * 0.8f);
                }
            }

            current.updateMovement(this);
        }else if(user != null){
            circle(user, unit.type.range * 0.8f);
        }
    }

    @Override
    protected void circle(Position target, float circleLength, float speed){
        if(target == null) return;

        vec.set(target).sub(unit);

        boolean reached;
        if(reached = (vec.len() < circleLength)){
            vec.rotate((circleLength - vec.len()) / circleLength * 180f);
        }

        vec.setLength(reached ? unit.speed() / 2f : speed);
        unit.moveAt(vec);
    }

    public void updateUser(){
        Teamc next = null;
        int prev = Integer.MAX_VALUE;

        for(Player player : Groups.player){
            ObjectSet<Unit> assists = hooks.get(player.id, ObjectSet::new);
            if(assists.size < prev){
                next = player.unit();
                prev = assists.size;
            }
        }

        if(next == null) next = targetFlag(unit.x, unit.y, BlockFlag.core, false);
        if(next != null) hooks.get(next.id(), ObjectSet::new).add(unit);

        user = next;
    }

    protected void displayMessage(String message){
        if(unit instanceof Assistantc assist){
            assist.textFadeTime(1f);
            assist.lastText(message);
        }
    }

    @Override
    protected void init(){
        updateUser();
    }

    /** Assistant service types. <b>Uses {@code ordinal()} for sorting</b>, with the lesser as the more prioritized. */
    public enum Assistance{
        mendCore{
            IntMap<CoreBuild> tiles = new IntMap<>();

            {
                predicate = ai -> canMend(ai) && state.teams.cores(ai.unit.team).contains(b -> b.health() < b.maxHealth());

                updateVisuals = updateMovement = predicate;

                updateTargetting = ai ->
                    canMend(ai) &&
                    tile(ai) != null &&
                    ai.unit.dst(tile(ai)) <= ai.unit.type.range;
            }

            boolean canMend(AssistantAI ai){
                return ai.unit.type.weapons.contains(w -> w.bullet.healPercent > 0f && w.bullet.collidesTeam);
            }

            CoreBuild tile(AssistantAI ai){
                return tiles.get(ai.unit.id);
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage(Core.bundle.get("service.coredamage"));
            }

            @Override
            protected void update(AssistantAI ai){
                tiles.put(ai.unit.id, state.teams.cores(ai.unit.team).min(b -> b.health() < b.maxHealth(), b -> ai.unit.dst2(b)));
            }

            @Override
            protected void updateVisuals(AssistantAI ai){
                if(tile(ai) != null){
                    ai.unit.lookAt(tile(ai));
                }
            }

            @Override
            protected void updateMovement(AssistantAI ai){
                if(tile(ai) != null){
                    ai.circle(tile(ai), canMend(ai) ? ai.unit.type.range * 0.8f : ai.unit.type.range * 1.2f);
                }
            }

            @Override
            protected void updateTargetting(AssistantAI ai){
                for(WeaponMount mount : ai.unit.mounts){
                    Weapon weapon = mount.weapon;
                    if(weapon.bullet.healPercent <= 0f) continue;

                    var tile = tile(ai);
                    float rotation = ai.unit.rotation - 90f;

                    float
                        mountX = ai.unit.x + Angles.trnsx(rotation, weapon.x, weapon.y),
                        mountY = ai.unit.y + Angles.trnsy(rotation, weapon.x, weapon.y);

                    boolean shoot = tile.within(mountX, mountY, weapon.bullet.range()) && ai.shouldShoot();

                    Vec2 to = Predict.intercept(ai.unit, tile, weapon.bullet.speed);
                    mount.aimX = to.x;
                    mount.aimY = to.y;

                    mount.shoot = shoot;
                    mount.rotate = shoot;

                    ai.unit.isShooting |= shoot;
                    if(shoot){
                        ai.unit.aimX = mount.aimX;
                        ai.unit.aimY = mount.aimY;
                    }
                }
            }
        };

        protected Boolf<AssistantAI> predicate = ai -> false;
        protected Boolf<AssistantAI> updateVisuals = ai -> false;
        protected boolean preserveVisuals = false;

        protected Boolf<AssistantAI> updateTargetting = ai -> false;

        protected Boolf<AssistantAI> updateMovement = ai -> false;
        protected boolean preserveMovement = false;

        protected void init(AssistantAI ai){}

        protected void update(AssistantAI ai){}

        protected void updateVisuals(AssistantAI ai){}

        protected void updateTargetting(AssistantAI ai){}

        protected void updateMovement(AssistantAI ai){}
    }
}
