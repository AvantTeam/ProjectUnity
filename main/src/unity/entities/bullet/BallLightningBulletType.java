package unity.entities.bullet;

import arc.audio.*;
import arc.graphics.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class BallLightningBulletType extends BasicBulletType{
    public float damageRadius;
    public Color ballLightningColor;
    public Sound ballLightningSound = Sounds.spark;
    public float ballLightningRange, ballLightningDamage, ballLightningInaccuracy, ballLightningReload = 10f, ballLightningSpread = 25f;
    public int ballLightningLength, ballLightningLengthRand, ballLightnings = 1;

    public BallLightningBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
        collides = false;
        shrinkY = 0f;
    }

    public BallLightningBulletType(float speed, float damage){
        this(speed, damage, "bullet");
    }

    @Override
    public void update(Bullet b){
        Teamc target = Units.closestTarget(b.team, b.x, b.y, ballLightningRange, e -> (e.isGrounded() && collidesGround) || (e.isFlying() && collidesAir), t -> collidesGround);
        if(target != null && b.timer(1, ballLightningReload)){
            ballLightningSound.at(b, Mathf.random(0.9f, 1.1f));
            for(int i = 0; i < ballLightnings; i++){
                Lightning.create(b, ballLightningColor, ballLightningDamage, b.x, b.y, Angles.angle(b.x, b.y, target.x(), target.y()) + Mathf.range(ballLightningInaccuracy) + (i - ballLightnings/2f) * ballLightningSpread, ballLightningLength + Mathf.random(ballLightningLengthRand));
            }
        }

        if(b.timer(2, 5)){
            Damage.damage(b.team, b.x, b.y, damageRadius, damage);
        }

        super.update(b);
    }
}
