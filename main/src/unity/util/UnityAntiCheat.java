package unity.util;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.world.blocks.ConstructBlock.*;
import unity.*;
import unity.entities.comp.*;
import unity.entities.units.*;
import unity.sync.*;

import java.util.*;

public class UnityAntiCheat implements ApplicationListener{
    private final Interval timer = new Interval();
    private final Seq<UnitQueue> unitSeq = new Seq<>();
    private final Seq<BuildingQueue> buildingSeq = new Seq<>();

    public void setup(){
        Events.on(BlockBuildBeginEvent.class, event -> {
            //sometimes doesnt work in sandbox mode.
            if(event.breaking && event.tile.build != null && event.unit != null && event.unit.team == event.tile.build.team){
                removeBuilding(event.tile.build);
            }
        });
        Events.on(ResetEvent.class, event -> {
            unitSeq.clear();
            buildingSeq.clear();
        });
    }

    public static void annihilateEntity(Entityc entity, boolean override){
        annihilateEntity(entity, override, false);
    }

    public static void annihilateEntity(Entityc entity, boolean override, boolean setNaN){
        Groups.all.remove(entity);
        if(entity instanceof Bossc) UnityCall.bossMusic(((Bossc)entity).type().name, false);
        if(entity instanceof Drawc) Groups.draw.remove((Drawc)entity);
        if(entity instanceof Syncc) Groups.sync.remove((Syncc)entity);
        if(entity instanceof Unit){
            Unit tmp = (Unit)entity;

            if(Unity.antiCheat != null && override) Unity.antiCheat.removeUnit(tmp);
            try{
                tmp.getClass().getField("added").setBoolean(tmp, false);
            }catch(Exception e){
                Unity.print(e);
            }
            if(tmp instanceof WormDefaultUnit){
                WormSegmentUnit nullUnit = new WormSegmentUnit();
                Arrays.fill(((WormDefaultUnit)tmp).segmentUnits, nullUnit);
            }
            if(setNaN){
                tmp.x = tmp.y = tmp.rotation = Float.NaN;
                for(WeaponMount mount : tmp.mounts){
                    mount.reload = Float.NaN;
                }
            }

            tmp.team.data().updateCount(tmp.type, -1);
            tmp.clearCommand();
            tmp.controller().removed(tmp);

            Groups.unit.remove(tmp);
            if(Vars.net.client()){
                Vars.netClient.addRemovedEntity(tmp.id);
            }

            for(WeaponMount mount : tmp.mounts){
                if(mount.bullet != null){
                    mount.bullet.time = mount.bullet.lifetime;
                    mount.bullet = null;
                }
                if(mount.sound != null){
                    mount.sound.stop();
                }
            }
        }
        if(entity instanceof Building){
            Building building = (Building)entity;
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
        }
        buildingSeq.each(b -> b.counter > 5, b -> b.build.update());
        unitSeq.each(e -> e.counter > 5, e -> e.unit.update());
    }

    public void removeBuilding(Building building){
        buildingSeq.removeAll(bq -> {
            boolean t = bq.build == building;
            if(t) bq.removed = true;
            //Unity.print(t + ":" + building.block + ":" + bq.build.block + ":" + Time.time);
            return t;
        });
    }

    public void removeUnit(Unit unit){
        unitSeq.removeAll(uq -> {
            boolean t = uq.unit == unit;
            if(t) uq.removed = true;
            return t;
        });
    }

    public void addBuilding(Building build){
        if(!buildingSeq.contains(bq -> bq.build == build)) buildingSeq.add(new BuildingQueue(build));
    }

    public void addUnit(Unit unit){
        if(!unitSeq.contains(uq -> uq.unit == unit)) unitSeq.add(new UnitQueue(unit));
    }

    boolean deconstructed(Building building){
        Building alt = building.tile.build;
        return alt instanceof ConstructBuild && alt.team == building.team;
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
