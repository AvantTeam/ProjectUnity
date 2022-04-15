package unity.entities.bullet.anticheat;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.graphics.*;
import unity.util.*;

import static unity.graphics.UnityDrawf.*;

public class OppressionLaserBulletType extends AntiCheatBulletTypeBase{
    private static final int detail = 24;
    private static final float timeMul = 4f;
    private static final float[] quad = new float[8], shape = new float[4 * detail], ltmp = new float[25], ltmp2 = new float[25];
    private static final Rand rand = new Rand(), rand2 = new Rand();
    private static final FloatSeq lines = new FloatSeq();
    private static final Color[] lightningColors = {Color.white, UnityPal.scarColor, Color.black};
    protected float length = 2150f, width = 140f, cone = 380f, endLength = 450f;
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
        lifetime = 8f * 60f;
        knockback = 7f;
        shootEffect = ChargeFx.oppressionCharge;

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
            Tmp.v1.trns(b.rotation(), length + endLength).add(b);
            Utils.collideLineLarge(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width, 24, false, (e, v) -> {
                float dst = b.dst(v);
                //float w = dst >= cone ? width : Interp.circleOut.apply((dst / cone)) * width;
                float w = getWidthCollision(dst, width);
                if(w > 0f && e.within(v, w + e.hitSize() / 2f)){
                    Tmp.r1.setCentered(e.getX(), e.getY(), e.hitSize()).grow(width * 2f);
                    Vec2 hv = Geometry.raycastRect(b.x, b.y, Tmp.v1.x, Tmp.v1.y, Tmp.r1);
                    if(hv != null){
                        float hs = e.hitSize() / 2f;
                        float scl = Math.max((hs - width) / hs, Math.min(hs / width, 1f));
                        hv.sub(e).scl(scl).add(e);
                        v.set(hv);
                        return true;
                    }
                }
                return false;
            }, (x, y, e, d) -> {
                hit(b, x, y);
                if(e instanceof Sized){
                    HitFx.endDeathLaserHit.at(x, y, b.angleTo(e), ((Sized)e).hitSize());
                }
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
        float wid = this.width * 0.125f;
        float fw = b.time < 50f ? Interp.pow2Out.apply(b.time / 50f) : 1f;
        float fow = Mathf.clamp((b.lifetime - b.time) / 120f);
        float inout = fw * fow;
        float width = this.width * inout + Mathf.absin(Time.time, 5f, 2f * inout);
        float sin = Mathf.absin(Time.time, 3f, 0.35f);
        Color col = Tmp.c1.set(UnityPal.scarColor).mul(1f + sin);
        Draw.color(col);
        Draw.blend();
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
        long seed = b.id * 9999L + 7813;
        for(int s : Mathf.signs){
            float stroke = (2f + (sin / 0.35f)) * 1.5f * fow;
            Vec2 v = Tmp.v1.trns(b.rotation(), length + wid, width * s).add(b);
            drawEndEdge(b, seed, v.x, v.y, width * s, stroke);
            seed += rand.nextInt();
        }

        rand.setSeed(b.id * 9999L + 8957324);
        float spikeLength = endLength;
        float time = Time.time;

        for(int i = 0; i < 18; i++){
            boolean alt = i < 8;
            float as = alt ? 1f : 3f;
            float wid2 = alt ? width : width / 1.5f;
            float d = alt ? rand.random(12f, 22f) : rand.random(22f, 60f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;

            rand2.setSeed(timeSeed);
            Draw.color(Color.white);

            float delay = rand2.random(0.55f, 0.8f);
            float pos1 = Mathf.pow(rand2.nextFloat(), 2f);
            float w = rand2.random(2f, 7f + (1f - pos1) * 3f) * as * Mathf.lerp(1f, rand2.random(0.8f, 1.2f), fin);
            float l = rand2.random(length / 2f);
            float pos2 = pos1 * Math.max(wid2 - (w * 2f), 0f) * Mathf.sign(rand2.chance(0.5f));

            float trns = ((length + spikeLength * (1f - pos1)) - l) * rand2.random(1f - (pos1 * 0.25f), 1.1f);
            float f1 = Mathf.curve(fin, 0f, 1f - delay), f2 = Mathf.curve(fin, delay, 1f);
            Interp p = Interp.pow2In;
            Lines.stroke(w * inout);
            drawLine(b, p.apply(f1) * trns + l, p.apply(f2) * trns + l, pos2);
        }

        for(int i = 0; i < 40; i++){
            boolean alt = i < 17;
            float as = alt ? 1f : 2.25f;
            float wid2 = alt ? width : width / 1.5f;
            float d = alt ? rand.random(16f, 27f) : rand.random(34f, 65f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;

            rand2.setSeed(timeSeed);
            Draw.color(rand2.chance(0.75f) ? Color.black : col);

            float w = rand2.random(20f, 35f) * inout * as * Mathf.slope(fin);
            float l = rand2.random(90f, 190f) * as * (alt ? 1f : 1.5f);
            float p1 = Mathf.pow(rand2.nextFloat(), 2f);
            float trns = rand2.random(length / 12f, length / 5f);
            float yps = rand2.random(l, Math.max((length + spikeLength * (1f - p1)) - (l + trns), l));
            float xps = (p1 * Math.max(wid2 - w, 0f) * Mathf.sign(rand2.chance(0.5f))) + rand2.range(8f) * fin;
            float wScl = getWidth(yps, 1f);
            Interp p = Interp.pow2In;
            Tmp.v1.trns(b.rotation(), trns * p.apply(fin) + yps, xps * wScl).add(b);
            diamond(Tmp.v1.x + Mathf.range(6f) * fin, Tmp.v1.y + Mathf.range(6f) * fin, w, l, b.rotation());
        }

        for(int i = 0; i < 20; i++){
            boolean alt = i < 12;
            float as = alt ? 1f : 3f;
            float wid2 = alt ? width : width / 2f;
            float d = alt ? rand.random(12f, 22f) : rand.random(22f, 60f);
            float timeOffset = rand.random(d);
            int timeSeed = Mathf.floor((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;

            rand2.setSeed(timeSeed);
            Draw.color(rand2.chance(alt ? 0.5f : 0.75f) ? Color.black : col);

            float delay = rand2.random(0.55f, 0.8f);
            float pos1 = Mathf.pow(rand2.nextFloat(), 2f);
            float w = rand2.random(2f, 7f + (1f - pos1) * 3f) * as * Mathf.lerp(1f, rand2.random(0.8f, 1.2f), fin);
            float l = rand2.random(length / 2f);
            float pos2 = pos1 * Math.max(wid2 - (w * 2f), 0f) * Mathf.sign(rand2.chance(0.5f));

            float trns = ((length + spikeLength * (1f - pos1)) - l) * rand2.random(1f - (pos1 * 0.25f), 1.1f);
            float f1 = Mathf.curve(fin, 0f, 1f - delay), f2 = Mathf.curve(fin, delay, 1f);
            Interp p = Interp.pow2In;
            Lines.stroke(w * inout);
            drawLine(b, p.apply(f1) * trns + l, p.apply(f2) * trns + l, pos2);
        }

        rand.setSeed(b.id * 999L + 7452);
        for(int i = 0; i < 5; i++){
            float d = rand.random(30f, 50f);
            float timeOffset = rand.random(d);
            int timeSeed = (int)((time + timeOffset) / d) + rand.nextInt();
            float fin = ((time + timeOffset) % d) / d;
            drawLightning(b, timeSeed, fin, fow, width);
        }

        Draw.blend();
        Draw.reset();
    }

    void drawLine(Bullet b, float l1, float l2, float width){
        lines.clear();
        float sw = Lines.getStroke();
        float s = Mathf.clamp((sw - 3f) / 2f);
        if(l1 < l2){
            float l = l1;
            l1 = l2;
            l2 = l;
        }
        float h = Math.min(cone, l1) - l2;
        if(h > 0){
            float l = l2;
            while(l < Math.min(cone, l1)){
                Tmp.v1.trns(b.rotation(), l, getWidth(l, width)).add(b);
                lines.add(Tmp.v1.x, Tmp.v1.y);
                l += 4f;
            }
            if(l1 > cone){
                Tmp.v1.trns(b.rotation(), cone, width).add(b);
                lines.add(Tmp.v1.x, Tmp.v1.y);
            }
        }else{
            Tmp.v1.trns(b.rotation(), l2, width).add(b);
            lines.add(Tmp.v1.x, Tmp.v1.y);
        }
        Tmp.v1.trns(b.rotation(), l1, getWidth(l1, width)).add(b);
        lines.add(Tmp.v1.x, Tmp.v1.y);

        for(int i = 0; i < lines.size - 2; i += 2){
            float x1 = lines.get(i), y1 = lines.get(i + 1), x2 = lines.get(i + 2), y2 = lines.get(i + 3);
            Lines.line(x1, y1, x2, y2, false);
            if(sw > 3f){
                if(i == 0){
                    Drawf.tri(x1, y1, sw * 1.22f, sw * s * 2f, Angles.angle(x2, y2, x1, y1));
                }
                if(i == lines.size - 4){
                    Drawf.tri(x2, y2, sw * 1.22f, sw * s * 2f, Angles.angle(x1, y1, x2, y2));
                }
            }
        }
    }

    float getWidth(float length, float width){
        return length >= cone ? width : Interp.circleOut.apply((length / cone)) * width;
    }

    float getWidthCollision(float length, float width){
        return length < this.length ? getWidth(length, width) : Mathf.clamp(1f - (length - this.length) / endLength) * width;
    }

    void drawEndEdge(Bullet b, long seed, float x, float y, float width, float stroke){
        float spikeLength = endLength;
        float time = Time.time * timeMul;
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
            diamond(v.x, v.y, w2, l, b.rotation());
        }
    }

    void drawEndVoid(Bullet b, float x, float y, float width){
        float spikeLength = endLength;
        float tx1 = Angles.trnsx(b.rotation() + 90f, width),
        ty1 = Angles.trnsy(b.rotation() + 90f, width),
        tx2 = Angles.trnsx(b.rotation(), spikeLength) + x,
        ty2 = Angles.trnsy(b.rotation(), spikeLength) + y;
        Fill.tri(x + tx1, y + ty1, x - tx1, y - ty1, tx2, ty2);

        rand.setSeed(b.id * 9999L + 1411);
        float time = Time.time * timeMul;
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
            float l = w * 3f * rand2.random(0.8f, 2f) * Interp.pow3Out.apply(fin);
            float pos1 = rand2.range(1f);
            float pos = pos1 * Math.max(width - w2 / 2.05f, 0f);
            float sclL = 1 + Math.abs(pos1) * 0.25f;
            float trns = rand2.random(220f, 380f);
            float offset = ((1f - Math.abs(pos1)) * spikeLength) + rand2.random(-34f, 4f) - (trns * 0.2f * 0.2f);
            Vec2 v = Tmp.v3.trns(b.rotation(), fin * fin * sclL * trns + offset, pos * fr2).add(x, y);
            diamond(v.x + Mathf.range(6f) * fin, v.y + Mathf.range(6f) * fin, w2, l, b.rotation());
        }
    }

    void drawLightning(Bullet b, int seed, float fin, float fout, float width){
        rand2.setSeed(seed * 2L + 856387231L);
        float time = b.time / 3f;
        float f2 = time % 1f;
        int timeSeed = (int)time;
        float pos = 0f, max = 0f;
        float pos2 = 0, max2 = 0f;
        float drift = rand2.range(1f);
        float length = this.length + (endLength * (1f - Math.abs(drift)));

        for(int i = 0; i < ltmp.length; i++){
            rand2.setSeed(seed * 9999L + (timeSeed + ltmp.length - i));
            float r = rand2.range(1.5f);
            rand2.setSeed(seed * 9999L + (timeSeed + ltmp.length - (i + 1)));
            float r2 = rand2.range(1.5f);
            pos += r;
            pos2 += r2;
            ltmp[i] = pos;
            ltmp2[i] = pos2;
        }
        float drft2 = drift > 0 ? (1f + drift) : (1f + drift / 2f);
        float delta = (pos / ltmp.length) * drft2;
        float delta2 = (pos2 / ltmp.length) * drft2;
        for(int i = 0; i < ltmp.length; i++){
            float v = ltmp[i] - delta * i;
            float v2 = ltmp2[i] - delta2 * i;
            ltmp[i] = v;
            ltmp2[i] = v2;
            max = Math.max(max, Math.abs(v));
            max2 = Math.max(max2, Math.abs(v2));
        }
        float lx = b.x, ly = b.y;
        Tmp.c1.lerp(lightningColors, Mathf.curve(fin, 0.01f, 0.7f));
        Draw.color(Tmp.c1);
        Lines.stroke(Mathf.clamp(1f - fin, 0f, 0.4f) * 11f * fout);
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
