package unity.world.modules;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.util.*;
import unity.world.graphs.*;

import static arc.Core.*;

public class GraphTorqueGenerateModule extends GraphTorqueModule<GraphTorqueGenerate>{
    final WindowedMean smoothedForce = new WindowedMean(40);
    float motorForceMult = 1f, maxMotorForceMult = 1f;

    @Override
    void updateExtension(){
        force = Utils.linear(networks.get(0).lastVelocity, graph.maxSpeed, graph.maxTorque, graph.torqueCoeff)
            * parent.build.edelta() * motorForceMult * maxMotorForceMult;
        smoothedForce.add(force);
    }

    @Override
    void displayBars(Table table){
        table.add(new Bar(
            () -> bundle.get("stat.unity.torque") + ": " + Strings.fixed(smoothedForce.mean(), 1) + "/" + Strings.fixed(graph.maxTorque * maxMotorForceMult, 1),
            () -> Pal.darkishGray,
            () -> smoothedForce.mean() / graph.maxTorque / maxMotorForceMult
        )).growX().row();
    }

    @Override
    public GraphTorqueGenerateModule graph(GraphTorqueGenerate graph){
        this.graph = graph;
        if(graph.isMultiConnector) multi = true;
        return this;
    }

    @Override
    public void setMotorForceMult(float a){
        motorForceMult = a;
    }
}
