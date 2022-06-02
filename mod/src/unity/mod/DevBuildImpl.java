package unity.mod;

import arc.util.*;
import arc.util.Log.*;
import unity.util.*;

import static mindustry.Vars.*;

/**
 * @author GlennFolker
 */
public class DevBuildImpl implements DevBuild{
    @Override
    public void setup(){
        enableConsole = true;
        Log.level = LogLevel.debug;
    }

    @Override
    public void init(){
        JSBridge.importDefaults(JSBridge.unityScope);
    }

    @Override
    public boolean isDev(){
        return true;
    }
}
