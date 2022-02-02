package unity.util;

import arc.struct.*;
import arc.util.*;
import arc.util.Time.*;
import arc.util.pooling.*;
import unity.*;

import java.lang.reflect.*;

/**
 * Class whose whole purpose is to change how Time.run() work
 */
public class TimeReflect{
    static Field runs, delay, finish;
    static Seq<DelayRun> trueRuns;
    static Seq<DelayRun> removes = new Seq<>();

    public static void init(){
        runs = ReflectUtils.findField(Time.class, "runs", true);
        trueRuns = ReflectUtils.getField(null, runs);

        delay = ReflectUtils.findField(DelayRun.class, "delay", true);
        finish = ReflectUtils.findField(DelayRun.class, "finish", true);
    }

    public static void swapRuns(Seq<DelayRun> newRuns){
        try{
            runs.set(null, newRuns);
        }catch(Exception e){
            Unity.print(e);
        }
    }

    public static void resetRuns(){
        try{
            runs.set(null, trueRuns);
        }catch(Exception e){
            Unity.print(e);
        }
    }

    public static void updateDelays(Seq<DelayRun> runSeq){
        removes.clear();
        for(DelayRun r : runSeq){
            updateDelay(r);
        }
        runSeq.removeAll(removes);
    }

    static void updateDelay(DelayRun run){
        try{
            float time = delay.getFloat(run);
            time -= Time.delta;
            if(time <= 0f){
                Runnable r = ReflectUtils.getField(run, finish);
                r.run();
                removes.add(run);
                Pools.free(run);
            }else{
                delay.setFloat(run, time);
            }
        }catch(Exception e){
            Unity.print(e);
        }
    }
}
