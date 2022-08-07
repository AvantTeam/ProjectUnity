package unity.world.graph;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.ui.*;

import static unity.graphics.YoungchaPal.*;

public class HeatGraphNode extends GraphNode<HeatGraph>{
    public static final float celsiusZero = 273.15f;
    public static float ambientTemp = celsiusZero + 20;
    public float flux = 0;
    public float heatEnergy = 1f;
    float energyBuffer = 0; //write to
    public float emission = 0.01f;
    public float conductivity = 0.1f;
    public float heatCapacity = 1f;
    public float maxTemp = celsiusZero + 1000;

    public boolean heatProducer = false;
    public float targetTemp = 1000;
    public float prodEfficiency = 0.1f;
    public float minGenerate = -9999999999999999f;
    public float lastEnergyInput = 0;
    public float efficiency = 0;

    public HeatGraphNode(GraphBuild build, float emission, float conductivity, float heatCapacity, float maxTemp){
        super(build);
        this.emission = emission;
        this.conductivity = conductivity;
        this.maxTemp = maxTemp;
        this.heatCapacity = heatCapacity;
        energyBuffer = this.heatEnergy = heatCapacity * ambientTemp;
    }

    public HeatGraphNode(GraphBuild build, float emission, float conductivity, float heatCapacity, float maxTemp, float targetTemp, float prodEfficiency){
        super(build);
        this.emission = emission;
        this.conductivity = conductivity;
        this.maxTemp = maxTemp;
        this.targetTemp = targetTemp;
        this.prodEfficiency = prodEfficiency;
        this.heatProducer = true;
        this.heatCapacity = heatCapacity;
        energyBuffer = this.heatEnergy = heatCapacity * ambientTemp;
    }

    public HeatGraphNode(GraphBuild build){
        super(build);
    }

    @Override
    public void displayBars(Table table){
        table.row();
        table.add(new Bar(
        () -> Core.bundle.format(
        "bar.unity-temp",
        Strings.fixed(getTemp() - celsiusZero, 1)
        ),
        () -> (getTemp() < maxTemp ? heatColor() : (Time.time % 30 > 15 ? Color.scarlet : Color.black)),
        () -> Mathf.clamp(Math.abs(getTemp() / maxTemp))
        ));
    }

    @Override
    public void update(){
        //graph handles all heat transmission.
        heatEnergy += (ambientTemp - getTemp()) * emission * Time.delta / 60f;
        if(heatProducer){
            generateHeat();
        }
        if(getTemp() > maxTemp){
            Puddles.deposit(build().tile, Liquids.slag, 9);
            build().damage(((getTemp() - maxTemp) / maxTemp) * Time.delta * 10f);
        }
    }

    public Color heatColor(){
        Color c = new Color();
        heatColor(c);
        return c;
    }

    public void heatColor(Color input){
        heatColor(getTemp(), input);
    }

    public static void heatColor(float t, Color input){
        float a;
        if(t > celsiusZero){
            a = Math.max(0, (t - 498) * 0.001f);
            if(a < 0.01){
                input.set(Color.clear);
                return;
            }
            input.set(heatColor.r, heatColor.g, heatColor.b, a);
            if(a > 1){
                input.add(0, 0, 0.01f * a);
                input.mul(a);
            }
        }else{
            a = 1.0f - Mathf.clamp(t / celsiusZero);
            if(a < 0.01){
                input.set(Color.clear);
            }
            input.set(coldColor.r, coldColor.g, coldColor.b, a);
        }
    }

    @Override
    public void read(Reads read){
        this.energyBuffer = this.heatEnergy = read.f();
    }

    @Override
    public void write(Writes write){
        write.f(this.heatEnergy);
    }

    public void generateHeat(float targetTemp, float eff){
        heatEnergy += (targetTemp - getTemp()) * eff;
    }

    public void generateHeat(){
        lastEnergyInput = Math.max(minGenerate, (targetTemp - getTemp()) * efficiency * prodEfficiency);
        heatEnergy += lastEnergyInput * Time.delta;
    }

    public float getTemp(){
        return heatEnergy / heatCapacity;
    }

    public void setTemp(float temp){
        heatEnergy = temp * heatCapacity;
    }

    public void addHeatEnergy(float e){
        heatEnergy += e;
    }

    @Override
    public void displayStats(Table table){
        addBundleStatLevelLine(table, "stat.unity-emission", emission * 60, new float[]{0.5f, 1f, 3f, 10, 20});
        addBundleStatLine(table, "stat.unity-heatcapacity", heatCapacity);
        addBundleStatLine(table, "stat.unity-heatconductivity", conductivity * 60);
        addBundleStatLine(table, "stat.unity-maxtemp", maxTemp - celsiusZero);
        //
    }
}
