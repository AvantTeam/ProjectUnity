package unity.mod;

import static mindustry.Vars.*;

/**
 * @author GlennFolker
 */
public class DevBuildImpl implements DevBuild{
    @Override
    public void setup(){
        enableConsole = true;
    }

    @Override
    public boolean isDev(){
        return true;
    }
}
