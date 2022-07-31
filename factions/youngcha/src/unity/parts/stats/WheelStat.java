package unity.parts.stats;

import arc.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import unity.parts.*;
import unity.parts.PartType.*;
import unity.util.*;

import static mindustry.Vars.tilesize;

public class WheelStat extends PartStat{
    float wheelStrength;
    float nominalWeight; //max weight supported until statSpeed penalties
    float maxSpeed;

    public WheelStat(float wheelStrength, float nominalWeight, float maxSpeed){
        super("wheel");
        this.wheelStrength = wheelStrength;
        this.nominalWeight = nominalWeight;
        this.maxSpeed = maxSpeed;
    }

    @Override
    public void merge(ValueMap id, Part part){
        ValueMap wheelStat = id.getValueMap("wheel");
        wheelStat.add("totalStrength", wheelStrength);
        wheelStat.add("totalSpeedPower", wheelStrength * maxSpeed);
        wheelStat.add("weightCapacity", nominalWeight);
    }

    @Override
    public void mergePost(ValueMap id, Part part){
        ValueMap wheelStat = id.getValueMap("wheel");
        if(wheelStat.has("nominal statSpeed")) return;
        wheelStat.put("nominal statSpeed", wheelStat.getFloat("totalSpeedPower") / wheelStat.getFloat("totalStrength"));
    }

    @Override
    public void display(Table table){
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.statType.weightcap") + ": [accent]" + nominalWeight).left().top();
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.statType.maxspeed") + ": [accent]" + Core.bundle.format("ui.parts.stat.speed", Strings.fixed(maxSpeed * 60f / tilesize, 1))).left().top();
    }
}
