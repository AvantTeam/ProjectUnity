package unity.content;

import mindustry.world.Block;
import unity.mod.*;
import unity.world.blocks.*;

import static unity.mod.FactionRegistry.register;

public class EndBlocks{
    public static Block endgame;

    public static void load(){
        endgame = register(Faction.end, new EndGameTurret("endgame"));
    }
}
