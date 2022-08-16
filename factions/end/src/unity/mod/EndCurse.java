package unity.mod;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import unity.gen.entities.*;
import unity.util.*;

public class EndCurse{
    private final static IntMap<BleedEffect> bleedingEntities = new IntMap<>();
    private final static BasicPool<BleedEffect> bleedPool = new BasicPool<>(BleedEffect::new);
    private final static Seq<BleedEffect> bleedSeq = new Seq<>();

    private final static IntSet blacklist = new IntSet();
    private final static Seq<UnitQueue> endUnits = new Seq<>();
    private final static BasicPool<UnitQueue> unitPool = new BasicPool<>(UnitQueue::new);
    private static long lastFrameId;

    public static void setup(){
        Events.run(Trigger.update, EndCurse::update);
        Events.on(EventType.ResetEvent.class, e -> reset());
    }

    public static void update(){
        long id = Core.graphics.getFrameId();
        if(Vars.state.isPaused() || id == lastFrameId) return;
        lastFrameId = id;

        bleedSeq.removeAll(b -> {
            b.update();
            return b.duration <= 0f;
        });

        if(!endUnits.isEmpty()){
            endUnits.forEach(e -> {
                int ac = e.as().activeFrame();
                if(e.active == ac){
                    e.inactiveTime++;
                }else if(e.inactiveTime > 0){
                    e.inactiveTime--;
                }
                e.active = ac;

                if(e.inactiveTime >= 5){
                    e.u.add();
                }
            });
        }
    }

    public static void reset(){
        bleedSeq.clear();
        bleedingEntities.clear();
        blacklist.clear();
        endUnits.clear();
    }

    public static void addUnit(Unit unit){
        //add class caller
        //if(blacklist.add(unit.id)) endUnits.add(unit);
        if(blacklist.add(unit.id)){
            UnitQueue q = unitPool.obtain();
            q.u = unit;
            q.allAdded = true;
            q.active = q.as().activeFrame() - 1;
            q.removed = false;
            endUnits.add(q);
        }
    }

    public static boolean removeUnit(Unit unit){
        //add class caller if() return false;
        if(blacklist.contains(unit.id)){
            endUnits.remove(q -> {
                boolean t = q.u == unit;
                if(t) q.removed = true;
                return t;
            });
        }
        blacklist.remove(unit.id);

        return true;
    }

    public static void bleed(Healthc e, float duration){
        bleed(e, duration, 0f);
    }

    public static void bleed(Healthc e, float duration, float limit){
        BleedEffect b = bleedingEntities.get(e.id());
        if(b != null){
            b.duration = Math.max(b.duration, duration);
            b.limit = Mathf.lerp(b.limit, limit, 0.1f);
        }else{
            b = bleedPool.obtain();
            b.e = e;
            b.health = e.health();
            b.duration = duration;
            b.limit = limit;
            b.id = e.id();

            bleedingEntities.put(b.id, b);
            bleedSeq.add(b);
        }
    }

    private static class UnitQueue implements Poolable{
        Unit u;
        boolean allAdded = true;
        int active = 0, inactiveTime;
        boolean removed = false;

        Endc as(){
            return (Endc)u;
        }

        @Override
        public void reset(){
            u = null;
            allAdded = true;
            active = 0;
            removed = false;
            inactiveTime = 0;
        }
    }

    private static class BleedEffect implements Poolable{
        private Healthc e;
        private int id;
        private float health, duration;
        private float limit, interval;

        void update(){
            float l = interval == 0f ? limit : 0f;
            if(e instanceof Unit u){
                if(u.health > health + l) u.health = health + l;
                health = u.health;
            }else if(e instanceof Building b){
                if(b.health > health + l) b.health = health + l;
                health = b.health;
            }

            duration -= Time.delta;
            interval += Time.delta;
            if(interval >= 15f){
                interval = 0f;
            }
            if(duration <= 0f){
                bleedingEntities.remove(e.id());
                bleedPool.free(this);
            }
        }

        @Override
        public void reset(){
            e = null;
            health = 0f;
            duration = 0f;
            id = 0;
            limit = 0f;
            interval = 0f;
        }
    }
}
