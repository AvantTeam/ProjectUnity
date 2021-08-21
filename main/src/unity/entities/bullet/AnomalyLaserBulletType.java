package unity.entities.bullet;

import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

import static unity.util.Utils.*;

public class AnomalyLaserBulletType extends BulletType{
    float length = 250f;

    public AnomalyLaserBulletType(float damage){
        super(0f, damage);
    }

    @Override
    public void init(Bullet b){
        float charge = Math.max(b.damage - damage, 0f);
        float d = damage * ((charge / 40f) + 1f);
        float size = (Mathf.sqrt(charge * 3f) * 2f) + 2.5f;

        Tmp.v1.trns(b.rotation(), length + charge);
        collideLineRawEnemyRatio(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, size, (building, ratio, direct) -> {
            building.damage(d * ratio);
            return building.block.absorbLasers;
        }, (unit, ratio) -> {
            return false;
        }, (ex, ey) -> hit(b, ex, ey));
    }
}
