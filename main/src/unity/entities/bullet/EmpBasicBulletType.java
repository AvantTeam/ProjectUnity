package unity.entities.bullet;

import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.*;

public class EmpBasicBulletType extends BasicBulletType{
    public float empRange = 100f;
    public float empMaxRange = 470f;
    public float empDuration = 120f;
    public float empDisconnectRange = 0f;
    public float empLogicDamage = 0f;
    public float empBatteryDamage = 7000f;
    public int trailLength = 7;

    public EmpBasicBulletType(float speed, float damage){
        super(speed, damage, "unity-electric-shell");
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new Trail(trailLength);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.data instanceof Trail t) t.update(b.x, b.y);
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        Emp.hitTile(x, y, b.team, empRange, empDuration, empBatteryDamage, empLogicDamage, 10, empDisconnectRange, empMaxRange, 7);
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof Trail t) t.draw(backColor, (width / 2f) / 2f);
        super.draw(b);
    }
}
