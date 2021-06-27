package unity.entities.abilities;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.*;

import static arc.Core.*;

public class BulletReflectPulseAbility extends Ability{
    protected int maxReflect = 1;
    protected float reload = 100f, range = 60f, reflectTime = 60f, maxDamage = 100f;
    protected Effect healEffect = Fx.heal;
    protected Effect pauseEffect = UnityFx.reflectPulseDynamic;
    protected Effect resumeEffect = UnityFx.reflectResumeDynamic;
    protected Effect activeEffect = UnityFx.reflectPulseDynamic;

    protected float reloadTimer;
    protected float timer;
    protected boolean active;
    protected Seq<Bullet> bullets = new Seq<>();
    protected Seq<Object> bulletTimes = new Seq<>();
    protected Seq<Object> bulletSpeeds = new Seq<>();
    protected float dmg;
    
    //Damage calculator made by Ilya246. Translated to Java by MEEP. I use this for the ramping on Luminocity in Progressed Materials.
    protected float bulletDamage(Bullet b){
        BulletType type = b.type;
        float dmg;
        if(type.fragBullet == null){
            dmg = b.damage + type.splashDamage + type.lightningDamage * type.lightning * type.lightningLength;
        }else{
            dmg = b.damage + type.splashDamage + type.lightningDamage * type.lightning * type.lightningLength + bulletDamage(type.fragBullet) * type.fragBullets;
        }
        if(type instanceof ContinuousLaserBulletType){
            return dmg * b.lifetime / 5;
        }else{
            return dmg;
        }
    }
    
    protected float bulletDamage(BulletType b){
        if(b.fragBullet == null){
            return b.damage + b.splashDamage + b.lightningDamage * b.lightning * b.lightningLength;
        }else{
            return b.damage + b.splashDamage + b.lightningDamage * b.lightning * b.lightningLength + bulletDamage(b.fragBullet) * b.fragBullets;
        }
    }

    BulletReflectPulseAbility(){}

    public BulletReflectPulseAbility(int maxReflect, float reload, float range, float reflectTime, float maxDamage){
        this.maxReflect = maxReflect;
        this.reload = reload;
        this.range = range;
        this.reflectTime = reflectTime;
        this.maxDamage = maxDamage;
    }

    @Override
    public void update(Unit unit){
        timer += Time.delta;

        if(timer >= reload + reflectTime){
            bullets.clear();
            bulletTimes.clear();
            bulletSpeeds.clear();
            
            Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2f, range * 2f, b -> {
                if(b != null && unit.team != b.team && Mathf.within(unit.x, unit.y, b.x, b.y, range)){
                    BulletType type = b.type;
                    if(type.speed > 0.01 && bulletDamage(b) <= maxDamage){
                        bullets.add(b);
                        //Unity.print("Grabbing Bullets");
                    }
                }
            });

            bullets.sort(e -> -bulletDamage(e));
            //Unity.print(bullets);
            for(int i = Math.min(maxReflect, bullets.size - 1); i < bullets.size - 1; i++){
                bullets.remove(i);
            }

            for(int i = 0; i < bullets.size - 1; i++){
                Bullet target = bullets.get(i);
                if(target != null && target.type != null){
                    bulletTimes.add(target.time);
                    bulletSpeeds.add(target.vel.len());
                    target.vel.trns(target.vel.angle(), 0.001f);
                    pauseEffect.at(target, target.type.hitSize * 4f);
                }
                //Unity.print("Stopping Bullets");
                active = true;
            }

            Time.run(reflectTime, () -> {
                for(int i = 0; i < bullets.size - 1; i++){
                    Bullet target = bullets.get(i);
                    if(target != null && target.type != null){
                        //Unity.print(target + " -> " + "(" + target.x + ", " + target.y + ")" + " / Size: " + target.type.hitSize);
                        target.team = unit.team;
                        resumeEffect.at(target, target.type.hitSize * 4f);
                        target.vel.trns(Angles.angle(unit.x, unit.y, target.x, target.y), (float)bulletSpeeds.get(i));
                    }
                    //Unity.print("Reflecting Bullets");
                }
                active = false;
            });

            activeEffect.at(unit, range);

            timer = 0f;

            //Unity.print("------------------------");
        }

        if(active){
            for(int i = 0; i < bullets.size - 1; i++){
                Bullet target = bullets.get(i);
                target.time = (float)bulletTimes.get(i);
            }
        }
    }

    @Override
    public String localized(){
        return bundle.get("ability.reflect-pulse-ability");
    }
}