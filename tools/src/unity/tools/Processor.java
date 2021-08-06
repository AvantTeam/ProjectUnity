package unity.tools;

import arc.util.*;
import arc.util.Log.*;

import java.util.concurrent.*;

import static unity.Unity.*;

public interface Processor{
    void process(ExecutorService exec);

    default void finish(){}

    default void submit(ExecutorService exec, UnsafeRunnable run){
        exec.submit(() -> {
            try{
                run.run();
            }catch(Throwable t){
                var msg = Strings.getFinalMessage(t);
                print(LogLevel.err, "", msg != null ? msg : Strings.getStackTrace(Strings.getFinalCause(t)));
            }
        });
    }
}
