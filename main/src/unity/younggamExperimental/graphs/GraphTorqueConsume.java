package unity.younggamExperimental.graphs;

import arc.scene.ui.layout.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.bundle;

//_TorqueConsumer
public class GraphTorqueConsume extends GraphTorque{
    public final float nominalSpeed, oversupplyFalloff, idleFriction, workingFriction;

    public GraphTorqueConsume(float friction, float inertia, float nominalS, float falloff, float idleF, float workingF){
        super(friction, inertia);
        nominalSpeed = nominalS;
        oversupplyFalloff = falloff;
        idleFriction = idleF;
        workingFriction = workingF;
    }

    public GraphTorqueConsume(){
        super();
        nominalSpeed = 10f;
        oversupplyFalloff = 0.7f;
        idleFriction = 0.01f;
        workingFriction = 0.1f;
    }

    @Override
    public void setStatsExt(Table table){
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.nominalspeed") + ":[] ").left();
        table.add(nominalSpeed * 0.1f + "rps");
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.idlefric") + ":[] ").left();
        table.add(idleFriction * 1000f + "Nmv^-2");
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.workfric") + ":[] ").left();
        table.add(workingFriction * 1000f + "Nmv^-2");
    }

    @Override
    public GraphTorqueModule module(){
        //TODO
        return null;
    }

    @Override
    boolean canBeMulti(){
        return false;
    }
}
