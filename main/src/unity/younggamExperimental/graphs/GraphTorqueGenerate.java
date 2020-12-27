package unity.younggamExperimental.graphs;

import arc.scene.ui.layout.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.bundle;

public class GraphTorqueGenerate extends GraphTorque{
    public final float maxSpeed, torqueCoeff, maxTorque, startTorque;

    public GraphTorqueGenerate(float friction, float inertia, float maxSpeed, float torqueCoeff, float maxTorque, float startTorque){
        super(friction, inertia);
        this.maxSpeed = maxSpeed;
        this.torqueCoeff = torqueCoeff;
        this.maxTorque = maxTorque;
        this.startTorque = startTorque;
    }

    public GraphTorqueGenerate(){
        super();
        this.maxSpeed = 10f;
        this.torqueCoeff = 1f;
        this.maxTorque = 5f;
        this.startTorque = 5f;
    }

    @Override
    public void setStatsExt(Table table){
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.maxspeed") + ":[] ").left();
        table.add(maxSpeed * 0.1f + "rps");
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.maxtorque") + ":[] ").left();
        table.add(maxTorque + "KNm");
    }

    @Override
    public GraphTorqueGenerateModule module(){
        return new GraphTorqueGenerateModule().graph(this);
    }

    @Override
    boolean canBeMulti(){
        return false;
    }
}
