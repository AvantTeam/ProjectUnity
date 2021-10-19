package unity.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import unity.graphics.*;
import unity.world.blocks.*;

import static arc.Core.*;

public class InlineGearbox extends GraphBlock{
    TextureRegion topRegion, overlayRegion, movingRegion, baseRegion, mbaseRegion, gearRegion;//topsprite,overlaysprite,moving,base,mbase,gear

    public InlineGearbox(String name){
        super(name);
        rotate = solid = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        overlayRegion = atlas.find(name + "-overlay");
        movingRegion = atlas.find(name + "-moving");
        baseRegion = atlas.find(name + "-base");
        mbaseRegion = atlas.find(name + "-mbase");
        gearRegion = atlas.find(name + "-gear");
    }

    public class InlineGearboxBuild extends GraphBuild{
        @Override
        public void draw(){
            float shaftRot = torque().getRotation();
            float fixedRot = (rotdeg() + 90f) % 180f - 90f;
            Draw.rect(baseRegion, x, y);
            Draw.rect(mbaseRegion, x, y, fixedRot);
            Point2 offset = Geometry.d4(rotation + 1);
            float ox = offset.x * 4f;
            float oy = offset.y * 4f;
            UnityDrawf.drawRotRect(movingRegion, x + ox, y + oy, 16f, 3.5f, 8f, fixedRot, shaftRot, shaftRot + 90f);
            UnityDrawf.drawRotRect(movingRegion, x - ox, y - oy, 16f, 3.5f, 8f, fixedRot, shaftRot + 90f, shaftRot + 180f);

            Draw.rect(gearRegion, x + 2f, y + 2f, shaftRot);
            Draw.rect(gearRegion, x - 2f, y + 2f, -shaftRot);
            Draw.rect(gearRegion, x + 2f, y - 2f, -shaftRot);
            Draw.rect(gearRegion, x - 2f, y - 2f, shaftRot);

            Draw.rect(overlayRegion, x, y, fixedRot);
            Draw.rect(topRegion, x, y, fixedRot);
            drawTeamTop();
        }
    }
}
