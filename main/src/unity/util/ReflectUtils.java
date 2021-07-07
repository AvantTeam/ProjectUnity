package unity.util;

import java.lang.reflect.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
@SuppressWarnings("unchecked")
public final class ReflectUtils{
    public static final Object[] emptyObjects = new Object[0];
    public static final Class<?>[] emptyClasses = new Class[0];

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

    /** A utility function to find a field without throwing exceptions. */
    public static Field findField(Class<?> type, String field, boolean access){
        try{
            var f = findClassf(type, field).getDeclaredField(field);
            if(access) f.setAccessible(true);

            return f;
        }catch(NoSuchFieldException e){
            throw new RuntimeException(e);
        }
    }

    /** Sets a field of an model without throwing exceptions. */
    public static void setField(Object object, Field field, Object value){
        try{
            field.set(object, value);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** Gets a value from a field of an model without throwing exceptions. */
    public static <T> T getField(Object object, Field field){
        try{
            return (T)field.get(object);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** A utility function to find a method without throwing exceptions. */
    public static Method findMethod(Class<?> type, String methodName, boolean access, Class<?>... args){
        try{
            var m = findClassm(type, methodName, args).getDeclaredMethod(methodName, args);
            if(access) m.setAccessible(true);

            return m;
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    /** Reflectively invokes a method without throwing exceptions. */
    public static <T> T invokeMethod(Object object, Method method, Object... args){
        try{
            return (T)method.invoke(object, args);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /** A utility function to find a constructor without throwing exceptions. */
    public static <T> Constructor<T> findConstructor(Class<T> type, boolean access, Class<?>... args){
        try{
            var c = ((Class<T>)findClassc(type, args)).getDeclaredConstructor(args);
            if(access) c.setAccessible(true);

            return c;
        }catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    /** Reflectively instantiates a type without throwing exceptions. */
    public static <T> T newInstance(Constructor<T> constructor, Object... args){
        try{
            return constructor.newInstance(args);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Class<?> classCaller(){
        var thread = Thread.currentThread();
        var trace = thread.getStackTrace();
        try{
            return Class.forName(trace[3].getClassName(), false, mods.mainLoader());
        }catch(ClassNotFoundException e){
            return null;
        }
    }
}
