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
    protected static IntMap<Seq<Unit>> hooks = new IntMap<>();

    protected Teamc user;

    protected final Seq<Assistance> services;
    protected final Interval timer = new Interval(2);
    protected Assistance current;

    public AssistantAI(Assistance... services){
        this.services = Seq.with(services).sort(Assistance::ordinal);
    }

    @Override
    public void updateUnit(){
        updateAssistance();
        updateVisuals();
        updateTargeting();
        updateMovement();
    }

    public void updateAssistance(){
        if(timer.get(0, 30f)){
            for(Assistance service : services){
                if(service.predicate.get(this)){
                    if(current != service){
                        current = service;
                        current.init(this);
                    }

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
        ) && timer.get(1, 30f)){
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
                super.updateVisuals();
            }
            current.updateVisuals(this);
        }else{
            super.updateVisuals();
        }
    }

    @Override
    protected void updateTargeting(){
        if(current != null && current.updateTargetting.get(this)){
            if(current.preserveTargetting){
                super.updateTargeting();
            }
            current.updateTargetting(this);
        }else{
            super.updateTargeting();
        }
    }

    @Override
    public void updateMovement(){
        if(current != null && current.updateMovement.get(this)){
            if(current.preserveMovement){
                circle(user, unit.type.range * 0.8f);
            }
            current.updateMovement(this);
        }else if(user != null){
            circle(user, unit.type.range * 0.8f);
        }
    }

    public void updateUser(){
        Teamc next = null;
        int prev = Integer.MAX_VALUE;

        for(Player player : Groups.player){
            Seq<Unit> assists = hooks.get(player.id, Seq::new);
            if(assists.size < prev){
                next = player.unit();
                prev = assists.size;
            }
        }

        if(next == null) next = targetFlag(unit.x, unit.y, BlockFlag.core, false);
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

    /** Assistant service types. <b>Uses {@code ordinal} for sorting.</b> */
    public enum Assistance{
        protectCore(ai -> state.teams.cores(ai.unit.team).contains(tile -> tile.health() < tile.maxHealth())){
            CoreBuild tile;

            {
                updateMovement = predicate;

                preserveTargetting = true;
                updateVisuals = updateTargetting = ai ->
                    ai.unit.type.canHeal &&
                    tile != null &&
                    ai.unit.dst(tile) <= ai.unit.type.range;
            }

            @Override
            protected void init(AssistantAI ai){
                ai.displayMessage(Core.bundle.get("service.coreattack"));
            }

            @Override
            protected void update(AssistantAI ai){
                tile = state.teams.cores(ai.unit.team).min(b -> b.health() < b.maxHealth(), b -> ai.unit.dst2(b));
            }

            @Override
            protected void updateVisuals(AssistantAI ai){
                if(tile != null){
                    ai.unit.lookAt(tile);
                }
            }

            @Override
            protected void updateMovement(AssistantAI ai){
                if(tile != null){
                    ai.circle(tile, ai.unit.type.range * 0.8f, ai.unit.speed() * 0.8f);
                }
            }

            @Override
            protected void updateTargetting(AssistantAI ai){
                WeaponMount mount = Structs.find(ai.unit.mounts, m -> m.weapon.bullet.healPercent > 0f);
                if(mount != null){
                    Weapon weapon = mount.weapon;
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

        protected final Boolf<AssistantAI> predicate;
        protected Boolf<AssistantAI> updateVisuals = ai -> false;
        protected boolean preserveVisuals = false;

        protected Boolf<AssistantAI> updateTargetting = ai -> false;
        protected boolean preserveTargetting = false;

        protected Boolf<AssistantAI> updateMovement = ai -> false;
        protected boolean preserveMovement = false;

        Assistance(Boolf<AssistantAI> predicate){
            this.predicate = predicate;
        }

        protected void init(AssistantAI ai){}

        protected void update(AssistantAI ai){}

        protected void updateVisuals(AssistantAI ai){}

        protected void updateTargetting(AssistantAI ai){}

        protected void updateMovement(AssistantAI ai){}
    }
}
