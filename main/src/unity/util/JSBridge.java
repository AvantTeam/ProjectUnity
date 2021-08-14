package unity.util;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import rhino.*;

import java.lang.reflect.*;

import static mindustry.Vars.*;

/**
 * Utility class for transition between Java and JS scripts, as well as providing a custom top level scope for the sake of
 * cross-mod compatibility. Use the custom scope for programmatically compiling Rhino functions. Note that {@link #unityScope}
 * does not support the {@code require()} function.
 * @author GlennFolker
 */
public final class JSBridge{
    public static final Context context;
    public static final ImporterTopLevel defaultScope;
    public static final ImporterTopLevel unityScope;

    private static final Constructor<NativeJavaPackage> packCtr = ReflectUtils.findConstructor(NativeJavaPackage.class, true, boolean.class, String.class, ClassLoader.class);
    private static final Method importPack = ReflectUtils.findMethod(ImporterTopLevel.class, "importPackage", true, NativeJavaPackage.class);
    private static final Method importClass = ReflectUtils.findMethod(ImporterTopLevel.class, "importClass", true, NativeJavaClass.class);

    private JSBridge(){}

    static{
        context = Reflect.get(Scripts.class, mods.getScripts(), "context");
        defaultScope = Reflect.get(Scripts.class, mods.getScripts(), "scope");

        unityScope = new ImporterTopLevel(context);
        context.evaluateString(unityScope, Core.files.internal("scripts/global.js").readString(), "global.js", 0);
    }

    public static void importDefaults(Scriptable scope){
        Seq<String> permit = Seq.with(
            "unity",
            "rhino",
            "java.lang",
            "java.io",
            "java.util"
        );

        for(Package pkg : Package.getPackages()){
            if(!permit.contains(pkg.getName()::startsWith)) continue;
            importPackage(scope, pkg);
        }
    }

    public static void importPackage(Scriptable scope, String packageName){
        NativeJavaPackage p = ReflectUtils.newInstance(packCtr, true, packageName, mods.mainLoader());
        p.setParentScope(scope);

        ReflectUtils.invokeMethod(scope, importPack, p);
    }

    public static void importPackage(Scriptable scope, Package pack){
        importPackage(scope, pack.getName());
    }

    public static void importClass(Scriptable scope, String canonical){
        importClass(scope, ReflectUtils.findClass(canonical));
    }

    public static void importClass(Scriptable scope, Class<?> type){
        NativeJavaClass nat = new NativeJavaClass(scope, type);
        nat.setParentScope(scope);

        ReflectUtils.invokeMethod(scope, importClass, nat);
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source){
        return compileFunc(scope, sourceName, source, 0);
    }

    public static Function compileFunc(Scriptable scope, String sourceName, String source, int lineNum){
        return context.compileFunction(scope, source, sourceName, lineNum);
    }
}
