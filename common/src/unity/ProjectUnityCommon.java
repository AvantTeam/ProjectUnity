package unity;

import arc.struct.*;
import mindustry.mod.*;
import unity.annotations.Annotations.*;
import unity.mod.*;

@ModBase
public abstract class ProjectUnityCommon extends Mod{
    public static DevBuild dev;

    public static final Seq<String> classes = new Seq<>(), packages = new Seq<>();
}
