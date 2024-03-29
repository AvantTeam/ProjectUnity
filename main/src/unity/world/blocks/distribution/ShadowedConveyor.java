package unity.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

//is shadow really important?
public class ShadowedConveyor extends Conveyor{
    TextureRegion shadowRegion;//shadowRog

    public ShadowedConveyor(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        shadowRegion = Core.atlas.find(name + "-shadow");
    }

    public class ShadowedConveyorBuild extends ConveyorBuild{
        boolean looking;

        @Override
        public void draw(){
            super.draw();
            Draw.z(Layer.block);
            if(nextc == null || block != nextc.block) Draw.rect(shadowRegion, x, y, rotdeg());
            if(!looking) Draw.rect(shadowRegion, x, y, rotdeg() + 180f);
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            Building backBuilding = back();
            Building leftBuilding = left();
            Building rightBuilding = right();
            Tile back = backBuilding != null ? backBuilding.tile : tile;
            Tile left = leftBuilding != null ? leftBuilding.tile : tile;
            Tile right = rightBuilding != null ? rightBuilding.tile : tile;

            looking = ((back.relativeTo(tile) - back.build.rotation) == 0 && back.build.block == block) || ((left.relativeTo(tile) - left.build.rotation) == 0 && left.build.block == block) || ((right.relativeTo(tile) - right.build.rotation) == 0 && right.build.block == block);
        }
    }
}
