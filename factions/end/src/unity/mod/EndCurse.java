package unity.mod;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;

public class EndCurse{
    private final static IntMap<BleedEffect> bleedingEntities = new IntMap<>();
    private final static BasicPool<BleedEffect> bleedPool = new BasicPool<>(BleedEffect::new);
    private final static Seq<BleedEffect> bleedSeq = new Seq<>();

    private static float interval = 0f;

    private final static IntSet blacklist = new IntSet();
    private final static Seq<UnitQueue> endUnits = new Seq<>();
    private final static BasicPool<UnitQueue> unitPool = new BasicPool<>(UnitQueue::new);

    public static void setup(){
        Events.run(Trigger.update, EndCurse::update);
        Events.on(EventType.ResetEvent.class, e -> reset());
    }

    public static void update(){
        if(Vars.state.isPaused()) return;

        bleedSeq.removeAll(b -> {
            b.update();
            if(b.duration <= 0f) bleedingEntities.remove(b.id);
            return b.duration <= 0f;
        });

        if((interval += Time.delta) >= 15f && !endUnits.isEmpty()){
            interval = 0f;
        }
    }

    public static void reset(){
        bleedSeq.clear();
        bleedingEntities.clear();
    }

    public static void addUnit(Unit unit){
        //add class caller
        //if(blacklist.add(unit.id)) endUnits.add(unit);
        if(blacklist.add(unit.id)){
            UnitQueue q = unitPool.obtain();
            q.u = unit;
            q.allAdded = true;
            q.counter = 0;
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
        BleedEffect b = bleedingEntities.get(e.id());
        if(b != null){
            b.duration = Math.max(b.duration, duration);
        }else{
            b = bleedPool.obtain();
            b.e = e;
            b.health = e.health();
            b.duration = duration;
            b.id = e.id();

            bleedingEntities.put(b.id, b);
            bleedSeq.add(b);
        }
    }

    private static class UnitQueue implements Poolable{
        Unit u;
        boolean allAdded = true;
        int counter = 0;
        boolean removed = false;

        @Override
        public void reset(){
            u = null;
            allAdded = true;
            counter = 0;
            removed = false;
        }
    }

    private static class BleedEffect implements Poolable{
        private Healthc e;
        private int id;
        private float health, duration;

        void update(){
            if(e instanceof Unit u){
                if(u.health > health) u.health = health;
                else health = u.health;
            }else if(e instanceof Building b){
                if(b.health > health) b.health = health;
                else health = b.health;
            }

            duration -= Time.delta;
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
        }
    }
}
