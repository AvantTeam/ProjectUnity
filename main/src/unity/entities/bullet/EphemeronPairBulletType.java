package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.content.*;

public class EphemeronPairBulletType extends BasicBulletType{
    public boolean positive;

    public EphemeronPairBulletType(float damage){
        super(0.001f, damage);

        lifetime = 300f;
        hitEffect = Fx.hitLancer;
        despawnEffect = Fx.none;
        hitSound = Sounds.spark;
        hitSize = 8f;
        pierce = true;
        collidesTiles = false;
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

        if(positive && b.data instanceof Bullet n){
            b.time(0f);
            
            Tmp.v1.trns(b.angleTo(n), Time.delta);
            b.vel.add(Tmp.v1);
            
            Tmp.v1.rotate(180f);
            n.vel.add(Tmp.v1);

            if(b.vel.len() > b.dst(n.x + n.vel.x, n.y + n.vel.y)){
                b.vel.setLength(b.dst(n.x + n.vel.x, n.y + n.vel.y));
            }

            if(n.vel.len() > n.dst(b.x + b.vel.x, b.y + b.vel.y)){
                n.vel.setLength(n.dst(b.x + b.vel.x, b.y + b.vel.y));
            }

            b.hitbox(Tmp.r1);
            n.hitbox(Tmp.r2);

            if(Tmp.r1.overlaps(Tmp.r2)){
                b.remove();
                n.remove();

                Tmp.v1.set((b.x + n.x) / 2f, (b.y + n.y) / 2f);

                UnityFx.ephemeronHit.at(Tmp.v1);
                Damage.damage(b.team, Tmp.v1.x, Tmp.v1.y, 40f, 80f);
            }
        }else{
            b.absorb();
        }
    }
}