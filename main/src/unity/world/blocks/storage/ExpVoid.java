package unity.world.blocks.storage;

import mindustry.gen.Building;
import mindustry.world.Block;
import unity.world.blocks.ExpBuildBase;

public class ExpVoid extends Block{
    public ExpVoid(String name){
        super(name);
        update = solid = true;
    }

    public class ExpVoidBuild extends Building implements ExpBuildBase{
        @Override
        public float getMaxExp(){
            return 0f;
        }

        @Override
        public float totalExp(){
            return 0f;
        }

        @Override
        public float expf(){
            return 0f;
        }

        @Override
        public void setExp(float a){}

        @Override
        public boolean consumesOrb(){
            return enabled;
        }

        @Override
        public void incExp(float a){}
    }
}
