package unity.entities.bullet.misc;

import arc.*;
import arc.audio.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.effects.*;

public class ShootingBulletType extends BulletType{
    public float targetRange = 220f, minTargetRange = 90f;
    public float smoothness = 35f;
    public float reloadTime = 20f, shootInaccuracy = 0f;
    public BulletType shootBullet = Bullets.standardCopper;
    public Sound shootSound = Sounds.none;
    public String name;
    protected TextureRegion region;

    public ShootingBulletType(String name, float speed, float damage){
        super(speed, damage);
        this.name = name;
        hittable = true;
        absorbable = false;
        collides = collidesTiles = false;
        drag = 0.05f;
        trailLength = 4;
        shootEffect = ShootFx.plagueShootSmokeLarge;
        hitEffect = HitFx.plagueLargeHit;
        trailChance = 0.2f;

        splashDamage = 30f;
        splashDamageRadius = 40f;
    }

    @Override
    public void load(){
        region = Core.atlas.find(name);
    }

    @Override
    public float estimateDPS(){
        float sum = splashDamage * 0.75f;
        if(fragBullet != null && fragBullet != this){
            sum += fragBullet.estimateDPS() * fragBullets / 2f;
        }
        return sum;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.fdata = b.rotation();
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.timer(1, 5f)){
            b.data = Units.closestTarget(b.team, b.x, b.y, targetRange);
        }
        if(b.data instanceof Healthc && Units.invalidateTarget((Healthc)b.data, b.team, b.x, b.y, targetRange * 1.1f)){
            b.data = null;
        }
        if(b.data instanceof Sized){
            Sized t = (Sized)b.data;
            float angTo = b.angleTo(t);
            int side = Mathf.randomSeed(b.id * 913L + (int)(b.time / 90f), 0, 1) == 0 ? -1 : 1;
            b.fdata = Angles.moveToward(b.fdata, angTo, 3f);
            Tmp.v2.trns(angTo + 180f + (side * speed / 2f), minTargetRange + (t.hitSize() / 2f)).add(t);
            Tmp.v1.set(Tmp.v2).sub(b).limit(speed).scl(1f / smoothness);
            b.vel.add(Tmp.v1).limit(speed);

            if(Angles.within(b.fdata, angTo, 2f) && b.within(t, shootBullet.range()) && b.timer(2, reloadTime)){
                shootBullet.shootEffect.at(b.x, b.y, b.fdata);
                shootSound.at(b);
                shootBullet.create(b, b.team, b.x, b.y, b.fdata + Mathf.range(shootInaccuracy));
            }
        }else{
            b.fdata = Mathf.slerpDelta(b.fdata, b.rotation(), 0.1f);
        }
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.rect(region, b.x, b.y, b.fdata - 90f);
    }
}
