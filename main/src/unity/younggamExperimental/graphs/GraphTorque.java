package unity.younggamExperimental.graphs;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import mindustry.graphics.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.bundle;
import static mindustry.Vars.tilesize;

//_RotPowerCommon
public class GraphTorque extends Graph{
    public final float baseFriction, baseInertia;

    public GraphTorque(float friction, float inertia){
        baseFriction = friction;
        baseInertia = inertia;
    }

    public GraphTorque(){
        this(0.1f, 10f);
    }

    @Override
    public void setStats(Table table){
        table.row().left();
        table.add("Torque system").color(Pal.accent).fillX();
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.friction") + ":[] ").left();
        table.row().left();
        table.add("[lightgray]" + bundle.get("stat.unity.inertia") + ":[] ").left();
        table.add(baseInertia + "t m^2");
        setStatsExt(table);
    }

    @Override
    public void setStatsExt(Table table){}

    @Override
    void drawPlace(int x, int y, int size, int rotation, boolean valid){
        for(int i = 0, len = accept.length; i < len; i++){
            if(accept[i] == 0) continue;
            Lines.stroke(3.5f, Color.white);
            GraphData outPos = GraphData.getConnectSidePos(i, size, rotation);
            int dx = (outPos.toPos.x + x) * tilesize;
            int dy = (outPos.toPos.y + y) * tilesize;
            Point2 dir = Geometry.d4(outPos.dir);
            Lines.line(dx - dir.x, dy - dir.y, dx - dir.x * 2, dy - dir.y * 2);
        }
    }

    @Override
    public GraphType type(){
        return GraphType.torque;
    }

    @Override
    public GraphTorqueModule module(){
        return new GraphTorqueModule<>().graph(this);
    }

    @Override
    boolean canBeMulti(){
        return true;
    }
}
