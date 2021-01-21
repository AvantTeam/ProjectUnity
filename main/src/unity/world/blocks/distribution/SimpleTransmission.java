package unity.world.blocks.distribution;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import unity.graphics.*;
import unity.world.blocks.*;

import static arc.Core.atlas;

public class SimpleTransmission extends GraphBlock{
    final TextureRegion[] topRegions = new TextureRegion[2], overlayRegions = new TextureRegion[2], movingRegions = new TextureRegion[3];//topsprite,overlaysprite,moving
    TextureRegion bottomRegion, mbaseRegion;//base,mbase

    public SimpleTransmission(String name){
        super(name);
        rotate = solid = true;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 2; i++){
            topRegions[i] = atlas.find(name + "-top" + (i + 1));
            overlayRegions[i] = atlas.find(name + "-overlay" + (i + 1));
        }
        for(int i = 0; i < 3; i++) movingRegions[i] = atlas.find(name + "-moving" + (i + 1));
        bottomRegion = atlas.find(name + "-bottom");
        mbaseRegion = atlas.find(name + "-mbase");
    }

    public class SimpleTransmissionBuild extends GraphBuild{
        @Override
        public void draw(){
            var torqueGraph = torque();
            float graphRot0 = torqueGraph.getRotationOf(0);
            float graphRot1 = torqueGraph.getRotationOf(1);
            float fixedRot = (rotdeg() + 90f) % 180f - 90f;
            int variant = (rotation + 1) % 4 >= 2 ? 1 : 0;
            Draw.rect(bottomRegion, x, y);
            Draw.rect(mbaseRegion, x, y, rotdeg());

            var offset = Geometry.d4(rotation + 1);
            float ox = offset.x * 4f;
            float oy = offset.y * 4f;
            //xelo..
            UnityDrawf.drawRotRect(movingRegions[0], x + ox, y + oy, 16f, 4.5f, 4.5f, fixedRot, graphRot0, graphRot0 + 180f);
            UnityDrawf.drawRotRect(movingRegions[0], x + ox, y + oy, 16f, 4.5f, 4.5f, fixedRot, graphRot0 + 180f, graphRot0 + 360f);

            UnityDrawf.drawRotRect(movingRegions[1], x + ox * -0.125f, y + oy * -0.125f, 16f, 4.5f, 4.5f, fixedRot, 360f - graphRot0, 180f - graphRot0);//360-(a+180)
            UnityDrawf.drawRotRect(movingRegions[1], x + ox * -0.125f, y + oy * -0.125f, 16f, 4.5f, 4.5f, fixedRot, 540f - graphRot0, 360f - graphRot0);//720-(a+180),720-(a+360)

            UnityDrawf.drawRotRect(movingRegions[2], x - ox, y - oy, 16f, 2.5f, 2.5f, fixedRot, graphRot1, graphRot1 + 180f);
            UnityDrawf.drawRotRect(movingRegions[2], x - ox, y - oy, 16f, 2.5f, 2.5f, fixedRot, graphRot1 + 180f, graphRot1 + 360f);

            Draw.rect(overlayRegions[variant], x, y, rotdeg());

            Draw.rect(topRegions[rotation % 2], x, y);
            drawTeamTop();
        }
    }
}
