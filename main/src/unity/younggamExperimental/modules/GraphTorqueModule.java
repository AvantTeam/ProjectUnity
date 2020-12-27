package unity.younggamExperimental.modules;

import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;
import unity.younggamExperimental.*;
import unity.younggamExperimental.graph.*;
import unity.younggamExperimental.graphs.*;

//_RotPowerPropsCommon
public class GraphTorqueModule<T extends GraphTorque> extends GraphModule<T, GraphTorqueModule<T>, TorqueGraph<T>>{
    static final Color[] pals = new Color[]{Pal.accent, Pal.redSpark, Pal.plasticSmoke, Pal.lancerLaser};
    final FloatSeq rots = new FloatSeq(4);//propsList
    float force, inertia = 10f, friction = 0.1f;

    @Override
    void applySaveState(TorqueGraph graph, int index){
        graph.lastVelocity = Math.max(graph.lastVelocity, ((Float[])saveCache.get(index))[0]);
    }

    @Override
    void updateExtension(){}

    @Override
    void updateProps(TorqueGraph graph, int index){
        float rot = rots.get(index);
        rot += graph.lastVelocity;
        rot %= (360f * 24f);
        rots.set(index, rot);
    }

    @Override
    void proximityUpdateCustom(){}

    @Override
    void display(Table table){
        TorqueGraph net = networks.get(0);
        if(multi || net == null) return;
        String ps = " " + StatUnit.perSecond.localized();
        table.row();
        table.table(sub -> {
            sub.clearChildren();
            sub.left();
            sub.label(() -> Strings.fixed(net.lastVelocity / 6f, 2) + "r" + ps).color(Color.lightGray);
        }).left();
    }

    @Override
    void initStats(){}

    @Override
    void displayBars(Table table){}

    @Override
    void drawSelect(){
        for(var graph : networks){
            graph.value.connected.each(module -> Drawf.selected(module.parent.build.<Building>self(), pals[graph.key]));
        }
    }

    @Override
    TorqueGraph newNetwork(){
        return new TorqueGraph(this);
    }

    @Override
    void writeGlobal(Writes write){
        write.f(force);
        write.f(inertia);
        write.f(friction);
    }

    @Override
    void readGlobal(Reads read, byte revision){
        force = read.f();
        inertia = read.f();
        friction = read.f();
    }

    @Override
    void writeLocal(Writes write, TorqueGraph graph){
        write.f(graph.lastVelocity);
    }

    @Override
    Float[] readLocal(Reads read, byte revision){
        float fk = read.f();
        return new Float[]{fk};
    }

    @Override
    public GraphType type(){
        return GraphType.torque;
    }

    //torque
    void setInertia(float iner){
        float diff = iner - inertia;
        if(diff != 0f){
            if(multi){
                for(var i : networks.values()) i.injectInertia(diff);
            }else networks.get(0).injectInertia(diff);
        }
        inertia = iner;
    }

    float getRotation(){
        return rots.get(0);
    }

    float getRotationOf(int index){
        return rots.get(index);
    }

    public float force(){
        return force;
    }

    public float friction(){
        return friction;
    }

    public float inertia(){
        return inertia;
    }
}
