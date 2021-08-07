package unity.tools;

import arc.util.*;
import arc.util.Log.*;

import java.util.concurrent.*;

import static unity.Unity.*;

/**
 * Base processor interface, used to submit asynchronous threads to process assets.
 * @author GlennFolker
 */
public interface Processor{
    /**
     * Submit processing threads here. A call to {@link #submit(ExecutorService, String, UnsafeRunnable)} should be used to
     * submit the threads to get a pleasant and proper error message in case any of the threads encountered an uncaught
     * exception.
     * @param exec The executor service for submitting threads.
     */
    void process(ExecutorService exec);

    /** Called after all processing threads are finished. */
    default void finish(){}

    default void submit(ExecutorService exec, String name, UnsafeRunnable run){
        exec.submit(() -> {
            try{
                run.run();
            }catch(Throwable t){
                var msg = Strings.getFinalMessage(t);
                print(LogLevel.err, " ", name + ":", msg != null ? msg : Strings.getStackTrace(Strings.getFinalCause(t)));
            }
        });
    }
}
