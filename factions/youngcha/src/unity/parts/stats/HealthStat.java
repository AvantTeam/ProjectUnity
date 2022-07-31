package unity.parts.stats;

import arc.*;
import arc.scene.ui.layout.*;
import unity.parts.*;
import unity.parts.PartType.*;
import unity.util.*;

public class HealthStat extends PartStat{
    public float hpBoost;
    public boolean percentage = false;

    public HealthStat(float flat){
        super("health");
        hpBoost = flat;
    }

    public HealthStat(float flat, boolean boost){
        super("health");
        percentage = boost;
        hpBoost = flat;
    }

    @Override
    public void merge(ValueMap id, Part part){
        if(!percentage) id.add(name, hpBoost);
    }

    @Override
    public void mergePost(ValueMap id, Part part){
        if(percentage) id.mul(name, hpBoost);
    }

    @Override
    public void display(Table table){
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.statType." + name) + ": [accent]" + hpBoost + (percentage ? "%" : "")).left().top();
    }
}
