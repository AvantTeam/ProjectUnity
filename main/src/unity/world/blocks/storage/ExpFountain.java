package unity.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.entities.bullet.exp.*;

import static arc.Core.atlas;

public class ExpFountain extends Block implements ExpOrbHandlerBase{
    protected TextureRegion topRegion;

    public ExpFountain(String name){
        super(name);
        update = solid = true;
        timers++;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
    }

    public class ExpFountainBuild extends Building{
        @Override
        public void draw(){
            super.draw();
            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(Mathf.absin(20f, 0.4f));
            Draw.rect(topRegion, x, y);
            Draw.blend();
            Draw.reset();
        }

        @Override
        public void updateTile(){
            if(enabled && timer(0, 60f)) ExpOrb.spreadExp(x, y, 100f, 6f);
        }

        @Override
        public void onDestroyed(){
            ExpOrb.spreadExp(x, y, 500f, 8f);
            super.onDestroyed();
        }
    }
}
