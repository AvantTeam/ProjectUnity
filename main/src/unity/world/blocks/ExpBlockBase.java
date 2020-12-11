package unity.world.blocks;

import arc.util.*;
import mindustry.ui.*;
import unity.graphics.*;

import static arc.Core.bundle;

public interface ExpBlockBase{
    int expCapacity();

    default Bar expBar(ExpBuildBase build){
        return new Bar(() -> bundle.get("explib.exp"), () -> Tmp.c1.set(UnityPal.expColor).lerp(UnityPal.expMaxColor, build.expf()), build::expf);
    }
}