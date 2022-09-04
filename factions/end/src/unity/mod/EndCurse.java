package unity.mod;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import unity.gen.entities.*;
import unity.util.*;

import java.lang.reflect.*;

public class EndCurse{
    private final static IntMap<BleedEffect> bleedingEntities = new IntMap<>();
    private final static BasicPool<BleedEffect> bleedPool = new BasicPool<>(BleedEffect::new);
    private final static Seq<BleedEffect> bleedSeq = new Seq<>();

    private final static IntSet blacklist = new IntSet(204);
    private final static Seq<UnitQueue> endUnits = new Seq<>();
    private final static BasicPool<UnitQueue> unitPool = new BasicPool<>(UnitQueue::new);
    private static long lastFrameId;

    private final static ObjectIntMap<Content> removeLevel = new ObjectIntMap<>(204);
    private final static Seq<Entityc> erased = new Seq<>(255), tmp = new Seq<>();
    private final static IntSet activeEntities = new IntSet(511);

    private final static IntMap<EntitySampler> damageSampler = new IntMap<>(204);
    private final static BasicPool<EntitySampler> samplerPool = new BasicPool<>(EntitySampler::new);
    private final static Seq<EntitySampler> samplerSeq = new Seq<>();

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

        if(!erased.isEmpty()){
            activeEntities.clear();
            for(Entityc e : Groups.all){
                activeEntities.add(e.id());
            }

            erased.removeAll(e -> {
                if(activeEntities.contains(e.id())){
                    Content t = getType(e);
                    removeLevel.put(t, removeLevel.get(t, 0) + 1);
                    erase(e);
                    return false;
                }
                return true;
            });
        }

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
        if(!samplerSeq.isEmpty()){
            samplerSeq.removeAll(s -> {
                s.duration -= Time.delta;
                if(s.duration <= 0f){
                    damageSampler.remove(s.entity.id());
                    samplerPool.free(s);
                    return true;
                }
                return false;
            });
        }
    }

    public static void reset(){
        bleedSeq.clear();
        bleedingEntities.clear();
        blacklist.clear();
        endUnits.clear();
    }

    private static Content getType(Entityc e){
        if(e instanceof Unit u){
            return u.type;
        }else if(e instanceof Building b){
            return b.block;
        }

        return null;
    }

    private static void eraseBuilding(Building b){
        if(b.tile != Vars.emptyTile){
            b.tile.remove();
        }
    }

    private static void eraseUnit(Unit u){
        if(Vars.net.client()){
            Vars.netClient.addRemovedEntity(u.id);
        }
        u.team.data().updateCount(u.type, -1);
        u.controller().removed(u);

        for(WeaponMount mount : u.mounts){
            if(mount.bullet != null && mount.bullet.owner == u){
                mount.bullet.remove();
                mount.bullet = null;
            }
            if(mount.sound != null){
                mount.sound.stop();
            }
        }
    }

    private static void eraseLevel(Entityc e, int level){
        /*
        if(level >= 2){
            //purgatory
        }
        */
        if(level >= 3){
            if(e instanceof Unit u){
                u.x = Float.NaN;
                u.y = Float.NaN;
                u.rotation = Float.NaN;

                for(WeaponMount m : u.mounts){
                    m.reload = m.rotation = Float.NaN;
                }
            }else if(e instanceof Building b){
                b.x = Float.NaN;
                b.y = Float.NaN;
            }
        }
        if(level >= 4){
            Class<?> c = ReflectUtils.known(e.getClass());
            try{
                for(Field f : c.getFields()){
                    if(f.getType().isAssignableFrom(Float.class)){
                        f.setAccessible(true);
                        f.setFloat(e, Float.NaN);
                    }
                }
            }catch(Exception ex){
                Log.err(ex);
            }
        }
    }

    public static void erase(Entityc e){
        if(blacklist.contains(e.id())) return;

        EntityGroup<?> group = null;
        Content type = null;
        int lastSize = 0;

        if(e instanceof Unit u){
            group = Groups.unit;
            type = u.type;
        }else if(e instanceof Building b){
            group = Groups.build;
            type = b.block;
        }

        if(group != null){
            lastSize = group.size();
        }

        int level;
        if((level = removeLevel.get(type, 0)) > 0){
            Groups.all.remove(e);

            try{
                ReflectUtils.set(e, "added", false);
            }catch(Exception ex){
                Log.err(ex);
            }

            if(e instanceof Drawc draw) Groups.draw.remove(draw);
            if(e instanceof Syncc sync) Groups.sync.remove(sync);
            if(group != null) group.remove(e.as());

            if(e instanceof Unit) eraseUnit((Unit)e);
            else if(e instanceof Building) eraseBuilding((Building)e);

            if(level >= 2) eraseLevel(e, level);

            return;
        }

        e.remove();
        erased.add(e);

        if(group != null && group.size() >= lastSize){
            tmp.clear();
            int size = group.size() - 1;
            int limit = Math.max(0, size - 100);
            Entityc e2;
            while(size >= limit && ((getType(e2 = group.index(size))) == type)){
                tmp.add(e2);
                size--;
            }
            //removeLevel.put(type, removeLevel.get(type, 0) + 1);
            if(tmp.size > 0){
                removeLevel.put(type, Math.max(removeLevel.get(type, 0), 1));

                //erase(e);
                for(Entityc e3 : tmp){
                    erase(e3);
                }

                tmp.clear();
            }
        }
        /*
        Groups.all.remove(entity);

        if(entity instanceof Drawc draw) Groups.draw.remove(draw);
        if(entity instanceof Syncc sync) Groups.sync.remove(sync);
         */
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

    public static void removeUnit(Unit unit){
        //add class caller if() return false;
        if(blacklist.contains(unit.id)){
            endUnits.remove(q -> {
                if(q.u == unit){
                    q.removed = true;
                    unitPool.free(q);
                    blacklist.remove(unit.id);
                    return true;
                }
                return false;
            });
        }
        blacklist.remove(unit.id);
    }

    public static void bleed(Healthc e, float duration){
        bleed(e, duration, 0f);
    }

    public static void bleed(Healthc e, float duration, float limit){
        BleedEffect b = bleedingEntities.get(e.id());
        if(b != null){
            b.duration = Math.max(b.duration, duration);
            b.updateLimit(limit);
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

    public static void bleedDamage(int id, float health){
        BleedEffect b = bleedingEntities.get(id);
        if(b != null && health < b.health){
            b.health = health;
        }
    }

    public static boolean contains(int id){
        return damageSampler.containsKey(id);
    }

    public static void notifyDamage(Healthc e, float damage){
        if(!blacklist.contains(e.id())) return;
        EntitySampler s = damageSampler.get(e.id());
        if(s != null){
            s.updateDamage(damage);
            s.duration = 3f * 60f;
            if(s.weight > 30 && s.damage <= damage / 1000f){
                erase(s.entity);
                s.duration = 0f;
            }
        }else{
            s = samplerPool.obtain();
            s.entity = e;
            s.updateDamage(damage);
            s.duration = 3f * 60f;
            damageSampler.put(e.id(), s);
            samplerSeq.add(s);
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

    private static class EntitySampler implements Poolable{
        Healthc entity;
        float duration, damage;
        int weight = 0;

        void updateDamage(float amount){
            float ratio = 1f / ++weight;
            damage = (damage * (1f - ratio)) + (amount * ratio);
        }

        @Override
        public void reset(){
            entity = null;
            duration = damage = 0f;
            weight = 0;
        }
    }

    private static class BleedEffect implements Poolable{
        private Healthc e;
        private int id, weight;
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

        void updateLimit(float limit){
            weight++;
            float w = (1f / (1f + weight));
            this.limit = (this.limit * (1f - w)) + limit * w;
        }

        @Override
        public void reset(){
            e = null;
            health = 0f;
            duration = 0f;
            id = 0;
            weight = 0;
            limit = 0f;
            interval = 0f;
        }
    }
}
