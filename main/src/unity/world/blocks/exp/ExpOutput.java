package unity.world.blocks.exp;

import arc.graphics.g2d.*;

public class ExpOutput extends ExpTank{
    public float ratio = 0.3f;
    public float reloadTime = 30f;

    public ExpOutput(String name){
        super(name);
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class ExpOutputBuild extends ExpTankBuild {
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

        }
    }
}
