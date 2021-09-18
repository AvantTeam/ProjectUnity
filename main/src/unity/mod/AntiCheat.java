package unity.mod;

import arc.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.world.blocks.ConstructBlock.*;
import unity.*;
import unity.content.effects.*;
import unity.entities.units.*;
import unity.util.*;

import java.util.*;

public class AntiCheat{
    private final Interval timer = new Interval();
    private final Seq<UnitQueue> unitSeq = new Seq<>();
    private final Seq<BuildingQueue> buildingSeq = new Seq<>();
    private final Seq<EntitySampler> sampler = new Seq<>(), samplerTmp = new Seq<>();
    private final IntSet exclude = new IntSet(204);
    private final IntMap<EntitySampler> samplerMap = new IntMap<>(409);

    private final Seq<DisableRegenStatus> status = new Seq<>();
    private final IntMap<DisableRegenStatus> statusMap = new IntMap<>(204);

    private float lastTime = 0f;

    public void setup(){
        Triggers.listen(Trigger.update, this::update);

        Events.on(BlockBuildBeginEvent.class, event -> {
            //sometimes doesn't work in sandbox mode.
            if(event.breaking && event.tile.build != null && event.unit != null && event.unit.team == event.tile.build.team){
                removeBuilding(event.tile.build);
            }
        });

        Events.on(ResetEvent.class, event -> {
            exclude.clear();
            unitSeq.clear();
            buildingSeq.clear();
            sampler.clear();
            samplerTmp.clear();
            samplerMap.clear();
        });
    }

    public static void annihilateEntity(Entityc entity, boolean override){
        annihilateEntity(entity, override, false);
    }

    public static void annihilateEntity(Entityc entity, boolean override, boolean setNaN){
        Groups.all.remove(entity);

        if(entity instanceof Drawc draw) Groups.draw.remove(draw);
        if(entity instanceof Syncc sync) Groups.sync.remove(sync);
        if(entity instanceof Unit unit){
            if(Unity.antiCheat != null && override) Unity.antiCheat.removeUnit(unit);
            try{
                ReflectUtils.setField(unit, ReflectUtils.findField(unit.getClass(), "added", true), false);
            }catch(Exception e){
                Unity.print(e);
            }
            if(unit instanceof WormDefaultUnit){
                WormSegmentUnit nullUnit = new WormSegmentUnit();
                WormSegmentUnit[] tmpArray = Arrays.copyOf(((WormDefaultUnit)unit).segmentUnits, ((WormDefaultUnit)unit).segmentUnits.length);
                Arrays.fill(((WormDefaultUnit)unit).segmentUnits, nullUnit);
                for(WormSegmentUnit segmentUnit : tmpArray){
                    if(segmentUnit != null) segmentUnit.remove();
                }
            }
            if(setNaN){
                unit.x = unit.y = unit.rotation = Float.NaN;
                for(WeaponMount mount : unit.mounts){
                    mount.reload = Float.NaN;
                }
            }

            unit.team.data().updateCount(unit.type, -1);
            unit.clearCommand();
            unit.controller().removed(unit);

            Groups.unit.remove(unit);
            if(Vars.net.client()){
                Vars.netClient.addRemovedEntity(unit.id);
            }

            for(WeaponMount mount : unit.mounts){
                if(mount.bullet != null){
                    mount.bullet.time = mount.bullet.lifetime;
                    mount.bullet = null;
                }
                if(mount.sound != null){
                    mount.sound.stop();
                }
            }
        }
        if(entity instanceof Building building){
            Groups.build.remove(building);
            building.tile.remove();
            if(Unity.antiCheat != null && override) Unity.antiCheat.removeBuilding(building);
            if(setNaN){
                building.x = building.y = Float.NaN;
            }

            if(building.sound != null) building.sound.stop();
            building.added = false;
        }
    }

    void update(){
        if(Vars.state.isPaused()) return;
        if(timer.get(15f) && (!unitSeq.isEmpty() || !buildingSeq.isEmpty())){
            for(Entityc e : Groups.all){
                if(e instanceof Unit){
                    for(UnitQueue u : unitSeq){
                        if(e == u.unit){
                            u.allAdded = true;
                            u.counter--;
                        }
                        if(u.removed) unitSeq.remove(u);
                    }
                }else if(e instanceof Building){
                    for(BuildingQueue b : buildingSeq){
                        if(e == b.build){
                            b.allAdded = true;
                            b.counter--;
                        }
                        if(b.removed) buildingSeq.remove(b);
                    }
                }
            }
            unitSeq.each(u -> {
                if(!u.allAdded && !u.removed) u.unit.add();
                u.allAdded = false;
                u.counter++;
            });
            buildingSeq.each(b -> {
                if(deconstructed(b.build)){
                    removeBuilding(b.build);
                    return;
                }
                if(!b.allAdded && !b.removed){
                    b.build.tile.setBlock(b.build.block, b.build.team, b.build.rotation, () -> b.build);
                }
                b.allAdded = false;
                b.counter++;
            });
            sampler.each(es -> {
                if(es.duration <= 0f && es.excludeDuration <= 0f){
                    samplerTmp.add(es);
                    samplerMap.remove(es.entity.id());
                    Pools.free(es);
                }
                es.excludeDuration -= 15f;
                es.duration -= 15f;
            });
            sampler.removeAll(samplerTmp);
            samplerTmp.clear();
        }
        //something is updating this multiple times.
        if(Time.time > lastTime){
            buildingSeq.each(b -> b.counter > 10, b -> b.build.update());
            unitSeq.each(e -> e.counter > 10, e -> e.unit.update());
            for(DisableRegenStatus s : status){
                s.update();
                if(s.duration <= 0f || !s.unit.isValid()){
                    status.remove(s);
                    statusMap.remove(s.unit.id);
                    Pools.free(s);
                }
            }
            lastTime = Time.time;
        }
    }

    public void notifyDamage(int unitId, float delta){
        if(delta > 0) return;
        DisableRegenStatus status = statusMap.get(unitId);
        if(status != null){
            status.lastHealth += delta;
        }
    }

    public void applyStatus(Unit unit, float duration){
        if(exclude.contains(unit.id)) return;
        DisableRegenStatus status = statusMap.get(unit.id);

        if(status != null){
            status.duration = Math.max(status.duration, duration);
        }else{
            DisableRegenStatus s = Pools.obtain(DisableRegenStatus.class, DisableRegenStatus::new);
            s.unit = unit;
            s.lastHealth = unit.health;
            s.duration = duration;
            this.status.add(s);
            statusMap.put(unit.id, s);
        }
    }

    public void samplerAdd(Healthc entity){
        samplerAdd(entity, false);
    }

    public void samplerAdd(Healthc entity, boolean verified){
        if(!verified){
            if(exclude.contains(entity.id())) return;
            EntitySampler ent;
            if((ent = samplerMap.get(entity.id())) != null){
                if(entity.health() >= ent.lastHealth && ent.excludeDuration <= 0f){
                    ent.duration = Math.max(30f, ent.duration);
                    if(ent.penalty++ >= 5){
                        annihilateEntity(entity, false);
                        samplerMap.remove(entity.id());
                        sampler.remove(ent);
                    }
                }
                return;
            }
            EntitySampler s = Pools.obtain(EntitySampler.class, EntitySampler::new);
            s.entity = entity;
            s.duration = 2f * 60f;
            s.lastHealth = entity.health();
            sampler.add(s);
            samplerMap.put(entity.id(), s);
        }else{
            EntitySampler ent = samplerMap.get(entity.id());
            if(ent != null){
                ent.excludeDuration = 2 * 60f;
            }else{
                //EntitySampler s = new EntitySampler(entity);
                EntitySampler s = Pools.obtain(EntitySampler.class, EntitySampler::new);
                s.entity = entity;
                s.excludeDuration = 2 * 60f;
                s.duration = 0f;
                sampler.add(s);
                samplerMap.put(entity.id(), s);
            }
        }
    }

    public void removeBuilding(Building building){
        exclude.remove(building.id);
        buildingSeq.removeAll(bq -> {
            boolean t = bq.build == building;
            if(t) bq.removed = true;
            return t;
        });
    }

    public void removeUnit(Unit unit){
        exclude.remove(unit.id);
        unitSeq.removeAll(uq -> {
            boolean t = uq.unit == unit;
            if(t) uq.removed = true;
            return t;
        });
    }

    public void addBuilding(Building build){
        if(!buildingSeq.contains(bq -> bq.build == build)){
            buildingSeq.add(new BuildingQueue(build));
            exclude.add(build.id);
        }
    }

    public void addUnit(Unit unit){
        if(!unitSeq.contains(uq -> uq.unit == unit)){
            unitSeq.add(new UnitQueue(unit));
            exclude.add(unit.id);
        }
    }

    boolean deconstructed(Building building){
        Building alt = building.tile.build;
        return alt instanceof ConstructBuild && alt.team == building.team;
    }

    static class EntitySampler implements Poolable{
        Healthc entity;
        float duration, excludeDuration = 0f, lastHealth;
        int penalty = 0;

        EntitySampler(){

        }

        @Override
        public void reset(){
            entity = null;
            duration = excludeDuration = lastHealth = 0f;
            penalty = 0;
        }
    }

    static class UnitQueue{
        Unit unit;
        boolean allAdded = true;
        int counter = 0;
        boolean removed = false;

        UnitQueue(Unit unit){
            this.unit = unit;
        }
    }

    static class BuildingQueue{
        Building build;
        boolean allAdded = true;
        int counter = 0;
        boolean removed = false;

        BuildingQueue(Building build){
            this.build = build;
        }
    }

    static class DisableRegenStatus implements Poolable{
        Unit unit;
        float lastHealth;
        float duration;

        void update(){
            if(unit.health == Float.POSITIVE_INFINITY || Float.isNaN(unit.health)){
                unit.health = unit.maxHealth == Float.POSITIVE_INFINITY || Float.isNaN(unit.maxHealth) ? 800000f : unit.maxHealth;
            }
            float delta = unit.health - lastHealth;
            if(delta > 0){
                unit.health -= delta;
            }

            if(Mathf.chanceDelta(0.19f)){
                Tmp.v1.rnd(Mathf.range(unit.type.hitSize / 2f));
                ParticleFx.endRegenDisable.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
            }

            lastHealth = unit.health;
            duration -= Time.delta;
        }

        @Override
        public void reset(){
            unit = null;
            lastHealth = 0f;
            duration = 0f;
        }
    }
}
