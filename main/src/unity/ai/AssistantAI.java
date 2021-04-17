package unity.ai;

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
import unity.util.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class AssistantAI extends FlyingAI{
    protected static IntMap<ObjectSet<Unit>> hooks = new IntMap<>();

    protected Teamc user;

    protected final Seq<Assistance> services;
    protected final Interval timer = new Interval(1);
    protected Assistance current;

    public AssistantAI(Assistance... services){
        this.services = Seq.with(services).sort(service -> service.priority);
    }

    public static Prov<AssistantAI> create(Assistance... services){
        return () -> new AssistantAI(services);
    }

    @Override
    public void updateUnit(){
        updateAssistance();
        updateTargeting();

        if(fallback != null && target == null){
            if(fallback.unit() != unit) fallback.unit(unit);

            if(current != null){
                Utils.invokeMethod(
                fallback,
                Utils.findMethod(fallback.getClass(), "updateTargeting", true, Utils.emptyClasses),
                Utils.emptyObjects
                );

                if(current.preserveVisuals){
                    Utils.invokeMethod(
                    fallback,
                    Utils.findMethod(fallback.getClass(), "updateVisuals", true, Utils.emptyClasses),
                    Utils.emptyObjects
                    );
                }

                if(current.preserveMovement){
                    Utils.invokeMethod(
                    fallback,
                    Utils.findMethod(fallback.getClass(), "updateMovement", true, Utils.emptyClasses),
                    Utils.emptyObjects
                    );
                }
            }else{
                fallback.updateUnit();
            }
        }else{
            updateVisuals();
            updateMovement();
        }
    }

    public void updateAssistance(){
        for(Assistance service : services){
            if(current != null && !current.predicate.get(this)){
                current.dispose(this);
                current = null;
            }

            if(current != service && service.predicate.get(this)){
                if(current != null) current.dispose(this);

                current = service;
                current.init(this);

                break;
            }
        }

        if((
            !userValid() || (
            user instanceof Unit unit
            ?   !unit.isPlayer()
            :   true
        )) && timer.get(5f)){
            updateUser();
        }

        if(current != null){
            current.update(this);
        }
    }

    @Override
    protected void updateVisuals(){
        if(current != null && current.updateVisuals.get(this)){
            if(current.preserveVisuals){
                unit.lookAt(unit.prefRotation());
            }
            current.updateVisuals(this);
        }else{
            unit.lookAt(unit.prefRotation());
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
            if(!unit.type.circleTarget){
                moveTo(target, unit.type.range * 0.8f);
                unit.lookAt(target);
            }else{
                attack(120f);
            }
        }else if(current != null && current.updateMovement.get(this)){
            if(current.preserveMovement){
                if(target != null){
                    if(!unit.type.circleTarget){
                        moveTo(target, unit.type.range * 0.8f);
                        unit.lookAt(target);
                    }else{
                        attack(120f);
                    }
                }else if(userValid()){
                    moveTo(user, unit.type.range * 0.8f);
                }
            }

            current.updateMovement(this);
        }else if(userValid()){
            moveTo(user, unit.type.range * 0.8f);
        }
    }

    public void updateUser(){
        if(userValid()) hooks.get(user.id(), ObjectSet::new).remove(unit);

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

    public boolean userValid(){
        return user != null && user.isAdded() && (user instanceof Healthc health ? health.isValid() : true);
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
        displayMessage("service.init");
    }

    public enum Assistance{
        mendCore(-100f){
            final IntMap<CoreBuild> tiles = new IntMap<>();

            {
                predicate = ai -> canMend(ai) && state.teams.cores(ai.unit.team).contains(b -> b.health() < b.maxHealth());

                updateVisuals = updateMovement = predicate;

                updateTargetting = ai ->
                    canMend(ai) &&
                    tile(ai) != null &&
                    ai.unit.dst(tile(ai)) <= ai.unit.type.range;
            }

            boolean canMend(AssistantAI ai){
                return ai.unit.ammof() > 0f && ai.unit.type.weapons.contains(w -> w.bullet.healPercent > 0f && w.bullet.collidesTeam);
            }

            CoreBuild tile(AssistantAI ai){
                return tiles.get(ai.unit.id);
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage("service.core");
            }

            @Override
            protected void dispose(AssistantAI ai){
                if(state.teams.cores(ai.unit.team).contains(b -> b.health() < b.maxHealth()) || ai.unit.ammof() <= 0f){
                    ai.displayMessage("service.outofammo");
                }else{
                    ai.displayMessage("service.coredone");
                }
            }

            @Override
            protected void update(AssistantAI ai){
                tiles.put(ai.unit.id, state.teams.cores(ai.unit.team).min(b -> b.health() < b.maxHealth(), b -> ai.unit.dst2(b)));
            }

            @Override
            protected void updateVisuals(AssistantAI ai){
                var tile = tile(ai);
                if(tile != null){
                    ai.unit.lookAt(tile);
                }
            }

            @Override
            protected void updateMovement(AssistantAI ai){
                var tile = tile(ai);
                if(tile != null){
                    ai.moveTo(tile, ai.unit.type.range * 0.9f);
                }
            }

            @Override
            protected void updateTargetting(AssistantAI ai){
                var tile = tile(ai);
                for(WeaponMount mount : ai.unit.mounts){
                    Weapon weapon = mount.weapon;
                    if(weapon.bullet.healPercent <= 0f) continue;

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
        },

        build(0f){
            {
                predicate = ai ->
                    ai.unit.type.buildSpeed > 0f &&
                    ai.user instanceof Builderc builder &&
                    builder.activelyBuilding();

                preserveVisuals = preserveMovement = true;
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage("service.build");
            }

            @Override
            protected void update(AssistantAI ai){
                if(!(ai.fallback instanceof BuilderAI)){
                    ai.fallback = new BuilderAI();
                }

                if(ai.fallback instanceof BuilderAI buildAI && ai.user instanceof Builderc builder){
                    Utils.setField(buildAI, Utils.findField(buildAI.getClass(), "following", true), builder);
                }
            }

            @Override
            protected void dispose(AssistantAI ai){
                if(ai.fallback instanceof BuilderAI){
                    ai.fallback = null;
                    ai.unit.clearBuilding();
                }
            }
        },

        mine(10f){
            {
                predicate = ai ->
                (
                    ai.unit.mining() &&
                    ai.unit.closestCore().acceptStack(
                        ai.unit.stack.item,
                        ai.unit.stack.amount,
                        ai.unit
                    ) > 0
                ) || (
                    ai.unit.type.mineTier > 0 &&
                    ai.unit.type.itemCapacity > 0 &&
                    ai.user instanceof Minerc miner &&
                    miner.mining() &&
                    ai.unit.validMine(miner.mineTile(), false) &&
                    ai.unit.closestCore().acceptStack(miner.mineTile().drop(), 1, ai.unit) > 0
                );

                preserveVisuals = preserveMovement = true;
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage("service.mine");
            }

            @Override
            protected void update(AssistantAI ai){
                if(!(ai.fallback instanceof FixedMinerAI)){
                    ai.fallback = new FixedMinerAI();
                }

                if(ai.fallback instanceof FixedMinerAI minAI && ai.user instanceof Minerc miner){
                    minAI.targetItem = ai.unit.stack.amount > 0
                    ?   ai.unit.stack.item
                    :   (
                        miner.mineTile() != null
                        ?   miner.mineTile().drop()
                        :   null
                    );
                }
            }

            @Override
            protected void dispose(AssistantAI ai){
                if(ai.fallback instanceof FixedMinerAI){
                    ai.fallback = null;
                    ai.unit.clearItem();
                }
            }
        },

        heal(20f){
            final float rad = 30f * tilesize;
            final Boolf2<Healthc, AssistantAI> pred;

            {
                pred = (target, ai) ->
                    target.within(ai.unit, rad) &&
                    target.health() < target.maxHealth();

                predicate = ai -> ai.unit.ammof() > 0f && hasTarget(ai);

                preserveVisuals = preserveMovement = true;
            }

            boolean hasTarget(AssistantAI ai){
                return
                    Groups.unit.contains(unit -> pred.get(unit, ai)) ||
                    indexer.findTile(ai.unit.team, ai.unit.x, ai.unit.y, rad, tile -> pred.get(tile, ai)) != null;
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage("service.mend");
            }

            @Override
            protected void dispose(AssistantAI ai){
                if(ai.fallback instanceof UnitHealerAI){
                    ai.fallback = null;
                }

                if(hasTarget(ai) || ai.unit.ammof() <= 0f){
                    ai.displayMessage("service.outofammo");
                }else{
                    ai.displayMessage("service.menddone");
                }
            }

            @Override
            protected void update(AssistantAI ai){
                if(!(ai.fallback instanceof UnitHealerAI)){
                    ai.fallback = new UnitHealerAI();
                }
            }
        };

        protected final float priority;

        protected Boolf<AssistantAI> predicate = ai -> false;
        protected Boolf<AssistantAI> updateVisuals = ai -> false;
        protected boolean preserveVisuals = false;

        protected Boolf<AssistantAI> updateTargetting = ai -> false;

        protected Boolf<AssistantAI> updateMovement = ai -> false;
        protected boolean preserveMovement = false;

        protected void init(AssistantAI ai){}

        protected void update(AssistantAI ai){}

        protected void dispose(AssistantAI ai){}

        protected void updateVisuals(AssistantAI ai){}

        protected void updateTargetting(AssistantAI ai){}

        protected void updateMovement(AssistantAI ai){}

        Assistance(float priority){
            this.priority = priority;
        }
    }
}
