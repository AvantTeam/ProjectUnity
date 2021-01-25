package unity.entities.bullet;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class KamiLaserBulletType extends BulletType{
    private static final Vec2 tVec = new Vec2();
    private static final Vec2 tVecB = new Vec2();
    private static final Vec2 tVecC = new Vec2();
    private static final Vec2 tVecD = new Vec2();
    private static final Rect tRect1 = new Rect();
    private static final Rect tRect2 = new Rect();
    private static TextureRegion circleRegion;

    public float length = 280f;
    public float width = 45f;
    public float curveScl = 2f;
    public float fadeTime = 16f;
    public float fadeInTime = 16f;

    public KamiLaserBulletType(float damage){
        super(0.001f, damage);
        collides = false;
        keepVelocity = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        despawnEffect = Fx.none;
    }

    @Override
    public void update(Bullet b){
        if(b.timer(1, 5f)){
            float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime) * width;

            tVec.trns(b.rotation(), length + (width * curveScl)).add(b);
            tVecD.trns(b.rotation(), -width * curveScl).add(b);
            tRect1.setCentered(tVecD.x, tVecD.y, width);
            tRect2.setCentered(tVec.x, tVec.y, width);
            tRect1.merge(tRect2);

            Units.nearby(tRect1, e -> {
                tVecB.trns(b.rotation(), length).add(b);
                tVecD.trns(b.rotation(), width * curveScl).add(b);
                tVecC.set(e);
                if(b.collides(e)){
                    Position a = e.dst(tVecB) < e.dst(tVecD) ? tVecB : tVecD;
                    float ba = e.dst(tVecB) < e.dst(tVecD) ? 0f : 180f;
                    //float angle = Angles.within(a.angleTo(e), b.rotation() + ba, 90f) ? 1f + (Mathf.clamp(1f - (Angles.angleDist(a.angleTo(e), b.rotation() + ba) / 90f)) * curveScl) : 1f;
                    float angle = angDist(a.angleTo(e), b.rotation() + ba) < 90f ? 1f + ((Mathf.clamp(1f - (angDist(a.angleTo(e), b.rotation() + ba) / 90f))) * curveScl) : 1f;
                    if(Intersector.intersectSegmentCircle(tVecD, tVecB, tVecC, fout * fout * angle)){
                        b.collision(e, e.x, e.y);
                        //Unity.print("Hit: " + Time.time + ":" + angle);
                    }
                }
            });
        }
    }

    private static float angDist(float a, float b){
        float x = Math.abs(a - b) % 360f;
        return x > 180f ? 360f - x : x;
    }

    @Override
    public void draw(Bullet b){
        if(circleRegion == null) circleRegion = Core.atlas.find("circle");
        tVecD.trns(b.rotation(), width * curveScl).add(b);
        float widthAlt = width + 3f;
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime);
        tVec.trns(b.rotation(), length).add(b);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time * 3f));
        Draw.rect(circleRegion, tVecD, Draw.scl * widthAlt * curveScl * 8f, Draw.scl * widthAlt * fout * 8f, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * widthAlt * curveScl * 8f, Draw.scl * widthAlt * fout * 8f, b.rotation());
        Lines.stroke(widthAlt * 2f * fout);
        Lines.line(tVecD.x, tVecD.y, tVec.x, tVec.y, false);
        Draw.color(Color.white);
        Draw.rect(circleRegion, tVecD, Draw.scl * width * curveScl * 8f, Draw.scl * width * fout * 8f, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * width * curveScl * 8f, Draw.scl * width * fout * 8f, b.rotation());
        Lines.stroke(width * 2f * fout);
        Lines.line(tVecD.x, tVecD.y, tVec.x, tVec.y, false);
        Draw.reset();
    }
}
