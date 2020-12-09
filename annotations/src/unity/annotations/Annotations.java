package unity.annotations;

import unity.annotations.util.*;

import java.lang.annotation.*;

public class Annotations{
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface FactionDef{
        Faction type();
    }
}
