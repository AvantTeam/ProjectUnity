package unity.content;

import mindustry.world.Block;
import unity.world.blocks.*;

public class EndBlocks{
    public static Block endgame;

    public static void load(){
        endgame = new EndGameTurret("endgame");
    }
}
