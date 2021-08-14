package unity.mod;

import arc.util.*;
import arc.util.async.*;
import unity.*;
import unity.util.*;

import java.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class DevBuildImpl implements DevBuild{
    @Override
    public void init(){
        initScripts();
        initCommandLine();
    }

    private void initScripts(){
        Time.mark();

        enableConsole = true;
        JSBridge.importDefaults(JSBridge.defaultScope);

        Unity.print(Strings.format("Total time to import unity packages: @ms", Time.elapsed()));
    }

    private void initCommandLine(){
        if(!headless && !android){
            Threads.daemon("Command-Executor", () -> {
                Scanner scan = new Scanner(System.in);
                String line;

                while((line = scan.nextLine()) != null){
                    String[] split = line.split("\\s+");
                    if(OS.isWindows){
                        String[] args = new String[split.length + 2];
                        args[0] = "cmd";
                        args[1] = "/c";

                        System.arraycopy(split, 0, args, 2, split.length);

                        OS.execSafe(args);
                    }else{
                        OS.execSafe(split);
                    }
                }
            });

            Unity.print("Done setting up command line listener");
        }
    }
}
