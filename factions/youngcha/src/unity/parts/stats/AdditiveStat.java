package unity.parts.stats;

import arc.*;
import arc.scene.ui.layout.*;
import unity.parts.*;
import unity.parts.PartType.*;
import unity.util.*;

public class AdditiveStat extends PartStat{
    float value;

    public AdditiveStat(String name, float value){
        super(name);
        this.value = value;
    }

    @Override
    public void merge(ValueMap id, Part part){
        id.add(name, value);
    }

    public void display(Table table){
        String valueStr = ": [accent]" + value;
        if(value % 1 <= 0.001f){
            valueStr = ": [accent]" + (int)value;
        }
        table.row();
        table.add("[lightgray]" + Core.bundle.get("ui.parts.statType." + name) + valueStr).left().top();
    }

    @Override
    public void mergePost(ValueMap id, Part part){

    }

    public static class PowerUsedStat extends AdditiveStat{
        public PowerUsedStat(float power){
            super("powerUsage", power);
        }
    }

    public static class EngineStat extends AdditiveStat{
        public EngineStat(float power){
            super("power", power);
        }
    }

    public static class MassStat extends AdditiveStat{
        public MassStat(float power){
            super("mass", power);
        }
    }

    public static class WeaponSlotStat extends AdditiveStat{
        public WeaponSlotStat(float slot){
            super("weaponSlots", slot);
        }
    }

    public static class WeaponSlotUseStat extends AdditiveStat{
        public WeaponSlotUseStat(float slot){
            super("weaponSlotUse", slot);
        }
    }

    public static class AbilitySlotStat extends AdditiveStat{
        public AbilitySlotStat(float slot){
            super("abilitySlots", slot);
        }
    }

    public static class AbilitySlotUseStat extends AdditiveStat{
        public AbilitySlotUseStat(float slot){
            super("abilitySlotUse", slot);
        }
    }

    public static class ItemCapacityStat extends AdditiveStat{
        public ItemCapacityStat(float slot){
            super("itemCapacity", slot);
        }
    }

}
