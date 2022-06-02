package unity.util;

import static mindustry.Vars.*;

/**
 * Shared utility access for reflective operations, without throwing any checked exceptions.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public final class ReflectUtils{
    private ReflectUtils(){
        throw new AssertionError();
    }

    public static <T> Class<T> findc(String name){
        try{
            return (Class<T>)Class.forName(name, true, mods.mainLoader());
        }catch(Throwable t){
            throw new RuntimeException(t);
        }
    }

    public static Class<?> box(Class<?> unboxed){
        return switch(unboxed.getName()){
            case "void" -> Void.class;
            case "byte" -> Byte.class;
            case "char" -> Character.class;
            case "short" -> Short.class;
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            default -> unboxed;
        };
    }

    public static Class<?> unbox(Class<?> boxed){
        return switch(boxed.getSimpleName()){
            case "Void" -> void.class;
            case "Byte" -> byte.class;
            case "Character" -> char.class;
            case "Short" -> short.class;
            case "Integer" -> int.class;
            case "Long" -> long.class;
            case "Float" -> float.class;
            case "Double" -> double.class;
            default -> boxed;
        };
    }
}
