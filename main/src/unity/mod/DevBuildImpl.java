package unity.mod;

import arc.util.*;
import mindustry.mod.*;
import unity.*;
import unity.util.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class DevBuildImpl implements DevBuild{
    @Override
    @SuppressWarnings("deprecation")
    public void initScripts(){
        if(mods == null || platform == null) return;

        Time.mark();

        enableConsole = true;
        Scripts scripts = mods.getScripts();
        for(Package pkg : Package.getPackages()){
            if(!pkg.getName().startsWith("unity")) continue;

            scripts.runConsole(Strings.format("importPackage(Packages.@)", pkg.getName()));
        }

        ReflectUtils.unblacklist();
        Unity.print(Strings.format("Total time to unblacklist and import unity packages: @ms", Time.elapsed()));
    }
}
