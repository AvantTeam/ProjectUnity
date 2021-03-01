package unity.world.graphs;

import arc.scene.ui.layout.*;
import unity.world.modules.*;

import static arc.Core.bundle;

public class GraphTorqueConsume extends GraphTorque{
    public final float nominalSpeed, oversupplyFalloff, idleFriction, workingFriction;

    public GraphTorqueConsume(float inertia, float nominalS, float falloff, float idleF, float workingF){
        super(idleF, inertia);
        nominalSpeed = nominalS;
        oversupplyFalloff = falloff;
        idleFriction = idleF;
        workingFriction = workingF;
    }

    public GraphTorqueConsume(float inertia, float nominalS, float idleF, float workingF){
        this(inertia, nominalS, 0.7f, idleF, workingF);
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
        table.add("[lightgray]" + bundle.get("stat.unity.idlefriction") + ":[] ").left();
        table.add(idleFriction * 1000f + "Nmv^-2");
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.workfriction") + ":[] ").left();
        table.add(workingFriction * 1000f + "Nmv^-2");
    }

    @Override
    public GraphTorqueConsumeModule module(){
        return new GraphTorqueConsumeModule().graph(this);
    }

    @Override
    boolean canBeMulti(){
        return false;
    }
}
