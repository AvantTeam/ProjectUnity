package unity.entities.bullet.anticheat;

import arc.*;
import arc.graphics.*;
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
    private static final int detail = 24;
    private static final float[] quad = new float[8], shape = new float[4 * detail], ltmp = new float[38], ltmp2 = new float[38];
    private static final Rand rand = new Rand(), rand2 = new Rand();
    protected float length = 2150f, width = 150f, cone = 380f;
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

    static{
        int sign = 1;
        for(int i = 0; i < detail; i++){
            int id = i * 4;
            float de = detail - 1f;
            float f = Interp.circleIn.apply(i / de),
            w = Interp.circleOut.apply(f);
            for(int s : Mathf.signs){
                shape[id] = w * s * sign;
                shape[id + 1] = f;
                id += 2;
            }
            sign *= -1f;
        }
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
            Utils.collideLineLarge(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width, 24, false, (e, v) -> {
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
        boolean bloom = Core.settings.getBool("bloom");
        float fw = b.time < 50f ? Interp.pow2Out.apply(b.time / 50f) : 1f;
        float fow = Mathf.clamp((b.lifetime - b.time) / 120f);
        float inout = fw * fow;
        float width = this.width * inout + Mathf.absin(Time.time, 5f, 2f * inout);
        Draw.color(bloom ? Color.black : UnityPal.scarColor);
        if(!bloom) Draw.blend(UnityBlending.shadowRealm);
        for(int i = 0; i < shape.length; i += 4){
            if(i < shape.length - 4){
                for(int j = 0; j < quad.length; j += 2){
                    Vec2 v = Tmp.v1.trns(b.rotation(), shape[i + j + 1] * cone, shape[i + j] * width).add(b);
                    quad[j] = v.x;
                    quad[j + 1] = v.y;
                }
                Fill.quad(quad[0], quad[1],
                quad[2], quad[3],
                quad[4], quad[5],
                quad[6], quad[7]);
            }else{
                Vec2 v = Tmp.v1.trns(b.rotation(), length).add(b), v2 = Tmp.v2.trns(b.rotation(), cone).add(b);
                Lines.stroke(width * 2f);
                Lines.line(v2.x, v2.y, v.x, v.y, false);
            }
        }
        float sin = Mathf.absin(Time.time, 3f, 1f);
        Draw.color(UnityPal.scarColor, UnityPal.endColor, sin);
        Draw.blend();
        for(int i = 0; i < shape.length; i += 4){
            if(i < shape.length - 4){
                for(int j = 0; j < quad.length; j += 2){
                    Vec2 v = Tmp.v1.trns(b.rotation(), shape[i + j + 1] * cone, shape[i + j] * width).add(b);
                    quad[j] = v.x;
                    quad[j + 1] = v.y;
                }
                Lines.stroke((2f + sin) * 1.5f * fow);
                Lines.line(quad[0], quad[1], quad[6], quad[7], false);
                Lines.line(quad[2], quad[3], quad[4], quad[5], false);
            }else{
                for(int s : Mathf.signs){
                    Vec2 v = Tmp.v1.trns(b.rotation(), length, width * s).add(b), v2 = Tmp.v2.trns(b.rotation(), cone, width * s).add(b);
                    Lines.stroke((2f + sin) * 1.5f * fow);
                    Lines.line(v2.x, v2.y, v.x, v.y, false);
                }
            }
        }
        float time = b.time;
        rand.setSeed(b.id * 9999L);
        for(int i = 0; i < 5; i++){
            float d = rand.random(90f, 60 * 6f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;
            drawLightning(b, timeSeed, fin, fow, width);
        }
        Draw.blend();
        Draw.reset();
    }

    void drawLightning(Bullet b, int seed, float fin, float fout, float width){
        float time = b.time / 3f;
        float f2 = time % 1f;
        int timeSeed = (int)time;
        float pos = 0f, max = 0f;
        float pos2 = 0, max2 = 0f;

        for(int i = 0; i < ltmp.length; i++){
            rand2.setSeed(seed + Mathf.mod((timeSeed - i), 999999) * 9999L);
            float r = rand2.range(1.5f);
            rand2.setSeed(seed + Mathf.mod((timeSeed - (i + 1)), 999999) * 9999L);
            float r2 = rand2.range(1.5f);
            pos += r;
            pos2 += r2;
            ltmp[i] = pos;
            ltmp2[i] = pos2;
        }
        float delta = (pos / ltmp.length) * 2f;
        float delta2 = (pos2 / ltmp.length) * 2f;
        for(int i = 0; i < ltmp.length; i++){
            float v = ltmp[i] - delta * i;
            float v2 = ltmp2[i] - delta2 * i;
            ltmp[i] = v;
            ltmp2[i] = v2;
            max = Math.max(max, Math.abs(v));
            max2 = Math.max(max2, Math.abs(v2));
        }
        float lx = b.x, ly = b.y;
        Lines.stroke((1f - fin) * 6f * fout);
        for(int i = 1; i < ltmp.length; i++){
            float v = (ltmp[i] / max) * width;
            float v2 = (ltmp2[i] / max2) * width;
            float w = Mathf.lerp(v, v2, 1f - f2);
            Vec2 nv = Tmp.v1.trns(b.rotation(), (i / (ltmp.length - 1f)) * length, w).add(b);
            Lines.line(lx, ly, nv.x, nv.y, false);
            lx = nv.x;
            ly = nv.y;
        }
    }
}
