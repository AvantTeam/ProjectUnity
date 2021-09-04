package unity.type.decal;

import arc.*;
import arc.graphics.g2d.*;
import mindustry.gen.*;

public class FlagellaDecorationType extends UnitDecorationType{
    public String name;
    int segments;
    TextureRegion[] regions;
    TextureRegion end;

    public FlagellaDecorationType(String name, int textures, int segments){
        this.name = name;
        this.segments = segments;
        regions = new TextureRegion[textures];
    }

    @Override
    public void load(){
        for(int i = 0; i < regions.length; i++){
            regions[i] = Core.atlas.find(name + "-" + i);
        }
        end = Core.atlas.find(name + "-end");
    }

    @Override
    public void update(Unit unit, UnitDecoration deco){

    }

    @Override
    public void added(Unit unit, UnitDecoration deco){

    }

    @Override
    public void draw(Unit unit, UnitDecoration deco){

    }
}
