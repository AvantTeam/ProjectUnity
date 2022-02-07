package unity.parts.types;

import mindustry.type.Weapon;
import unity.parts.ModularPart;
import unity.parts.ModularPartStatMap;
import unity.parts.ModularPartType;
import unity.parts.stat.WeaponMountStat;

public class ModularWeaponMountType extends ModularPartType{
    public ModularWeaponMountType(String name){
        super(name);
    }

    public void weapon(Weapon weapon){
        stats.add(new WeaponMountStat(weapon));
    }

    @Override
    public void appendStats(ModularPartStatMap statmap, ModularPart part, ModularPart[][] grid){
        super.appendStats(statmap, part, grid);
    }


}
