package unity.world.blocks.essence;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.*;
import unity.graphics.*;

public class EssenceRouter extends EndEssenceBlock{
    TextureRegion bottomRegion;
    public Color baseColor = EndPal.endSolidDarker;

    public EssenceRouter(String name){
        super(name);
        underBullets = true;
        solid = false;
        noUpdateDisabled = true;
        canOverdrive = false;
    }

    @Override
    public void load(){
        super.load();
        bottomRegion = Core.atlas.find("unity-essence-duct-bottom");
    }
    
    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{bottomRegion, region};
    }

    public class EssenceRouterBuild extends EndEssenceBuilding{
        @Override
        public void draw(){
            //Draw.z(Layer.blockUnder);
            if(essence.essence > 0.001f){
                Draw.color(baseColor, EndPal.endMid, efract());
            }else{
                Draw.color(baseColor);
            }
            Fill.square(x, y, size * Vars.tilesize / 2f);

            Draw.color();
            super.draw();
        }

        @Override
        public void updateTile(){
            if(essence.essence > 0.001f){
                dumpEssence(3f);
            }
        }
    }
}
