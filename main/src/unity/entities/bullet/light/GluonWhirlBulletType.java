package unity.entities.bullet.light;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.entities.bullet.light.GluonOrbBulletType.*;

public class GluonWhirlBulletType extends BasicBulletType{
    public float force = 8f, scaledForce = 7f, radius = 100f;
    
    public GluonWhirlBulletType(float damage){
        super(0.001f, damage);

        pierce = pierceBuilding = true;
        despawnEffect = hitEffect = Fx.none;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        b.data = new GluonOrbData();
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(!(b.data instanceof GluonOrbData data)) return;

        if(Mathf.chance(Time.delta * 0.7f * b.fout())){
            UnityFx.whirl.at(b);
        }

        if(b.timer(0, 2f)){
            data.units.clear();
            Units.nearbyEnemies(b.team, b.x - radius, b.y - radius, radius * 2, radius * 2, u -> {
                if(u != null && Mathf.within(b.x, b.y, u.x, u.y, radius)){
                    /*Tmp.v1.trns(u.angleTo(b), force + (1f - u.dst(b) / radius) * scaledForce * b.fout(Interp.pow2In) * (u.isFlying() ? 1.5f : 1f));

                    u.impulse(Tmp.v1);*/
                    data.units.add(u);
                }
            });

            Damage.damage(b.team, b.x, b.y, hitSize, damage);
        }

        data.units.each(u -> {
            if(!u.dead){
                Tmp.v1.trns(u.angleTo(b), force + (1f - u.dst(b) / radius) * scaledForce * b.fout(Interp.pow2In) * (u.isFlying() ? 1.5f : 1f)).scl(20f * Time.delta);

                u.impulse(Tmp.v1);
            }
        });
    }

    @Override
    public void draw(Bullet b){
        Draw.color(Pal.lancerLaser);
        Fill.circle(b.x, b.y, b.fout() * 7.5f);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, b.fout() * 5.5f);
    }
}