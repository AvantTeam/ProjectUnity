package unity.world.blocks.distribution;

import mindustry.world.blocks.distribution.Conveyor;

public class ExpConveyor extends Conveyor{
    protected float realSpeed, drawMultiplier;

    public ExpConveyor(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        realSpeed = speed;
    }

    public class KoruhConveyorBuild extends ConveyorBuild{
        @Override
        public void draw(){
            speed = realSpeed * drawMultiplier;
            super.draw();
            speed = realSpeed;
        }
    }
}
