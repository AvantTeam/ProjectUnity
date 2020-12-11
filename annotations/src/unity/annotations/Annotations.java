package unity.annotations;

import arc.audio.*;
import unity.annotations.util.*;

import java.lang.annotation.*;

public class Annotations{
    /** Indicates that this content belongs to a specific faction */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionDef{
        /** @return The {@link Faction} */
        Faction type();
    }

    /** Indicates that this music belongs to a specific faction in a specific category */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface MusicDef{
        /** @return The {@link Faction} */
        Faction type();

        /**
         * The category of this {@link Music}.<br>
         * <br>
         * Reserved keywords are {@code "ambient"}, {@code "dark"}, and {@code "boss"}
         * @return The music category.
         */
        String category() default "ambient";
    }

    /** Indicates that this content implements exp mechanism */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface ExpDef{
    }
}
