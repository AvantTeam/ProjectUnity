package unity.world.blocks.production;

import arc.func.Cons;
import arc.graphics.g2d.TextureRegion;
import arc.struct.*;
import mindustry.world.blocks.production.GenericSmelter;

import static arc.Core.*;

public class StemGenericSmelter extends GenericSmelter{
    protected boolean preserveDraw = true, preserveUpdate = true;
    protected Cons<StemSmelterBuild> foreDrawer = e -> {}, afterDrawer = e -> {}, foreUpdate = e -> {}, afterUpdate = e -> {};
    protected final ObjectMap<String, TextureRegion> regions = new ObjectMap<>(3);
    
    protected String[] spriteNames = new String[]{};

    public StemGenericSmelter(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        
        for(String s : spriteNames) regions.put(s, atlas.find(name + "-" + s));
    }

    protected void addSprites(String... names){
        spriteNames = names;
    }

    public class StemSmelterBuild extends SmelterBuild{
        public float fdata;

        @Override
        public void draw(){
            foreDrawer.get(this);
            
            if(preserveDraw) super.draw();
            afterDrawer.get(this);
        }

        @Override
        public void updateTile(){
            foreUpdate.get(this);
            
            if(preserveUpdate) super.updateTile();
            afterUpdate.get(this);
        }
    }
}
