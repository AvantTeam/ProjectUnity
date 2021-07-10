package unity.type.weapons;

import arc.math.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.util.*;

public class EnergyChargeWeapon extends Weapon{
    public Cons3<Unit, WeaponMount, Float> drawCharge = (unit, mount, charge) -> {};
    public boolean drawTop = true;

    public EnergyChargeWeapon(String name){
        super(name);
    }

    @Override
    public void draw(Unit unit, WeaponMount mount){
        if(!drawTop) drawCharge.get(unit, mount, 1f - Mathf.clamp(mount.reload / reload));
        super.draw(unit, mount);
        if(drawTop) drawCharge.get(unit, mount, 1f - Mathf.clamp(mount.reload / reload));
    }
}
