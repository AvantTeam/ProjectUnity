package unity.younggamExperimental.modules;

import mindustry.gen.*;
import mindustry.world.*;
import unity.younggamExperimental.*;

//building 에 들어갈 모듈. powerModule와 비슷한 역할?
public class GraphModule{
    Building parentBuilding;
    Block parentBlock;
    int lastRecalc;

    protected GraphData getConnectSidePos(int index){
        return GraphData.getConnectSidePos(index, parentBlock.size, parentBuilding.rotation);
    }

    protected void recalcPorts(){
        if(lastRecalc == parentBuilding.rotation) return;

    }
}
