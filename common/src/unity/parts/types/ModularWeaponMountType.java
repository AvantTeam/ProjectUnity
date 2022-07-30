package unity.parts.types;

import mindustry.type.*;
import unity.parts.*;
import unity.parts.stats.AdditiveStat.*;
import unity.parts.stats.*;
import unity.util.*;

public class ModularWeaponMountType extends ModularPartType{
    public ModularWeaponMountType(String name){
        super(name);
    }

    public void weapon(int slots, Weapon weapon){
        stats.add(new WeaponSlotUseStat(slots));
        stats.add(new WeaponMountStat(weapon));
    }

    @Override
    public void appendStats(ModularPartStatMap statMap, ModularPart part, ModularPart[][] grid){
        super.appendStats(statMap, part, grid);
    }

    @Override
    public void drawTop(DrawTransform transform, ModularPart part){
        super.drawTop(transform, part);
    }
}
