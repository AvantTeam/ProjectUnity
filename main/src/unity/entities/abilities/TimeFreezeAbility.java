package unity.entities.abilities;

import arc.*;
import arc.func.*;
import arc.input.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.abilities.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.input.*;
import unity.*;

public class TimeFreezeAbility extends Ability{
    protected static final Vec2 movement = new Vec2();
    protected static boolean paused = false;
    protected static float pausedDelta = 1f;
    protected static final Floatp unityDeltaImpl = () -> Math.min(paused ? 0f : Core.graphics.getDeltaTime() * 60f, 3f);
    protected static final Floatp replDeltaImpl = () -> Math.min(Core.graphics.getDeltaTime() * 60f, 3f);

    public float duration = 5f * 60f;
    public float reloadTime = 10f * 60f;
    protected float time = 0f;
    protected float rtime = 0f;
    protected boolean triggered = false;

    static{
        Events.run(Trigger.update, () -> pausedDelta = replDeltaImpl.get());
    }

    @Override
    public void update(Unit unit){
        //TODO sync
        if(rtime >= reloadTime){
            if(!paused && !triggered && !Vars.headless && (Core.input.keyDown(KeyCode.x) && unit.controller() == Vars.player)){
                Unity.print("Za warudo");
                paused = true;
                triggered = true;
                Time.setDeltaProvider(unityDeltaImpl);
            }
        }else{
            rtime += pausedDelta;
        }

        if(triggered){
            if(time >= duration){
                paused = false;
                triggered = false;
                rtime = time = 0f;
            }else{
                if(unit.controller() instanceof Player) updatePlayerUnit(unit);
                time += pausedDelta;
            }
        }
    }

    private void updatePlayerUnit(Unit unit){
        boolean omni = unit.type.omniMovement;

        float speed = unit.realSpeed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean boosted = (unit instanceof Mechc && unit.isFlying());

        movement.set(xa, ya).nor().scl(speed);
        if(Core.input.keyDown(Binding.mouse_move)){
            movement.add(Core.input.mouseWorld().sub(Vars.player).scl(1f / 25f * speed)).limit(speed);
        }

        if(omni){
            Tmp.v1.set(movement).scl(unit.floorSpeedMultiplier()).sub(unit.vel).limit(unit.type.accel * movement.len() * pausedDelta);
            unit.vel.add(Tmp.v1);
        }else{
            Tmp.v1.set(movement).scl(unit.floorSpeedMultiplier()).sub(unit.vel).limit(unit.type.accel * movement.len() * pausedDelta);
            unit.vel.add(Tmp.v2.trns(unit.rotation, movement.len()));
            if(!movement.isZero()){
                unit.vel.rotateTo(movement.angle(), unit.type.rotateSpeed * Math.max(pausedDelta, 1));
            }
        }

        updateVelocity(unit);

        float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && Vars.player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted && unit.type.rotateShooting;

        if(aimCursor){
            //unit.lookAt(mouseAngle);
            unit.rotation = Angles.moveToward(unit.rotation, mouseAngle, unit.type.rotateSpeed * pausedDelta * unit.speedMultiplier());
        }else{
            //unit.lookAt(unit.prefRotation());
            unit.rotation = Angles.moveToward(unit.rotation, unit.prefRotation(), unit.type.rotateSpeed * pausedDelta * unit.speedMultiplier());
        }

        unit.aim(unit.type.faceTarget ? Core.input.mouseWorld() : Tmp.v1.trns(unit.rotation, Core.input.mouseWorld().dst(unit)).add(unit.x, unit.y));
        unit.controlWeapons(true, Vars.player.shooting && !boosted);

        Vars.player.boosting = Core.input.keyDown(Binding.boost) && !movement.isZero();
        Vars.player.mouseX = unit.aimX();
        Vars.player.mouseY = unit.aimY();

        for(WeaponMount mount : unit.mounts){
            mount.reload = Math.max(mount.reload - pausedDelta * unit.reloadMultiplier(), 0);
        }
    }

    private void updateVelocity(Unit unit){
        unit.move(unit.vel.x * pausedDelta, unit.vel.y * pausedDelta);
        unit.vel.scl(Mathf.clamp(1f - (unit.drag * pausedDelta)));
    }
}
