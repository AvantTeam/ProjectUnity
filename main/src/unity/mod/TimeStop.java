package unity.mod;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.type.*;
import unity.gen.*;
import unity.util.*;

public class TimeStop{
    private final static float lerpTime = 20f, error = 0.000002f * lerpTime;
    private final static float slowDownTime = 30f, continueTimeDuration = 89f;
    private final static Seq<TimeStopEntity> entities = new Seq<>();
    private final static IntMap<TimeStopEntity> map = new IntMap<>(102);
    private final static BasicPool<TimeStopEntity> pool = new BasicPool<>(8, 200, TimeStopEntity::new);
    private final static Vec2 movement = new Vec2();

    private static float time = 0f, lastTime = 0f;
    private static boolean set = false;
    private static boolean reseting = false;
    private static Sound continueTimeSound;

    private final static Floatp defaultDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60f, 3f);
    private final static Floatp timeStopDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60f * Mathf.sqrt(time / lerpTime), 3f);
    private final static Floatp stoppedTimeDelta = () -> Math.min(Core.graphics.getDeltaTime() * 60f * Mathf.sqrt(1f - (time / lerpTime)), 3f);

    public static void init(){
        Events.run(Trigger.update, TimeStop::update);
        Events.on(EventType.ResetEvent.class, event -> reset());
    }

    public static boolean inTimeStop(){
        return set || !entities.isEmpty();
    }

    public static boolean contains(Entityc entity){
        return map.containsKey(entity.id());
    }

    public static float getTime(Entityc entity){
        TimeStopEntity e = map.get(entity.id());

        if(e == null){
            return 0f;
        }else{
            return e.time;
        }
    }

    public static void addEntity(Entityc entity, float time){
        if(!map.containsKey(entity.id())){
            TimeStopEntity te = pool.obtain();
            te.entity = entity;
            te.time = time;
            te.id = entity.id();
            te.fakeTime = Time.time;
            map.put(entity.id(), te);
            entities.add(te);
        }else{
            TimeStopEntity te = map.get(entity.id());
            te.time = Math.max(te.time, time);
        }
    }

    static void draw(){
        if(time > 0.0001f && (Vars.player.unit() == null || !map.containsKey(Vars.player.unit().id))){
            float z = Draw.z();
            Draw.z(Layer.space + 1f);
            Draw.color(Color.black);
            Draw.rect();
            Draw.color();
            Draw.z(z);
        }
    }

    static void reset(){
        entities.clear();
        map.clear();
        time = lastTime = 0f;
        set = reseting = false;
    }

    static void update(){
        if(continueTimeSound == null) continueTimeSound = UnitySounds.continueTime;
        if(!Vars.state.isGame() || Vars.state.isPaused()) return;
        float tDelta = defaultDelta.get();
        time = Mathf.approach(time, reseting ? lerpTime - error : 0f, tDelta);

        if(!set && !entities.isEmpty()){
            set = true;
            reseting = true;
            lastTime = Time.time;
            Time.setDeltaProvider(stoppedTimeDelta);
        }
        if(set && !entities.isEmpty()){
            float lastDelta = Time.delta;
            lastTime += Time.delta;

            float delta = timeStopDelta.get();
            reseting = false;
            entities.removeAll(te -> {
                float lastT = te.time;
                te.time -= tDelta;
                boolean valid = te.entity.isAdded() && te.entity.id() == te.id;

                if(valid){
                    float d = delta * Mathf.clamp(te.time / slowDownTime);
                    boolean isPlayer = te.entity instanceof Unit && ((Unit)te.entity).controller() == Vars.player;
                    te.fakeTime += d;
                    Time.delta = d;
                    //TODO fix Intervals
                    Time.time = te.fakeTime;

                    if(isPlayer){
                        Unit u = (Unit)te.entity;
                        if(Vars.mobile){
                            updateMovementMobile(u);
                        }else{
                            updateMovementDesktop(u);
                        }
                    }

                    te.entity.update();

                    if(isPlayer){
                        Position p = (Position)te.entity;
                        Core.camera.position.set(p);
                        if(te.time < continueTimeDuration && lastT >= continueTimeDuration){
                            continueTimeSound.at(p);
                        }
                    }
                }

                if(te.time > slowDownTime && valid) reseting = true;
                if(te.time <= 0f || !valid){
                    pool.free(te);
                    map.remove(te.id);
                }
                return te.time <= 0f || !valid;
            });
            Time.delta = lastDelta;
            Time.time = lastTime;
        }
        if(entities.isEmpty() && set){
            set = false;
            Time.time = lastTime;
            Time.setDeltaProvider(defaultDelta);
        }
    }

    static void updateMovementMobile(Unit unit){
        UnitType type = unit.type;
        if(type == null) return;

        Tmp.v1.set(Core.camera.position);
        float attractDst = 15f;

        float speed = unit.speed();

        movement.set(Tmp.v1).sub(Vars.player).limit(speed);
        movement.setAngle(Mathf.slerp(movement.angle(), unit.vel.angle(), 0.05f));

        if(Vars.player.within(Tmp.v1, attractDst)){
            movement.setZero();
            unit.vel.approachDelta(Vec2.ZERO, unit.speed() * type.accel / 2f);
        }

        unit.movePref(movement);
    }

    static void updateMovementDesktop(Unit unit){
        boolean omni = unit.type.omniMovement;

        float speed = unit.speed();
        float xa = Core.input.axis(Binding.move_x);
        float ya = Core.input.axis(Binding.move_y);
        boolean boosted = (unit instanceof Mechc && unit.isFlying());

        movement.set(xa, ya).nor().scl(speed);
        if(Core.input.keyDown(Binding.mouse_move)){
            movement.add(Core.input.mouseWorld().sub(Vars.player).scl(1f / 25f * speed)).limit(speed);
        }

        float mouseAngle = Angles.mouseAngle(unit.x, unit.y);
        boolean aimCursor = omni && Vars.player.shooting && unit.type.hasWeapons() && unit.type.faceTarget && !boosted && unit.type.rotateShooting;

        if(aimCursor){
            unit.lookAt(mouseAngle);
        }else{
            unit.lookAt(unit.prefRotation());
        }

        unit.movePref(movement);
    }

    static class TimeStopEntity{
        Entityc entity;
        float time, fakeTime;
        int id;
    }
}
