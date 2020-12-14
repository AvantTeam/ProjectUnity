package unity.annotations;

import arc.audio.*;

import java.lang.annotation.*;

public class Annotations{
    // region definitions

    /** Indicates that this content belongs to a specific faction */
    @Target({ElementType.FIELD, ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionDef{
        /** @return The {@link Faction} */
        String type() default "invalid";

        /** @return Wether this class is the base class for faction enum. Only one type may use this */
        boolean base() default false;
    }

    /** Indicates that this music belongs to a specific faction in a specific category */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MusicDef{
        /** @return The {@link Faction} */
        String facType();

        /**
         * The category of this {@link Music}.<br>
         * <br>
         * Reserved keywords are {@code "ambient"}, {@code "dark"}, and {@code "boss"}
         * @return The music category.
         */
        String category() default "ambient";
    }

    // end region
    // region utilities

    /** Indicates that the following function is used to get a field that will be defined later */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Getter{
    }

    /** Same as {@link Getter}, the following function will be identified as a setter */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Setter{
    }

    /**
     * Indicates that the following field returned by this getter is meant to be read-only
     * @see Getter
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReadOnly{
    }

    /**
     * Indicates that the following field returned by this getter is gonna be initialized
     * @see Getter
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Initialize{
        /** @return The code that is gonna be used to evaluate the initializer */
        String eval();

        /** @return Class arguments to be parsed into {@link #eval()} */
        Class<?>[] args() default {};
    }

    // end region
}
