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
            return liquid.heatCapacity > 0f;
        }

        @Override
        public BulletType useAmmo(){
            BulletType b = UnityBullets.kelvinLaser;
            liquids.remove(liquids.current(), 1f / b.ammoMultiplier);
            return b;
        }

        @Override
        public BulletType peekAmmo(){
            return UnityBullets.kelvinLaser;
        }

        @Override
        public boolean hasAmmo(){
            BulletType b = UnityBullets.kelvinLaser;
            return liquids.total() >= 1f / b.ammoMultiplier;
        }
    }
}
