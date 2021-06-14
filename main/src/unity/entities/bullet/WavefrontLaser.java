package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.entities.bullet.EndCutterLaserBulletType.*;
import unity.graphics.*;
import unity.util.*;

public class WavefrontLaser extends BulletType{
    public float length = 450f;
    public float accel = 20f, laserSpeed = 20f;
    public Color[] colors = {UnityPal.advanceDark.cpy().mul(0.9f, 1f, 1f, 0.4f), UnityPal.advanceDark, UnityPal.advance, Color.white};

    public WavefrontLaser(float damage){
        super(0.005f, damage);
        despawnEffect = Fx.none;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public void init(){
        super.init();
        drawSize = length * 2f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new LaserData();
    }

    @Override
    public float range(){
        return length;
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof LaserData vec){
            if(vec.restartTime >= 5f){
                vec.velocity = Mathf.clamp((vec.velocityTime / accel) + vec.velocity, 0f, laserSpeed);
                b.fdata = Mathf.clamp(b.fdata + (vec.velocity * Time.delta), 0f, length);
                vec.velocityTime += Time.delta;
            }else{
                vec.restartTime += Time.delta;
            }
        }

        if(b.timer(1, 5f)){
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15f, (build, direct) -> {
                if(direct){
                    build.damage(damage);
                }
                boolean hit = build.block.absorbLasers;
                if(hit){
                    b.fdata = Math.min(b.dst(build), b.fdata);
                }
                return hit;
            }, unit -> {
                unit.apply(status, statusDuration);
                float lh = unit.health;
                unit.damage(damage);
                boolean hit = unit.hitSize > 250f && lh > damage;
                if(hit){
                    b.fdata = Math.min(b.dst(unit), b.fdata);
                }
                return hit;
            }, (ex, ey) -> hit(b, ex, ey), true);
        }

        Effect.shake(1f, 1f, b);
    }

    @Override
    public void drawLight(Bullet b){
    }

    @Override
    public void draw(Bullet b){
        float l = b.fdata;

        Lines.lineAngle(b.x, b.y, b.rotation(), l);
    }
}
