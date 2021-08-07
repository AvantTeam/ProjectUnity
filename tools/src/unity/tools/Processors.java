package unity.tools;

import arc.util.*;
import arc.util.async.*;
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
            Threads.await(exec);

            process.finish();
            print(process.getClass().getSimpleName() + " executed for " + Time.elapsed() + "ms");
        }
    }
}
