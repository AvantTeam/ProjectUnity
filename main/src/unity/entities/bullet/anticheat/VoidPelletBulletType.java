package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.content.effects.*;
import unity.util.*;

public class VoidPelletBulletType extends AntiCheatBulletTypeBase{
    public VoidPelletBulletType(float speed, float damage){
        super(speed, damage);
        lifetime = 90f;
        trailColor = Color.black;
        trailLength = 16;
        trailWidth = 2f;
        despawnEffect = hitEffect = HitFx.voidHit;
        homingPower = 0.01f;
        homingRange = 50f;
        homingDelay = 20f;
        hitSize = 3f;
        keepVelocity = false;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.fdata = b.rotation();
        b.rotation(b.rotation() + Mathf.range(120f));
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.fdata != -361f){
            float ang = Utils.angleDistSigned(b.rotation(), b.fdata);
            b.vel.rotate(-ang * Mathf.clamp(0.2f * Time.delta));
            if(Math.abs(ang) <= 0.06f){
                b.fdata = -361f;
            }
        }
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);
        Draw.color(Color.black);
        Fill.square(b.x, b.y, 2f, b.rotation() + 45f);
    }

    @Override
    public void drawLight(Bullet b){

    }
}
