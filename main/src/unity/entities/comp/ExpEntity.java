package unity.entities.comp;

import arc.struct.*;
import unity.annotations.Annotations.*;

public interface ExpEntity{
    float testFloat();

    @Initialize(eval = "120")
    int testInt();

    @ReadOnly
    @Initialize(eval = "new $T<>()", args = {"arc.struct.Seq"})
    Seq<String> testSeq();
}
