package unity.younggamExperimental.modules;

import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

public class GraphTorqueTransModule extends GraphTorqueModule<GraphTorqueTrans>{

    @Override
    void updateExtension(){
        if(networks.isEmpty() || dead) return;
        float[] ratios = graph.ratio;
        float totalMRatio = 0f;
        float totalM = 0f;
        boolean allPositive = true;
        for(int i = 0; i < ratios.length; i++){
            TorqueGraph net = networks.get(i);
            if(net == null) return;
            totalMRatio += net.lastInertia * ratios[i];
            totalM += net.lastInertia * net.lastVelocity;
            allPositive &= net.lastInertia > 0f;
        }
        if(totalMRatio != 0f && totalM != 0 && allPositive){
            for(int i = 0; i < ratios.length; i++){
                TorqueGraph net = networks.get(i);
                float cratio = net.lastInertia * ratios[i] / totalMRatio;
                net.lastVelocity = totalM * cratio / net.lastInertia;
            }
        }
    }
}
