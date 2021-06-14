package unity.util;

import arc.struct.*;
import arc.util.*;
import mindustry.mod.*;
import rhino.*;

import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;

import static unity.Unity.*;
import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public final class ReflectUtils{
    private static final Seq<Object> jsArgs = new Seq<>();
    private static final Context context;
    private static final Scriptable scope;
    private static final Function handleInvoker;

    private static final MethodHandle modifiers;
    private static final MethodHandle declClass;
    private static final Field modifiersAnd;

    private static final MethodHandle setAccessor;

    private static final boolean useSun;
    private static Object j8RefFac;
    private static MethodHandle j8NewConsAccessor;
    private static MethodHandle j8NewInstance;

    private static final ObjectMap<Class<?>, Lookup> lookups = new ObjectMap<>();
    private static final int modes = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC | (Modifier.STATIC << 1) | (Modifier.STATIC << 2);

    public static final Object[] emptyObjects = new Object[0];
    public static final Class<?>[] emptyClasses = new Class[0];

    static{
        try {
            boolean use;
            try{
                Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
                j8RefFac = getReflectionFactory.invoke(null);

                Lookup lookup = MethodHandles.lookup();
                j8NewConsAccessor = lookup.unreflect(Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newConstructorAccessor", Constructor.class));
                j8NewInstance = lookup.unreflect(Class.forName("sun.reflect.ConstructorAccessor").getDeclaredMethod("newInstance", Object[].class));

                use = true;
            }catch(Exception e){
                use = false;
            }
            useSun = use;

            if(mods != null){
                Scripts scripts = mods.getScripts();
                context = getField(scripts, findField(Scripts.class, "context", true));
                scope = getField(scripts, findField(Scripts.class, "scope", true));
            }else{
                context = Context.enter();
                context.setOptimizationLevel(9);
                context.getWrapFactory().setJavaPrimitiveWrap(false);
                context.setLanguageVersion(Context.VERSION_ES6);

                scope = new ImporterTopLevel(context);
            }

            if(android){
                modifiers = null;
                declClass = null;

                modifiersAnd = findField(Field.class, "accessFlags", true);
                setAccessor = null;

                handleInvoker = null;
            }else{
                modifiers = getLookup(Field.class).findSetter(Field.class, "modifiers", int.class);
                declClass = getLookup(Constructor.class).findSetter(Constructor.class, "clazz", Class.class);

                modifiersAnd = null;

                Method setf = Structs.find(Field.class.getDeclaredMethods(), m -> m.getName().contains("setFieldAccessor"));
                setAccessor = getLookup(Field.class).unreflect(setf);

                handleInvoker = context.compileFunction(scope,
                    """
                    function(handle, args){
                        return handle.invokeWithArguments(args.toArray());
                    }
                    """,
                    "unity_impl.js", 0
                );
            }

            print("Reflection/invocation utility has been initialized.");
            print("Usage of sun packages: " + useSun);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static <T> T handleInvoke(MethodHandle handle, Object... args){
        synchronized(jsArgs){
            jsArgs.clear();

            Object[] all = new Object[2];
            all[0] = handle;
            all[1] = jsArgs.addAll(args);

            Object obj = handleInvoker.call(context, scope, scope, all);

            if(obj instanceof Wrapper w) obj = w.unwrap();

            return (T)obj;
        }
    }

    public static Lookup getLookup(Class<?> in){
        if(lookups.containsKey(in)) return lookups.get(in);

        Seq<Class<?>> argTypes = new Seq<>(Class.class);
        argTypes.addAll(Class.class, Class.class, int.class);

        Seq<Object> args = new Seq<>();
        args.addAll(in, null, modes);

        try {
            Lookup.class.getDeclaredField("prevLookupClass");
        }catch(NoSuchFieldException e){
            argTypes.remove(1);
            args.remove(1);
        }

        Constructor<Lookup> cons = findConstructor(Lookup.class, true, argTypes.toArray());
        Field name = findField(Class.class, "name", true);

        String prev = in.getName();

        setField(in, name, prev.substring(prev.lastIndexOf(".")));
        Lookup lookup = newInstance(cons, args.toArray());
        setField(in, name, prev);

        lookups.put(in, lookup);
        return lookup;
    }

    /** Finds a class from the parent classes that has a specific field. */
    public static Class<?> findClassf(Class<?> type, String field){
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()){
            try{
                type.getDeclaredField(field);
                break;
            }catch(NoSuchFieldException ignored){}
        }

        return type;
    }

    /** Finds a class from the parent classes that has a specific method. */
    public static Class<?> findClassm(Class<?> type, String method, Class<?>... args){
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()){
            try{
                type.getDeclaredMethod(method, args);
                break;
            }catch(NoSuchMethodException ignored){}
        }

        return type;
    }

    /** Finds a class from the parent classes that has a specific constructor. */
    public static Class<?> findClassc(Class<?> type, Class<?>... args){
        for(type = type.isAnonymousClass() ? type.getSuperclass() : type; type != null; type = type.getSuperclass()){
            try{
                type.getDeclaredConstructor(args);
                break;
            }catch(NoSuchMethodException ignored){}
        }

        return type;
    }

    /** A utility function to find a field without throwing {@link NoSuchFieldException}. */
    public static Field findField(Class<?> type, String field, boolean access){
        try{
            var f = findClassf(type, field).getDeclaredField(field);
            if(access) f.setAccessible(true);

            return f;
        }catch(NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    /** Sets a field of an object without throwing {@link IllegalAccessException}. */
    public static void setField(Object object, Field field, Object value){
        try{
            field.set(object, value);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** Gets a value from a field of an object without throwing {@link IllegalAccessException}. */
    public static <T> T getField(Object object, Field field){
        try{
            return (T)field.get(object);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** A utility function to find a method without throwing {@link NoSuchMethodException}. */
    public static Method findMethod(Class<?> type, String methodName, boolean access, Class<?>... args){
        try{
            var m = findClassm(type, methodName, args).getDeclaredMethod(methodName, args);
            if(access) m.setAccessible(true);

            return m;
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    /** Reflectively invokes a method without throwing {@link IllegalAccessException}. */
    public static <T> T invokeMethod(Object object, Method method, Object... args){
        try{
            return (T)method.invoke(object, args);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** A utility function to find a constructor without throwing {@link NoSuchMethodException}. */
    public static <T> Constructor<T> findConstructor(Class<T> type, boolean access, Class<?>... args){
        try{
            var c = ((Class<T>)findClassc(type, args)).getDeclaredConstructor(args);
            if(access) c.setAccessible(true);

            return c;
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    /** Reflectively instantiates a type without throwing {@link IllegalAccessException}. */
    public static <T> T newInstance(Constructor<T> constructor, Object... args){
        try{
            return constructor.newInstance(args);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static <T> Object getConstructorAccessor(Constructor<T> constructor){
        try{
            return handleInvoke(j8NewConsAccessor, j8RefFac, constructor);
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    /** Revokes a modifier from a field. <b>Using this to primitive types / {@link String} constant expressions has no use.</b> */
    public static void revokeModifier(Field field, int modifier){
        try{
            field.setAccessible(true);

            int mods = field.getModifiers();
            if(android){
                modifiersAnd.setInt(field, mods & ~modifier);
            }else{
                handleInvoke(modifiers, field, mods & ~modifier);
            }

            if(setAccessor != null){
                handleInvoke(setAccessor, field, null, false);
                handleInvoke(setAccessor, field, null, true);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    /** Invokes a modifier to a field. <b>Using this to primitive types / {@link String} constant expressions has no use.</b> */
    public static void invokeModifier(Field field, int modifier){
        try{
            field.setAccessible(true);

            int mods = field.getModifiers();
            if(android){
                modifiersAnd.setInt(field, mods | modifier);
            }else{
                handleInvoke(modifiers, field, mods | modifier);
            }

            if(setAccessor != null){
                handleInvoke(setAccessor, field, null, false);
                handleInvoke(setAccessor, field, null, true);
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    private static <T extends Enum<T>> T createEnum(Constructor<T> cons, String name, int ordinal, Object... args){
        try{
            Object[] arguments = new Object[args.length + 2];
            arguments[0] = name;
            arguments[1] = ordinal;
            System.arraycopy(args, 0, arguments, 2, args.length);

            if(useSun){
                return (T)handleInvoke(j8NewInstance, getConstructorAccessor(cons), arguments);
            }else{
                cons.setAccessible(true);

                Class<T> before = cons.getDeclaringClass();
                if(!android) handleInvoke(declClass, cons, Object.class);

                T obj = cons.newInstance(arguments);
                if(!android) handleInvoke(declClass, cons, before);

                return obj;
            }
        }catch(Throwable e){
            throw new RuntimeException(e);
        }
    }

    public static <T extends Enum<T>> T addEnumEntry(Class<T> type, Constructor<T> cons, String name, Object... args){
        Field valuesField = Structs.find(type.getDeclaredFields(), f -> f.getName().contains("$VALUES"));

        if(valuesField == null) throw new IllegalStateException("values field is null");
        try{
            valuesField.setAccessible(true);

            T[] previousValues = (T[])valuesField.get(type);
            Seq<T> values = new Seq<>(type);
            values.addAll(previousValues);

            T value = createEnum(cons, name, previousValues.length, args);
            values.add(value);

            revokeModifier(valuesField, Modifier.FINAL);
            valuesField.set(null, values.toArray());
            invokeModifier(valuesField, Modifier.FINAL);

            return value;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Class<?> classCaller(){
        var thread = Thread.currentThread();
        var trace = thread.getStackTrace();
        try{
            return Class.forName(trace[3].getClassName(), false, thread.getContextClassLoader());
        }catch(ClassNotFoundException e){
            return null;
        }
    }
}
