package unity.world.blocks.power;

import unity.world.blocks.*;

public class TorqueGenerator extends GraphBlock{
    public TorqueGenerator(String name){
        super(name);
        
        rotate = true;
    }

    public class TorqueGeneratorBuild extends GraphBuild{
        @Override
        public void updatePre(){
            torque().setMotorForceMult(generateTorque());
        }

        protected float generateTorque(){
            return 1f;
        }
    }
}
