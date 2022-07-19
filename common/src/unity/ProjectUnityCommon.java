package unity;

import arc.struct.*;
import mindustry.mod.*;
import unity.mod.*;
import unity.ui.*;

public abstract class ProjectUnityCommon extends Mod{
    public static DevBuild dev;

    public static final Seq<String> classes = new Seq<>(), packages = new Seq<>();

    public static final PUUI ui = new PUUI();
}
