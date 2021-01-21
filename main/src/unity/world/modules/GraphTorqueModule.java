package unity.world.modules;

import arc.graphics.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.meta.*;
import unity.world.meta.*;
import unity.world.graph.*;
import unity.world.graphs.*;

//_RotPowerPropsCommon
public class GraphTorqueModule<T extends GraphTorque> extends GraphModule<T, GraphTorqueModule<T>, TorqueGraph<T>>{
    static final Color[] pals = new Color[]{Pal.accent, Pal.redSpark, Pal.plasticSmoke, Pal.lancerLaser};
    public float force, inertia;
    final IntFloatMap rots = new IntFloatMap(4);//propsList
    float friction;

    @Override
    void applySaveState(TorqueGraph<T> graph, int index){
        graph.lastVelocity = Math.max(graph.lastVelocity, ((Float[])saveCache.get(index))[0]);
    }

    @Override
    void updateExtension(){}

    @Override
    void updateProps(TorqueGraph<T> graph, int index){
        float rot = rots.get(index, 0f);
        rot += graph.lastVelocity;
        rot %= (360f * 24f);
        rots.put(index, rot);
    }

    @Override
    void proximityUpdateCustom(){}

    @Override
    void display(Table table){
        TorqueGraph<T> net = networks.get(0);
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
    void initStats(){
        friction = graph.baseFriction;
        setInertia(graph.baseInertia);
    }

    @Override
    void displayBars(Table table){}

    @Override
    void drawSelect(){
        for(var graph : networks){
            graph.value.connected.each(module -> Drawf.selected(module.parent.build.<Building>self(), pals[graph.key]));
        }
    }

    @Override
    TorqueGraph<T> newNetwork(){
        return new TorqueGraph<>();
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
    void writeLocal(Writes write, TorqueGraph<T> graph){
        write.f(graph.lastVelocity);
    }

    @Override
    Float[] readLocal(Reads read, byte revision){
        return new Float[]{read.f()};
    }

    @Override
    public GraphTorqueModule<T> graph(GraphTorque graph){
        this.graph = (T)graph;
        if(graph.isMultiConnector) multi = true;
        return this;
    }

    @Override
    public GraphType type(){
        return GraphType.torque;
    }

    //torque
    public void setInertia(float iner){
        float diff = iner - inertia;
        if(diff != 0f){
            if(multi){
                for(var i : networks.values()) i.injectInertia(diff);
            }else networks.get(0).injectInertia(diff);
        }
        inertia = iner;
    }

    public float getRotation(){
        return rots.get(0, 0f);
    }

    public float getRotationOf(int index){
        return rots.get(index, 0f);
    }

    public float friction(){
        return friction;
    }

    public void setMotorForceMult(float a){}
}
