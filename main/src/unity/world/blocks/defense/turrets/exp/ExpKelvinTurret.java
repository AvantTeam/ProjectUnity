package unity.world.blocks.defense.turrets.exp;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.content.*;

public class ExpKelvinTurret extends ExpLiquidTurret{
    public ExpKelvinTurret(String name){
        super(name);
    }

    public class ExpKelvinTurretBuild extends ExpLiquidTurretBuild{
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return (liquids.current() == liquid || liquids.currentAmount() < 0.2f); //Accept any liquid, liquid acceptance copied from liquid tanks.
        }

        @Override
        public BulletType useAmmo(){
            BulletType b = peekAmmo();
            liquids.remove(liquids.current(), 1f / b.ammoMultiplier);
            return b;
        }

        @Override
        public BulletType peekAmmo(){
            BulletType b = ammoTypes.get(liquids.current());
            return b == null ? UnityBullets.kelvinLiquidLaser : b;
        }

        @Override
        public boolean hasAmmo(){
            return liquids.total() >= 1f / peekAmmo().ammoMultiplier;
        }
    }
}
