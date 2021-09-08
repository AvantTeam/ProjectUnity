package unity.entities.bullet.energy;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.effects.*;

public class EphemeronPairBulletType extends BasicBulletType{
    public boolean positive;

    public EphemeronPairBulletType(float damage){
        super(0.001f, damage);

        lifetime = 360f;
        hitEffect = Fx.hitLancer;
        despawnEffect = Fx.none;
        hitSound = Sounds.spark;
        hitSize = 8f;
        drag = 0.015f;
        pierce = true;
        hittable = absorbable = reflectable = collidesTiles = false;
    }

    @Override
    public void draw(Bullet b){
        Draw.color(frontColor);
        Fill.circle(b.x, b.y, 4f + (b.fout() * 1.5f));
        Draw.color(backColor);
        Fill.circle(b.x, b.y, 2.5f + (b.fout()));
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.data instanceof Bullet n && n.added){
            float dst = hitSize / Math.max(b.dst(n) / 2f, hitSize);
            Tmp.v1.set(n).sub(b).nor().scl(dst);
            b.vel.add(Tmp.v1);

            if(!positive) return;

            b.hitbox(Tmp.r1);
            n.hitbox(Tmp.r2);

            if(Tmp.r1.overlaps(Tmp.r2)){
                b.remove();
                n.remove();

                Tmp.v1.set((b.x + n.x) / 2f, (b.y + n.y) / 2f);

                HitFx.LightHitLarge.at(Tmp.v1);
                Damage.damage(b.team, Tmp.v1.x, Tmp.v1.y, 40f, 80f);
            }
        }else{
            b.absorb();
        }
    }
}