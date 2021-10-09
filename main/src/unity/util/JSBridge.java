package unity.util;

import arc.*;
import rhino.*;

import static mindustry.Vars.*;
import static unity.Unity.*;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions. Note that {@link #unityScope}
 * does not support the {@code require()} function.
 * @author GlennFolker
 */
public final class JSBridge{
    public static Context context;
    public static ImporterTopLevel defaultScope;
    public static ImporterTopLevel unityScope;

    private JSBridge(){}

    // Main thread only!
    public static void init(){
        try{
            context = mods.getScripts().context;
            defaultScope = (ImporterTopLevel)mods.getScripts().scope;

            unityScope = new ImporterTopLevel(context);
            context.evaluateString(unityScope, Core.files.internal("scripts/global.js").readString(), "global.js", 1);
        }catch(Throwable ignored){} // Happens in sprite generation - ignore, since irrelevant anyway.
    }

    public static void importDefaults(ImporterTopLevel scope){
        for(var pack : packages){
            importPackage(scope, pack);
        }
    }

    public static void importPackage(ImporterTopLevel scope, String packageName){
        var p = new NativeJavaPackage(packageName, mods.mainLoader());
        p.setParentScope(scope);

        scope.importPackage(p);
    }

    public static void importPackage(ImporterTopLevel scope, Package pack){
        importPackage(scope, pack.getName());
    }

    public static void importClass(ImporterTopLevel scope, String canonical){
        importClass(scope, ReflectUtils.findClass(canonical));
    }

    public static void importClass(ImporterTopLevel scope, Class<?> type){
        var nat = new NativeJavaClass(scope, type);
        nat.setParentScope(scope);

        scope.importClass(nat);
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source){
        return compileFunc(scope, sourceName, source, 1);
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source, int lineNum){
        return context.compileFunction(scope, source, sourceName, lineNum);
    }
}
