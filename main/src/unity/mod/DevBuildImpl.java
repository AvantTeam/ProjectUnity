package unity.mod;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import arc.util.async.*;
import mindustry.mod.*;
import rhino.*;
import unity.*;
import unity.util.*;

import java.lang.reflect.*;
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

        Unity.print(Strings.format("Total time to import unity packages: @ms", Time.elapsed()));
    }

    private void initCommandLine(){
        if(!headless){
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
