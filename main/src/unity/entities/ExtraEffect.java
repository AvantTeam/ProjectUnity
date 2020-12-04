package unity.entities;

import arc.Events;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.content.Liquids;
import mindustry.entities.Puddles;
import mindustry.game.EventType;
import mindustry.gen.*;
import unity.entities.effects.VapourizeEffectState;

public class ExtraEffect{
    private static final Seq<BuildQueue> vapourizeQueue = new Seq<>(512);

    static{
        Events.on(EventType.WorldLoadEvent.class, e -> {
            vapourizeQueue.clear();
        });
        Events.run(EventType.Trigger.update, () -> {
            vapourizeQueue.each(buildq -> {
                Building temp = buildq.build;
                if(!temp.isValid()){
                    int size = temp.block.size;
                    Puddles.deposit(temp.tile, Liquids.slag, size * size * 2 + 6);
                }
                buildq.time -= Time.delta;
            });
            vapourizeQueue.removeAll(buildq -> buildq.build.dead || buildq.time <= 0f);
        });
    }

    public static void addMoltenBlock(Building build){
        BuildQueue temp = vapourizeQueue.find(bq -> bq.build == build);
        if(temp == null) vapourizeQueue.add(new BuildQueue(build));
        else temp.time = 14.99f;
    }

    public static void createEvaporation(float x, float y, Unit host, Entityc influence){
        if(host == null || influence == null) return;
        new VapourizeEffectState(x, y, host, influence).add();
    }

    //TODO separate this or not?
    static class BuildQueue{
        Building build;
        float time;

        public BuildQueue(Building build, float time){
            this.build = build;
            this.time = time;
        }

        public BuildQueue(Building build){
            this(build, 14.99f);
        }
    }
}
