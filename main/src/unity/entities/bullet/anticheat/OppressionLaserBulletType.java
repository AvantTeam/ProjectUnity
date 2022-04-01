package unity.entities.bullet.anticheat;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
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

    @Override
    public void update(Bullet b){
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
        float wid = this.width / 4f;
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
                Vec2 v = Tmp.v1.trns(b.rotation(), length + wid).add(b), v2 = Tmp.v2.trns(b.rotation(), cone).add(b);
                Lines.stroke(width * 2f);
                Lines.line(v2.x, v2.y, v.x, v.y, false);
                drawEndVoid(b, v.x, v.y, width);
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
                long seed = b.id * 9999L + 7813;
                for(int s : Mathf.signs){
                    float stroke = (2f + sin) * 1.5f * fow;
                    Vec2 v = Tmp.v1.trns(b.rotation(), length + wid, width * s).add(b), v2 = Tmp.v2.trns(b.rotation(), cone, width * s).add(b);
                    Lines.stroke(stroke);
                    Lines.line(v2.x, v2.y, v.x, v.y, false);
                    drawEndEdge(b, seed, v.x, v.y, width * s, stroke);
                    seed += rand.nextInt();
                }
                Vec2 v = Tmp.v1.trns(b.rotation(), length + wid).add(b);
                drawEndRed(b, v.x, v.y, width);
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

    void drawEndEdge(Bullet b, long seed, float x, float y, float width, float stroke){
        float spikeLength = this.width * 2.5f;
        float time = Time.time;
        rand.setSeed(b.id * 9999L + seed);
        Drawf.tri(x, y, stroke * 1.22f, Math.abs(width) / 4f, b.rotation());
        for(int i = 0; i < 14; i++){
            float d = rand.random(30f, 60 * 2f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;

            rand2.setSeed(timeSeed);
            float w = rand2.random(stroke * 3f, stroke * 5f);
            float ofRand = rand2.random(0.5f, 0.8f);
            float of = -width * Interp.pow3In.apply(fin) * ofRand;
            float l = w * 5f * rand2.random(1f, 2f) * Interp.pow3Out.apply(fin);
            float w2 = w * Mathf.slope(fin);
            float trns = (spikeLength * ofRand) + rand2.random(60f, 110f);
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * trns, of).add(x, y);
            drawDiamond(v.x, v.y, w2, l, b.rotation());
        }
    }

    void drawEndRed(Bullet b, float x, float y, float width){
        float spikeLength = this.width * 3f;
        rand.setSeed(b.id * 9999L + 17231);
        float time = Time.time;
        for(int i = 0; i < 27; i++){
            float d = rand.random(40f, 60 * 3f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;
            float fr = 1f - fin;

            rand2.setSeed(timeSeed);

            float w = rand2.random(width / 8f, width / 5f);
            float fr2 = Mathf.lerp(fr, 1f, rand2.random(0.1f, 0.6f));
            float w2 = w * Mathf.curve(fr, 0f, 0.7f) * Mathf.curve(fin, 0f, 0.3f);
            float l = w * 2f * rand2.random(2f, 2f) * Interp.pow3Out.apply(fin) + w * 2f * rand2.random(1f, 2f);
            float pos1 = rand2.range(1f);
            float pos = pos1 * Math.max(width - w2 / 2.2f, 0f);
            float sclL = 1 + Math.abs(pos1) * 0.25f;
            float trns = rand2.random(190f, 390f);
            float offset = ((1f - Math.abs(pos1)) * spikeLength) + rand2.random(-8f, 42f) - (trns * 0.3f * 0.3f);
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * sclL * trns + offset, pos * fr2).add(x, y);
            drawDiamond(v.x, v.y, w2, l, b.rotation());
        }
    }

    void drawEndVoid(Bullet b, float x, float y, float width){
        float spikeLength = this.width * 2.5f;
        float tx1 = Angles.trnsx(b.rotation() + 90f, width),
        ty1 = Angles.trnsy(b.rotation() + 90f, width),
        tx2 = Angles.trnsx(b.rotation(), spikeLength) + x,
        ty2 = Angles.trnsy(b.rotation(), spikeLength) + y;
        Fill.tri(x + tx1, y + ty1, x - tx1, y - ty1, tx2, ty2);

        rand.setSeed(b.id * 9999L + 1411);
        float time = Time.time;
        for(int i = 0; i < 22; i++){
            float d = rand.random(40f, 60 * 3f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;
            float fr = 1f - fin;

            rand2.setSeed(timeSeed);

            float w = rand2.random(width / 5f, width / 1.75f);
            float fr2 = Mathf.lerp(fr, 1f, rand2.random(0.1f, 0.6f));
            float w2 = w * Mathf.curve(fr, 0f, 0.8f) * Mathf.curve(fin, 0f, 0.2f);
            float l = w * 2f * rand2.random(0.8f, 2f) * Interp.pow3Out.apply(fin);
            float pos1 = rand2.range(1f);
            float pos = pos1 * Math.max(width - w2 / 2.05f, 0f);
            float sclL = 1 + Math.abs(pos1) * 0.25f;
            float trns = rand2.random(220f, 380f);
            float offset = ((1f - Math.abs(pos1)) * spikeLength) + rand2.random(-34f, 4f) - (trns * 0.2f * 0.2f);
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * sclL * trns + offset, pos * fr2).add(x, y);
            drawDiamond(v.x, v.y, w2, l, b.rotation());
        }
    }

    void drawDiamond(float x, float y, float w, float h, float r){
        float tx1 = Angles.trnsx(r + 90f, w), ty1 = Angles.trnsy(r + 90f, w),
        tx2 = Angles.trnsx(r, h), ty2 = Angles.trnsy(r, h);
        Fill.quad(x + tx1, y + ty1,
        x + tx2, y + ty2,
        x - tx1, y - ty1,
        x - tx2, y - ty2);
    }

    void drawLightning(Bullet b, int seed, float fin, float fout, float width){
        rand2.setSeed(seed * 2L + 856387231L);
        float time = b.time / 3f;
        float f2 = time % 1f;
        int timeSeed = (int)time;
        float pos = 0f, max = 0f;
        float pos2 = 0, max2 = 0f;
        float length = this.length + (this.width * 2f * rand2.nextFloat());

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
