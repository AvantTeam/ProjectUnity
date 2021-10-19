package unity.mod;

import arc.util.*;
import arc.util.async.*;
import unity.util.*;

import java.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class DevBuildImpl implements DevBuild{
    @Override
    public void init(){
        enableConsole = true;
        JSBridge.importDefaults(JSBridge.defaultScope);

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
        }
    }

    @Override
    public boolean isDev(){
        return true;
    }
}
