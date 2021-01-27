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

    /** Indicates that this content implements an exp mechanism */
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

    /** Indicates that this content's entity will be the one that is pointed */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface EntityPoint{
        /** @return The entity type */
        Class<?> type();
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

    /** Wether this class is the base class for exp types. Only one type may use this */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpBase{
    }

    // end region
    // region utilities

    /** Wether the field returned by this getter is meant to be read-only */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ReadOnly{
    }

    /** Wether this getter must be implemented by the type's subtypes */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MustInherit{
    }

    /** Wether this method replaces the actual method in the base class */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Replace{
    }

    /**
     * Indicates that the following field returned by this getter is gonna be initialized
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Initialize{
        /** @return The code that is gonna be used to evaluate the initializer */
        String eval();

        /** @return Class arguments to be parsed into {@link #eval()}. */
        Class<?>[] args() default {};
    }

    /**
     * Loads texture regions but does not assign them to their acquirers
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadRegs{
        /** @return The regions' name */
        String[] value();

        /** @return Wether it should load the outlined region as well */
        boolean outline() default false;
    }

    // end region
}
