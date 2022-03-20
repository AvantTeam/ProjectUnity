package unity.entities.bullet.anticheat;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import unity.content.effects.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.graphics.*;
import unity.util.*;

public class OppressionLaserBulletType extends AntiCheatBulletTypeBase{
    private static final float[] quad = new float[8];
    protected float length = 1400f, width = 140f, cone = 380f;
    protected TextureRegion gradientRegion;

    public OppressionLaserBulletType(){
        speed = 0f;
        damage = 4000f;
        ratioStart = 100000f;
        ratioDamage = 1f / 60f;
        overDamage = 650000f;
        overDamagePower = 2.7f;
        overDamageScl = 4000f;
        bleedDuration = 10f * 60f;
        despawnEffect = Fx.none;
        hitEffect = HitFx.endHitRedBig;
        hittable = collides = absorbable = keepVelocity = false;
        impact = true;
        pierceShields = pierce = true;
        lifetime = 5f * 60f;

        modules = new AntiCheatBulletModule[]{
            new ArmorDamageModule(0.002f, 4f, 20f, 4f),
            new AbilityDamageModule(40f, 350f, 6f, 0.001f, 4f),
            new ForceFieldDamageModule(8f, 20f, 220f, 6f, 1f / 40f)
        };
    }

    @Override
    public void load(){
        gradientRegion = Core.atlas.find("unity-gradient");
    }

    @Override
    public float range(){
        return length / 3f;
    }

    @Override
    public void init(){
        super.init();
        drawSize = length * 2f;
    }

    /*
    @Override
    public void init(Bullet b){
        super.init(b);
        if(b.owner instanceof Unit){
            Unit u = (Unit)b.owner;
            b.data = new Vec3(u.x, u.y, u.rotation);
        }
    }
     */

    @Override
    public void update(Bullet b){
        /*
        if(b.data instanceof Vec3 && b.owner instanceof Unit){
            Unit u = (Unit)b.owner;
            Vec3 v = (Vec3)b.data;
            Tmp.v1.set(u).lerpDelta(v.x, v.y, 0.2f);
            u.set(Tmp.v1);
            v.z = Angles.moveToward(v.z, u.rotation, 0.75f);
            u.rotation = v.z;
        }
        */
        if(b.timer(1, 5f)){
            float fw = b.time < 50f ? Interp.pow2Out.apply(b.time / 50f) : 1f;
            float fow = Mathf.clamp((b.lifetime - b.time) / 120f);
            float width = this.width * fw * fow;
            Tmp.v1.trns(b.rotation(), length).add(b);
            Utils.collideLineLarge(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width, 18, false, (e, v) -> {
                float dst = b.dst(v);
                float w = dst >= cone ? width : Interp.circleOut.apply((dst / cone)) * width;
                if(e.within(v, w + e.hitSize() / 2f)){
                    Tmp.r1.setCentered(e.getX(), e.getY(), e.hitSize()).grow(width * 2f);
                    Vec2 hv = Geometry.raycastRect(b.x, b.y, Tmp.v1.x, Tmp.v1.y, Tmp.r1);
                    if(hv != null){
                        float hs = e.hitSize() / 2f;
                        float scl = Math.max((hs - width) / hs, hs / width);
                        hv.sub(e).scl(scl).add(e);
                        v.set(hv);
                        return true;
                    }
                }
                return false;
            }, (x, y, e, d) -> {
                hit(b, x, y);
                if(e instanceof Unit){
                    hitUnitAntiCheat(b, (Unit)e);
                }else if(e instanceof Building){
                    hitBuildingAntiCheat(b, (Building)e);
                }
                return false;
            });
        }
    }

    @Override
    public void drawLight(Bullet b){
    }

    @Override
    public void draw(Bullet b){
        float fw = b.time < 50f ? Interp.pow2Out.apply(b.time / 50f) : 1f;
        float fow = Mathf.clamp((b.lifetime - b.time) / 120f);
        float fwr = 1f - fw;
        float width = this.width * fw * fow + Mathf.absin(Time.time, 5f, 2f * fw * fow);
        Draw.color(UnityPal.scarColor);
        for(int i = 0; i < 24; i++){
            float fin1 = Interp.circleIn.apply(i / 24f), fin2 = Interp.circleIn.apply(((i + 1f) / 24f));
            float w1 = Interp.circleOut.apply(fin1) * width,
            w2 = Interp.circleOut.apply(fin2) * width;
            int q = 0;
            for(int j = 0; j < 2; j++){
                for(int s : Mathf.signs){
                    if(j == 1){
                        s *= -1;
                        Vec2 v = Tmp.v1.trns(b.rotation(), fin2 * cone, w2 * s).add(b);
                        quad[q] = v.x;
                        quad[q + 1] = v.y;
                    }else{
                        Vec2 v = Tmp.v1.trns(b.rotation(), fin1 * cone, w1 * s).add(b);
                        quad[q] = v.x;
                        quad[q + 1] = v.y;
                    }
                    q += 2;
                }
            }
            Draw.blend(UnityBlending.shadowRealm);
            Fill.quad(quad[0], quad[1],
            quad[2], quad[3],
            quad[4], quad[5],
            quad[6], quad[7]);
            if(fwr > 0.0001f){
                float z = Draw.z();
                Draw.blend();
                Draw.z(z + 0.0001f);
                Lines.stroke(fwr * 3f);
                Lines.line(quad[0], quad[1], quad[6], quad[7], false);
                Lines.line(quad[2], quad[3], quad[4], quad[5], false);
                Draw.z(z);
            }
        }
        Vec2 v = Tmp.v1.trns(b.rotation(), length).add(b), v2 = Tmp.v2.trns(b.rotation(), cone).add(b);
        Draw.blend(UnityBlending.shadowRealm);
        Lines.stroke(width * 2f);
        Lines.line(v2.x, v2.y, v.x, v.y, false);
        float z = Draw.z();
        Draw.z(z + 0.0001f);
        Draw.blend();
        if(fwr > 0.0001f){
            Vec2 v3 = Tmp.v3.trns(b.rotation() + 90f, width);
            Lines.stroke(fwr * 3f);
            Lines.line(v2.x + v3.x, v2.y + v3.y, v.x + v3.x, v.y + v3.y);
            Lines.line(v2.x - v3.x, v2.y - v3.y, v.x - v3.x, v.y - v3.y);
        }
        Draw.z(z);
        Draw.blend();
        Draw.reset();
    }
}
