package unity.parts.types;

import mindustry.type.*;
import unity.parts.*;
import unity.parts.stats.AdditiveStat.*;
import unity.parts.stats.*;
import unity.util.*;

public class ModularUnitWeaponMountType extends ModularUnitPartType{
    public ModularUnitWeaponMountType(String name){
        super(name);
    }

    public void weapon(int slots, Weapon weapon){
        stats.add(new WeaponSlotUseStat(slots));
        stats.add(new WeaponMountStat(weapon));
    }

    @Override
    public void drawTop(DrawTransform transform, Part part){
        super.drawTop(transform, part);
    }
}
