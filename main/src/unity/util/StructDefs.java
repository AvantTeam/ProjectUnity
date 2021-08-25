package unity.util;

import arc.graphics.*;
import arc.math.geom.*;
import unity.annotations.Annotations.*;

@SuppressWarnings("unused")
final class StructDefs{
    @StructWrap({
        @StructField(name = "r", value = 8, range = @Range(min = 0f, max = 1f)),
        @StructField(name = "g", value = 8, range = @Range(min = 0f, max = 1f)),
        @StructField(name = "b", value = 8, range = @Range(min = 0f, max = 1f)),
        @StructField(name = "a", value = 8, range = @Range(min = 0f, max = 1f))
    }) Color colorStruct;

    @StructWrap({
        @StructField(name = "x"),
        @StructField(name = "y")
    }) Vec2 vec2Struct;
}
