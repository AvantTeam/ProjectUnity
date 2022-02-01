package unity.entities;

import arc.func.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

/**
 * May causes problems with modded blocks that actively modify the tiles.
 */
public class UnitTile extends Tile{
    public UnitTile(int x, int y){
        super(x, y);
    }

    @Override
    public void setFloor(Floor type){
        floor = type;
    }

    public void setBlockQuiet(Block block){
        this.block = block;
    }

    @Override
    protected void changeBuild(Team team, Prov<Building> entityprov, int rotation){
        if(block.hasBuilding()){
            build = entityprov.get();
            build.rotation = rotation;
            build.tile = this;
        }
    }
    
    @Override
    protected void fireChanged(){
        
    }

    @Override
    public void recache(){

    }

    @Override
    public void recacheWall(){

    }
}
