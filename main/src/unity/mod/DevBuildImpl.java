package unity.mod;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import rhino.*;
import unity.*;
import unity.cinematic.*;
import unity.util.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class DevBuildImpl implements DevBuild{
    @Override
    public void initScripts(){
        if(mods == null || platform == null) return;

        Time.mark();

        enableConsole = true;
        Scripts scripts = mods.getScripts();

        ImporterTopLevel scope = ReflectUtils.getField(mods.getScripts(), ReflectUtils.findField(Scripts.class, "scope", true));
        Constructor<NativeJavaPackage> constr = ReflectUtils.findConstructor(NativeJavaPackage.class, true,
            boolean.class, String.class, ClassLoader.class
        );
        Method importPack = ReflectUtils.findMethod(ImporterTopLevel.class, "importPackage", true, NativeJavaPackage.class);
        ClassLoader loader = getClass().getClassLoader();
        Cons<String> importPackage = name -> {
            NativeJavaPackage n = ReflectUtils.newInstance(constr, true, name, loader);
            n.setParentScope(scope);

            ReflectUtils.invokeMethod(scope, importPack, n);
        };
        Seq<String> permit = Seq.with(
            "unity",
            "rhino",
            "java.lang",
            "java.io",
            "java.util"
        );

        for(Package pkg : Package.getPackages()){
            if(!permit.contains(pkg.getName()::startsWith)) continue;
            importPackage.get(pkg.getName());
        }

        ReflectUtils.unblacklist();
        Unity.print(Strings.format("Total time to unblacklist and import unity packages: @ms", Time.elapsed()));
    }
}
