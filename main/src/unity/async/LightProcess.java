package unity.async;

import arc.struct.*;
import mindustry.async.*;
import mindustry.gen.*;
import unity.gen.*;

import static mindustry.Vars.*;

/**
 * An asynchronous task manager for processing {@link Light} ray-casting. First, it collects all lights synchronously,
 * which won't be done if it's still processing, then the collected lights will do their ray-casting in a separate
 * thread, whose finalization will be done again synchronously.
 * @author GlennFolker
 */
public class LightProcess implements AsyncProcess{
    private volatile boolean processing = false;
    private volatile boolean ending = false;

    private final Seq<Light> toProcess = new Seq<>();
    private final Seq<Runnable> toRun = new Seq<>();

    @Override
    public void init(){}

    @Override
    public void reset(){}

    @Override
    public void begin(){
        if(shouldProcess()){
            toProcess.clear();
            for(var e : Groups.draw){
                if(e instanceof Light light){
                    toProcess.add(light);
                }
            }
        }
    }

    @Override
    public void process(){
        processing = true;
        toProcess.each(l -> l.walk(toRun));

        processing = false;
        ending = true;
    }

    @Override
    public void end(){
        if(ending){
            var it = toRun.iterator();
            while(it.hasNext()){
                it.next().run();
                it.remove();
            }

            ending = false;
        }
    }

    @Override
    public boolean shouldProcess(){
        return !processing && !state.isPaused();
    }
}
