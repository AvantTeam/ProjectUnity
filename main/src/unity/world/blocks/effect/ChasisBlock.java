package unity.world.blocks.effect;

import mindustry.gen.Building;
import unity.world.blocks.ConnectedBlock;
import unity.world.blocks.GraphBlock;

//this does nothing for now.
//It requires a new graph to function, however i need to unfuck the graph system first.
//Making a new graph in the js version was meant to be easiest thing in the world :c
public class ChasisBlock extends GraphBlock{
    public ChasisBlock(String name){
        super(name);
    }
    public class ChasisBlockBuild extends GraphBuild implements ConnectedBlock{
        @Override
        public void updatePre(){
            torque().setMotorForceMult(generateTorque());
        }

        protected float generateTorque(){
            return 1f;
        }

        @Override public boolean isConnected(Building b){
            return false;
        }
    }
}
