package unity.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.type.*;
import unity.type.*;

public class UnderworldBlocks {
    public static boolean loaded = false;
    public static Seq<UnderworldBlock> blocks = new Seq<>();

    public static void load() {
        if(loaded) return;
        for(int i = 0; i < 99; i++){
            blocks.add(new UnderworldBlock("pipe-straight", ItemStack.with(Items.copper, 1), 10));
        }
        loaded = true;
    }
}
