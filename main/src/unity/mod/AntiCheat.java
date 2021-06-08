package unity.mod;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.world.blocks.ConstructBlock.*;
import unity.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.sync.*;
import unity.util.*;

import java.util.*;

public class AntiCheat implements ApplicationListener{
    private final Interval timer = new Interval();
    private final Seq<UnitQueue> unitSeq = new Seq<>();
    private final Seq<BuildingQueue> buildingSeq = new Seq<>();
    private final Seq<EntitySampler> sampler = new Seq<>(), samplerTmp = new Seq<>();
    private final IntSet exclude = new IntSet(204);
    private final IntMap<EntitySampler> samplerMap = new IntMap<>(409);

    public void setup(){
        Events.on(BlockBuildBeginEvent.class, event -> {
            //sometimes doesnt work in sandbox mode.
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

        if(entity instanceof Bossc boss) UnityCall.bossMusic(boss.type().name, false);
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

    @Override
    public void update(){
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
                }
                es.excludeDuration -= 15f;
                es.duration -= 15f;
            });
            sampler.removeAll(samplerTmp);
            samplerTmp.clear();
        }
        buildingSeq.each(b -> b.counter > 5, b -> b.build.update());
        unitSeq.each(e -> e.counter > 5, e -> e.unit.update());
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
            EntitySampler s = new EntitySampler(entity);
            s.duration = 2f * 60f;
            s.lastHealth = entity.health();
            sampler.add(s);
            samplerMap.put(entity.id(), s);
        }else{
            EntitySampler ent = samplerMap.get(entity.id());
            if(ent != null){
                ent.excludeDuration = 2 * 60f;
            }else{
                EntitySampler s = new EntitySampler(entity);
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

    static class EntitySampler{
        Healthc entity;
        float duration, excludeDuration = 0f, lastHealth;
        int penalty = 0;

        EntitySampler(Healthc entity){
            this.entity = entity;
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
}
