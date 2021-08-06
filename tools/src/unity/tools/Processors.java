package unity.tools;

import arc.util.*;
import unity.tools.proc.*;

import java.util.concurrent.*;

import static unity.Unity.*;

public final class Processors{
    private static final Processor[] processes = {
        new OutlineRegionProcessor(),
        new UnitProcessor()
    };

    private Processors(){}

    public static void process(){
        for(var process : processes){
            Time.mark();

            var exec = Executors.newCachedThreadPool();

            process.process(exec);
            exec.shutdown();

            try{
                if(!exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) throw new IllegalStateException("Very strange things happened.");
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }

            print(process.getClass().getSimpleName() + " executed for " + Time.elapsed() + "ms");
        }
    }
}
