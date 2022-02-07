package unity.parts.stat;

import mindustry.type.Weapon;
import unity.parts.ModularPart;
import unity.parts.ModularPartStat;
import unity.parts.ModularPartStatMap;
import unity.parts.ModularPartType;
import unity.util.ValueMap;

public class WeaponMountStat extends ModularPartStat{
    Weapon baseweapon;

    public WeaponMountStat(Weapon w){
        super("weapon");
        baseweapon = w.copy();
    }

    @Override
    public void merge(ModularPartStatMap id, ModularPart part){
        if(id.has("weapons")){
            var weaponsarr = id.stats.getList("weapons");
            ValueMap weapon = new ValueMap();
            weapon.put("part", part);
            Weapon copy = baseweapon.copy();
            copy.x = part.getCx()*ModularPartType.partSize;
            copy.y = part.getCy()*ModularPartType.partSize;
            weapon.put("weapon", copy);
            weaponsarr.add(weapon);
        }
    }

    @Override
    public void mergePost(ModularPartStatMap id, ModularPart part){

    }
}
