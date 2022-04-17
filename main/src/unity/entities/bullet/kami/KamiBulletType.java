package unity.entities.bullet.kami;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.content.effects.*;
import unity.gen.*;

public class KamiBulletType extends BulletType{
    public static TextureRegion region;
    public float delay = -1f;

    public KamiBulletType(){
        speed = 1f;
        damage = 9f;
        absorbable = false;
        hittable = false;
        collidesTiles = false;
        pierce = true;
        keepVelocity = false;
        shootEffect = SpecialFx.kamiBulletSpawn;
    }

    @Override
    public void load(){
        if(region == null) region = Core.atlas.find("circle");
    }

    @Override
    public void draw(Bullet b){
        KamiBullet kb = (KamiBullet)b;
        if(!kb.isTelegraph()){
            float time = (b.time * 2f) + (Time.time / 2f);
            float st = Mathf.clamp(Math.max(kb.width, kb.length) / 10f + 1.2f, 1.5f, 4f) * (1f + Mathf.absin(time, 10f, 0.33f));
            Tmp.c1.set(Color.red).shiftHue(time);
            drawTrail(b);
            Draw.color(Tmp.c1);
            Draw.rect(region, b.x, b.y, (kb.width * 2f) + st, (kb.length * 2f) + st, b.rotation());
            Draw.color(Color.white);
            Draw.rect(region, b.x, b.y, kb.width * 2f, kb.length * 2f, b.rotation());
        }else{
            Draw.blend(Blending.additive);
            FloatSeq seq = kb.lastPositions;
            float time = Time.time / 2f;
            float fout = 1f - Mathf.clamp(b.time - (b.lifetime - 40f)) / 40f;
            int max = (seq.size / 2) - 1;
            for(int i = 0; i < seq.size - 2; i += 2){
                int s = i / 2;
                float fin = (s + 1f) / max;
                float x1 = seq.get(i), y1 = seq.get(i + 1), x2 = seq.get(i + 2), y2 = seq.get(i + 3);
                Tmp.c1.set(Color.red).shiftHue(time).a(fin);
                Draw.color(Tmp.c1);
                Lines.stroke(3f);
                Lines.line(x1, y1, x2, y2, false);
                time += b.time * 2 / 3f;
            }
            Draw.blend();
        }
    }

    @Override
    public void drawTrail(Bullet b){
        if(trailLength > 0 && b.trail != null){
            KamiBullet kb = (KamiBullet)b;
            float z = Draw.z();
            Draw.z(z - 0.0001f);
            Draw.blend(Blending.additive);
            b.trail.draw(Tmp.c1, kb.width);
            Draw.blend();
            Draw.z(z);
        }
    }

    @Override
    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        KamiBullet bullet = KamiBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.vel.trns(angle, speed * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.width = hitSize;
        bullet.length = hitSize;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        if(delay > 0f){
            shootEffect.at(x, y, angle, bullet);
            Time.run(delay, bullet::add);
        }else{
            bullet.add();
        }
        return bullet;
    }
}
