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
    private static final Rect tRect1 = new Rect();
    private static final Rect tRect2 = new Rect();
    private static TextureRegion circleRegion;

    public float length = 280f;
    public float width = 45f;
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

            tVec.trns(b.rotation(), length).add(b);
            tRect1.setCentered(b.x, b.y, width);
            tRect2.setCentered(tVec.x, tVec.y, width);
            tRect1.merge(tRect2);

            Units.nearby(tRect1, e -> {
                tVecB.trns(b.rotation(), length).add(b);
                tVec.set(b);
                tVecC.set(e);
                if(b.collides(e) && Intersector.intersectSegmentCircle(tVec, tVecB, tVecC, fout * fout)){
                    Vec2 v = Intersector.nearestSegmentPoint(tVec, tVecB, tVecC, Tmp.v1);
                    b.collision(e, v.x, v.y);
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        if(circleRegion == null) circleRegion = Core.atlas.find("circle");
        float widthAlt = width + 3f;
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime);
        tVec.trns(b.rotation(), length).add(b);
        Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time * 3f));
        Draw.rect(circleRegion, b, Draw.scl * widthAlt * 16f, Draw.scl * widthAlt * fout * 8f, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * widthAlt * 19f, Draw.scl * widthAlt * fout * 8f, b.rotation());
        Lines.stroke(widthAlt * 2f * fout);
        Lines.line(b.x, b.y, tVec.x, tVec.y, false);
        Draw.color(Color.white);
        Draw.rect(circleRegion, b, Draw.scl * width * 16f, Draw.scl * width * fout * 8f, b.rotation());
        Draw.rect(circleRegion, tVec, Draw.scl * width * 19f, Draw.scl * width * fout * 8f, b.rotation());
        Lines.stroke(width * 2f * fout);
        Lines.line(b.x, b.y, tVec.x, tVec.y, false);
        Draw.reset();
    }
}
