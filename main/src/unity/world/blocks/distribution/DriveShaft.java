package unity.world.blocks.distribution;

import arc.graphics.g2d.*;
import unity.graphics.*;
import unity.world.blocks.*;

import static arc.Core.atlas;

public class DriveShaft extends GraphBlock{
    final TextureRegion[] baseRegions = new TextureRegion[4];
    TextureRegion topRegion, overlayRegion, movingRegion;//topsprite,overlaysprite,moving

    public DriveShaft(String name){
        super(name);
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        overlayRegion = atlas.find(name + "-overlay");
        movingRegion = atlas.find(name + "-moving");
        for(int i = 0; i < 4; i++) baseRegions[i] = atlas.find(name + "-base" + (i + 1));
    }

    public class DriveShaftBuild extends GraphBuild{
        int baseSpriteIndex;

        @Override
        public void onNeighboursChanged(){
            baseSpriteIndex = 0;
            torque().eachNeighbourValue(n -> {
                if(rotation == 1 || rotation == 2) baseSpriteIndex += n.equals(0) ? 2 : 1;
                else baseSpriteIndex += n.equals(0) ? 1 : 2;
            });
        }

        @Override
        public void draw(){
            float graphRot = torque().getRotation();
            float fixedRot = (rotdeg() + 90f) % 180f - 90f;
            Draw.rect(baseRegions[baseSpriteIndex], x, y, fixedRot);
            UnityDrawf.drawRotRect(movingRegion, x, y, 8f, 3.5f, 6f, fixedRot, graphRot, graphRot + 90f);
            Draw.rect(overlayRegion, x, y, fixedRot);
            Draw.rect(topRegion, x, y, fixedRot);
        }
    }
}
