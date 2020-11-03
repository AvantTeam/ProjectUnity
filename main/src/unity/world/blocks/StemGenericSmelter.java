package unity.world.blocks;

import arc.func.Cons;
import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.production.GenericSmelter;

import static arc.Core.*;

public class StemGenericSmelter extends GenericSmelter{
    protected boolean preserveDraw = true, preserveUpdate = true;
    protected Cons<StemSmelterBuild> foreDrawer = e -> {}, afterDrawer = e -> {}, foreUpdate = e -> {}, afterUpdate = e -> {};
    protected TextureRegion[] dataRegions;
    protected String[] spriteNames = new String[]{};

    public StemGenericSmelter(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        int len = spriteNames.length;
        dataRegions = new TextureRegion[len];
        for(int i = 0; i < len; i++) dataRegions[i] = atlas.find(spriteNames[i]);
    }

    protected void addSprites(String... names){
        spriteNames = names;
    }

    public class StemSmelterBuild extends SmelterBuild{
        public Object data;

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
