package unity.entities.bullet.kami;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.gen.*;

public class NewKamiLaserBulletType extends BulletType{
    static TextureRegion hcircle;

    public NewKamiLaserBulletType(){
        speed = 0f;
        damage = 9f;
        absorbable = false;
        hittable = false;
        collidesTiles = false;
        pierce = true;
        keepVelocity = false;
    }

    @Override
    public void load(){
        hcircle = Core.atlas.find("hcircle");
    }

    @Override
    public void draw(Bullet b){
        KamiLaser lb = (KamiLaser)b;
        TextureRegion r = KamiBulletType.region;
        float time = (b.time * 2f) + (Time.time / 2f);
        Tmp.c1.set(Color.red).shiftHue(time);
        if(lb.ellipseCollision){
            Vec2 v = Tmp.v1.set(lb.x, lb.y).sub(lb.x2, lb.y2).setLength(3f);
            Lines.stroke((lb.width + 3.5f) * 2f);
            Draw.color(Tmp.c1);
            Lines.line(r, lb.x + v.x, lb.y + v.y, lb.x2 - v.x, lb.y2 - v.y, false);
            Draw.color();
            Lines.stroke(lb.width * 2f);
            Lines.line(r, lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.reset();
        }else{
            float ang = lb.angleTo(lb.x2, lb.y2);
            Draw.blend(Blending.additive);
            Draw.color(Tmp.c1);
            Lines.stroke(lb.width * 2f);
            Lines.line(lb.x, lb.y, lb.x2, lb.y2, false);
            Draw.rect(hcircle, lb.x, lb.y, lb.width * 2, lb.width * 2f, ang + 180f);
            Draw.rect(hcircle, lb.x2, lb.y2, lb.width * 2, lb.width * 2f, ang);
            Draw.blend();
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    public KamiLaser createL(Entityc owner, Team team, float x, float y, float x2, float y2, Object data){
        KamiLaser b = (KamiLaser)create(owner, team, x, y, 0f, -1f, 1f, 1f, data);
        b.x2 = x2;
        b.y2 = y2;
        return b;
    }

    @Override
    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        KamiLaser bullet = KamiLaser.create();
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
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();
        return bullet;
    }
}
