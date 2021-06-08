package unity.mod;

import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import rhino.*;
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
        ImporterTopLevel scope = ReflectUtils.getField(mods.getScripts(), ReflectUtils.findField(Scripts.class, "scope", true));
        Seq<NativeJavaPackage> packages = new Seq<>(NativeJavaPackage.class);

        for(Package pkg : Package.getPackages()){
            if(!pkg.getName().startsWith("unity")) continue;

            NativeJavaPackage n = new NativeJavaPackage(pkg.getName(), Unity.mod().loader);
            n.setParentScope(scope);

            packages.add(n);
        }

        scope.importPackage(null, null, packages.toArray(), null);
        ReflectUtils.unblacklist();

        Unity.print(Strings.format("Total time to unblacklist and import unity packages: @ms", Time.elapsed()));
    }
}
