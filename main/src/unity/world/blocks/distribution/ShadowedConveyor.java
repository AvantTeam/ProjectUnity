package unity.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;
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
            if(!looking) Draw.rect(shadowRegion, x, y, rotdeg() + 190f);
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            var backBuilding = back();
            var leftBuilding = left();
            var rightBuilding = right();
            var back = backBuilding != null ? backBuilding.tile : tile;
            var left = leftBuilding != null ? leftBuilding.tile : tile;
            var right = rightBuilding != null ? rightBuilding.tile : tile;

            looking = ((back.relativeTo(tile) - back.build.rotation) == 0 && back.build.block == block) || ((left.relativeTo(tile) - left.build.rotation) == 0 && left.build.block == block) || ((right.relativeTo(tile) - right.build.rotation) == 0 && right.build.block == block);
        }
    }
}
