package unity.world.blocks.environment;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import unity.util.*;

import static mindustry.Vars.world;

/**
 * Uses dynamic image according to adjacent same walls.
 * @author younggam, xelo
 */
public class ConnectedWall extends StaticWall{
    TextureRegion[][] tiles;

    public ConnectedWall(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        tiles = Core.atlas.find(name + "-tiles").split(32, 32);
    }

    @Override
    public void drawBase(Tile tile){
        Tile[][] grid = new Tile[3][3];
        boolean avail = false;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                grid[i][j] = world.tile(i + tile.x - 1, j + tile.y - 1);
                if(grid[i][j] != null){
                    avail = true;
                }
            }
        }
        int index = TilingUtils.getTilingIndex(grid, 1, 1, t -> t != null && t.block() == this);
        if(avail){
            Draw.rect(tiles[index % 12][index / 12], tile.worldx(), tile.worldy());
        }else{
            Draw.rect(region, tile.worldx(), tile.worldy());
        }
        if(tile.overlay().wallOre){
            tile.overlay().drawBase(tile);
        }
    }
}
