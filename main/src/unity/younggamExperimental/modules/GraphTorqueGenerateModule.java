package unity.younggamExperimental.modules;

import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.util.*;
import unity.younggamExperimental.graphs.*;

import static arc.Core.bundle;

public class GraphTorqueGenerateModule extends GraphTorqueModule<GraphTorqueGenerate>{
    final WindowedMean smoothedForce = new WindowedMean(40);
    float motorForceMult = 1f, maxMotorForceMult = 1f;

    @Override
    void updateExtension(){
        force = Funcs.linear(networks.get(0).lastVelocity, graph.maxSpeed, graph.maxTorque, graph.torqueCoeff)
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
}
