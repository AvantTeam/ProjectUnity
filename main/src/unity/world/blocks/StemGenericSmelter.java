package unity.world.blocks;

import arc.func.Cons;
import mindustry.world.blocks.production.GenericSmelter;

public class StemGenericSmelter extends GenericSmelter{
    protected boolean preserveDraw = true, preserveUpdate = true;
    protected Cons<FlexibleGenericSmelterBuild> foreDrawer = e -> {}, afterDrawer = e -> {}, foreUpdate = e -> {}, afterUpdate = e -> {};

    public StemGenericSmelter(String name){
        super(name);
    }

    public class FlexibleGenericSmelterBuild extends GenericCrafterBuild{
        @Override
        public void draw(){
            foreDrawer.get(this);
            if(!preserveUpdate) super.draw();
            afterDrawer.get(this);
        }

        @Override
        public void updateTile(){
            foreUpdate.get(this);
            if(!preserveUpdate) super.updateTile();
            afterUpdate.get(this);
        }
    }
}
