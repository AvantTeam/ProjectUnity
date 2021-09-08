package unity.entities.bullet.energy;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.*;
import unity.entities.*;

public class EmpBasicBulletType extends BasicBulletType{
    public float empRange = 100f;
    public float empMaxRange = 470f;
    public float empDuration = 120f;
    public float empDisconnectRange = 0f;
    public float empLogicDamage = 0f;
    public float empBatteryDamage = 7000f;
    public int powerGridIteration = 7;

    public EmpBasicBulletType(float speed, float damage){
        super(speed, damage, "unity-electric-shell");
        trailLength = 7;
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        Emp.hitTile(x, y, b.team, empRange, empDuration, empBatteryDamage, empLogicDamage, 10, empDisconnectRange, empMaxRange, powerGridIteration);
        UnityFx.empShockwave.at(b.x, b.y, empRange);
        if(Emp.hitDisconnect) UnityFx.empShockwave.at(b.x, b.y, empDisconnectRange);
        if(Emp.hitPowerGrid) UnityFx.empShockwave.at(b.x, b.y, empMaxRange);
    }
}
