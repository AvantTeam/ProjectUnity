package unity.parts.stats;

import arc.*;
import arc.scene.ui.layout.*;
import unity.parts.*;
import unity.parts.PartType.*;

public class HealthStat extends PartStat{
    public float hpboost = 0;
    public boolean percentage = false;

    public HealthStat(float flat){
        super("health");
        hpboost = flat;
    }

    public HealthStat(float flat, boolean boost){
        super("health");
        percentage = boost;
        hpboost = flat;
    }

    @Override
    public void merge(PartStatMap id, Part part){
        if(!percentage && id.has(name)){
            var jo = id.getOrCreate(name);
            jo.put("value", jo.getFloat("value") + hpboost);
        }
    }

    @Override
    public void mergePost(PartStatMap id, Part part){
        if(percentage && id.has(name)){
            var jo = id.getOrCreate(name);
            jo.put("value", jo.getFloat("value") * hpboost);
        }
    }

    @Override
    public void display(Table table){
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.stattype." + name) + ": [accent]" + hpboost + (percentage ? "%" : "")).left().top();
    }
}
