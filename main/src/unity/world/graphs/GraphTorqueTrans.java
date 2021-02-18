package unity.world.graphs;

import arc.*;
import arc.scene.ui.layout.*;
import unity.world.modules.*;

public class GraphTorqueTrans extends GraphTorque{
    public final float[] ratio = new float[]{1f, 2f};

    public GraphTorqueTrans(float friction, float inertia){
        super(friction, inertia);
        multi();
    }

    public GraphTorqueTrans setRatio(float ratio1, float ratio2){
        ratio[0] = ratio1;
        ratio[1] = ratio2;
        
        return this;
    }

    @Override
    public void setStatsExt(Table table){
        table.row().left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.transratio") + ":[] ").left();
        
        String ratio = this.ratio[0] + ":" + this.ratio[1];
        table.add(ratio);
    }

    @Override
    public GraphTorqueTransModule module(){
        return new GraphTorqueTransModule().graph(this);
    }
}
