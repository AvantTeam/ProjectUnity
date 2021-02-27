package unity.world.blocks.defense.turrets.exp;


import mindustry.entities.bullet.*;
import unity.entities.bullet.exp.ExpLaserFieldBulletType;

public class ExpPowerFieldTurret extends ExpPowerChargeTurret {
    public float basicFieldRadius;

    public ExpPowerFieldTurret(String name){
        super(name);
    }

    public class ExpPowerFieldTurretBuild extends ExpPowerChargeTurretBuild {

        @Override
        public BulletType peekAmmo(){
            BulletType bullet = shootType;
            if(bullet instanceof ExpLaserFieldBulletType) ((ExpLaserFieldBulletType)bullet).basicFieldRadius = basicFieldRadius;
            return shootType;
        }
    }
}
