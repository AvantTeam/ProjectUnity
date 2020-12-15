package unity.annotations;

import arc.audio.*;

import java.lang.annotation.*;

public class Annotations{
    // region definitions

    /** Indicates that this content belongs to a specific faction */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionDef{
        /** @return The {@link Faction} */
        String type();
    }

    /** Indicates that this content has an exp mechanism */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpDef{
        /** @return The exp type */
        Class<?> type();
    }

    /** Indicates that this content's entity type inherits interfaces */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityDef{
        /** @return The base class for the generated entity class */
        Class<?> base();

        /** @return The interfaces that will be inherited by the generated entity class */
        Class<?>[] def();
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

    /** Wether this class is the base class for faction enum. Only one type may use this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionBase{
    }

    // end region
    // region utilities

    /** Wether the field returned by this getter is meant to be read-only */
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

        /** @return Class arguments to be parsed into {@link #eval()}. */
        String[] args() default {};
    }

    // end region
}
