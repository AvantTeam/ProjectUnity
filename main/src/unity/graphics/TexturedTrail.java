package unity.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

/**
 * A texture-able trail.
 * @author GlennFolker
 */
public class TexturedTrail extends Trail{
    /** The texture region of this trail. */
    public TextureRegion region;
    /** The cap texture region of this trail. */
    public TextureRegion capRegion;
    /** The trail's width shrink as it goes, in percentage. `1f` makes the trail triangle-shaped. */
    public float shrink = 1f;
    /** The trail's alpha as it goes, in percentage. `1f` makes the trail's tail completely invisible. */
    public float fadeAlpha = 0f;
    /** The trail's mix color alpha, used in {@link #draw(Color, float)}. Fades as the trail goes. */
    public float mixAlpha = 0.5f;
    /** The trail's base width, multiplied by {@link #update(float, float, float)}'d width. **/
    public float baseWidth = 1f;
    /** The trail's fade color, multiplied as the trail fades. Typically used to saturate the trail as it fades. */
    public Color fadeColor = Color.white;
    /** The trail's {@link #fadeColor} interpolation. */
    public Interp gradientInterp = Interp.linear;
    /** The trail's center alpha interpolation. */
    public Interp fadeInterp = Interp.pow2In;
    /** The trail's edge alpha interpolation. */
    public Interp sideFadeInterp = Interp.pow3In;
    /** The trail's mix color interpolation. */
    public Interp mixInterp = Interp.pow5In;
    /** The trail's blending. */
    public Blending blend = Blending.normal;

    /** Whether to force drawing the trail's cap or not. */
    public boolean forceCap;

    /** The trail's particle effect. */
    public Effect trailEffect = Fx.missileTrail;
    /** The particle effect's chance. */
    public float trailChance = 0f;
    /** The particle effect's radius. */
    public float trailWidth = 1f;
    /** The particle effect's color. */
    public Color trailColor = Pal.engine;
    /** The trail speed's bare minimum at which the particle effects start appearing less. */
    public float trailThreshold = 3f;

    private static final float[] vertices = new float[24];
    private static final Color tmp = new Color();

    // sigh.
    protected final FloatSeq points;
    protected float lastX = -1f, lastY = -1f, lastAngle = -1f, lastW = 0f, counter = 0f;

    public TexturedTrail(TextureRegion region, TextureRegion capRegion, int length){
        this(length);
        this.region = region;
        this.capRegion = capRegion;
    }

    public TexturedTrail(TextureRegion region, int length){
        this(length);
        this.region = region;
        if(!headless && region instanceof AtlasRegion reg) capRegion = Core.atlas.find(reg.name + "-cap", "unity-hcircle");
    }

    public TexturedTrail(int length){
        super(0); // Don't allocate anything for base class' point array.

        this.length = length;
        points = new FloatSeq(length * 4);
    }

    @Override
    public TexturedTrail copy(){
        TexturedTrail out = new TexturedTrail(region, capRegion, length);
        out.shrink = shrink;
        out.fadeAlpha = fadeAlpha;
        out.mixAlpha = mixAlpha;
        out.baseWidth = baseWidth;
        out.fadeColor = fadeColor;
        out.gradientInterp = gradientInterp;
        out.fadeInterp = fadeInterp;
        out.sideFadeInterp = sideFadeInterp;
        out.mixInterp = mixInterp;
        out.blend = blend;
        out.forceCap = forceCap;
        out.trailEffect = trailEffect;
        out.trailChance = trailChance;
        out.trailWidth = trailWidth;
        out.trailColor = trailColor;
        out.trailThreshold = trailThreshold;
        out.points.addAll(points);
        out.lastX = lastX;
        out.lastY = lastY;
        out.lastAngle = lastAngle;
        out.lastW = lastW;
        out.counter = counter;
        return out;
    }

    @Override
    public void clear(){
        points.clear();
    }

    @Override
    public int size(){
        return points.size / 4;
    }

    @Override
    public void drawCap(Color color, float widthMultiplier){
        if(forceCap) return;

        float width = baseWidth * widthMultiplier;
        if(capRegion == null) capRegion = Core.atlas.find("unity-hcircle");

        int psize = points.size;
        if(psize > 0){
            float
                rv = Mathf.clamp(points.items[psize - 1]),
                alpha = rv * fadeAlpha + (1f - fadeAlpha),
                w = lastW * width / (psize / 4f) * ((psize - 4f) / 4f) * 2f,
                h = ((float)capRegion.height / capRegion.width) * w,

                angle = -Mathf.radDeg * lastAngle - 90f,
                u = capRegion.u, v = capRegion.v2, u2 = capRegion.u2, v2 = capRegion.v, uh = Mathf.lerp(u, u2, 0.5f),
                cx = Mathf.cosDeg(angle) * w / 2f, cy = Mathf.sinDeg(angle) * w / 2f,
                x1 = lastX, y1 = lastY,
                x2 = lastX + Mathf.cosDeg(angle + 90f) * h, y2 = lastY + Mathf.sinDeg(angle + 90f) * h,

                col1 = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv)).a(fadeInterp.apply(alpha)).toFloatBits(),
                col1h = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv)).a(sideFadeInterp.apply(alpha)).toFloatBits(),
                col2 = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv)).a(fadeInterp.apply(alpha)).toFloatBits(),
                col2h = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv)).a(sideFadeInterp.apply(alpha)).toFloatBits(),
                mix1 = tmp.set(color).a(mixInterp.apply(rv * mixAlpha)).toFloatBits(),
                mix2 = tmp.set(color).a(mixInterp.apply(rv * mixAlpha)).toFloatBits();

            Draw.blend(blend);
            vertices[0] = x1 - cx;
            vertices[1] = y1 - cy;
            vertices[2] = col1h;
            vertices[3] = u;
            vertices[4] = v;
            vertices[5] = mix1;

            vertices[6] = x1;
            vertices[7] = y1;
            vertices[8] = col1;
            vertices[9] = uh;
            vertices[10] = v;
            vertices[11] = mix1;

            vertices[12] = x2;
            vertices[13] = y2;
            vertices[14] = col2;
            vertices[15] = uh;
            vertices[16] = v2;
            vertices[17] = mix2;

            vertices[18] = x2 - cx;
            vertices[19] = y2 - cy;
            vertices[20] = col2h;
            vertices[21] = u;
            vertices[22] = v2;
            vertices[23] = mix2;

            Draw.vert(region.texture, vertices, 0, 24);

            vertices[6] = x1 + cx;
            vertices[7] = y1 + cy;
            vertices[8] = col1h;
            vertices[9] = u2;
            vertices[10] = v;
            vertices[11] = mix1;

            vertices[0] = x1;
            vertices[1] = y1;
            vertices[2] = col1;
            vertices[3] = uh;
            vertices[4] = v;
            vertices[5] = mix1;

            vertices[18] = x2;
            vertices[19] = y2;
            vertices[20] = col2;
            vertices[21] = uh;
            vertices[22] = v2;
            vertices[23] = mix2;

            vertices[12] = x2 + cx;
            vertices[13] = y2 + cy;
            vertices[14] = col2h;
            vertices[15] = u2;
            vertices[16] = v2;
            vertices[17] = mix2;

            Draw.vert(region.texture, vertices, 0, 24);
            Draw.blend();
        }
    }

    @Override
    public void draw(Color color, float widthMultiplier){
        if(forceCap) drawCap(color, widthMultiplier);
        float width = baseWidth * widthMultiplier;

        if(region == null) region = Core.atlas.find("white");
        if(points.isEmpty()) return;

        float[] items = points.items;
        int psize = points.size;

        float
            endAngle = this.lastAngle, lastAngle = endAngle,
            u = region.u2, v = region.v2, u2 = region.u, v2 = region.v, uh = Mathf.lerp(u, u2, 0.5f);

        Draw.blend(blend);
        for(int i = 0; i < psize; i += 4){ // Draw from tail to head.
            float
                x1 = items[i], y1 = items[i + 1], w1 = items[i + 2], rv1 = Mathf.clamp(items[i + 3]),
                x2, y2, w2, rv2;

            if(i < psize - 4){
                x2 = items[i + 4];
                y2 = items[i + 5];
                w2 = items[i + 6];
                rv2 = Mathf.clamp(items[i + 7]);
            }else{
                x2 = lastX;
                y2 = lastY;
                w2 = lastW;
                rv2 = points.items[psize - 1];
            }

            float
                z2 = i == psize - 4 ? endAngle : -Angles.angleRad(x1, y1, x2, y2), z1 = i == 0 ? z2 : lastAngle,
                fs1 = Mathf.map(i, 0f, psize, 1f - shrink, 1f) * width * w1,
                fs2 = Mathf.map(Math.min(i + 4f, psize - 4f), 0f, psize, 1f - shrink, 1f) * width * w2,

                cx = Mathf.sin(z1) * fs1, cy = Mathf.cos(z1) * fs1,
                nx = Mathf.sin(z2) * fs2, ny = Mathf.cos(z2) * fs2,

                mv1 = Mathf.lerp(v, v2, rv1), mv2 = Mathf.lerp(v, v2, rv2),
                cv1 = rv1 * fadeAlpha + (1f - fadeAlpha), cv2 = rv2 * fadeAlpha + (1f - fadeAlpha),
                col1 = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv1)).a(fadeInterp.apply(cv1)).toFloatBits(),
                col1h = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv1)).a(sideFadeInterp.apply(cv1)).toFloatBits(),
                col2 = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv2)).a(fadeInterp.apply(cv2)).toFloatBits(),
                col2h = tmp.set(Draw.getColor()).lerp(fadeColor, gradientInterp.apply(1f - rv2)).a(sideFadeInterp.apply(cv2)).toFloatBits(),
                mix1 = tmp.set(color).a(mixInterp.apply(rv1 * mixAlpha)).toFloatBits(),
                mix2 = tmp.set(color).a(mixInterp.apply(rv2 * mixAlpha)).toFloatBits();

            vertices[0] = x1 - cx;
            vertices[1] = y1 - cy;
            vertices[2] = col1h;
            vertices[3] = u;
            vertices[4] = mv1;
            vertices[5] = mix1;

            vertices[6] = x1;
            vertices[7] = y1;
            vertices[8] = col1;
            vertices[9] = uh;
            vertices[10] = mv1;
            vertices[11] = mix1;

            vertices[12] = x2;
            vertices[13] = y2;
            vertices[14] = col2;
            vertices[15] = uh;
            vertices[16] = mv2;
            vertices[17] = mix2;

            vertices[18] = x2 - nx;
            vertices[19] = y2 - ny;
            vertices[20] = col2h;
            vertices[21] = u;
            vertices[22] = mv2;
            vertices[23] = mix2;

            Draw.vert(region.texture, vertices, 0, 24);

            vertices[6] = x1 + cx;
            vertices[7] = y1 + cy;
            vertices[8] = col1h;
            vertices[9] = u2;
            vertices[10] = mv1;
            vertices[11] = mix1;

            vertices[0] = x1;
            vertices[1] = y1;
            vertices[2] = col1;
            vertices[3] = uh;
            vertices[4] = mv1;
            vertices[5] = mix1;

            vertices[18] = x2;
            vertices[19] = y2;
            vertices[20] = col2;
            vertices[21] = uh;
            vertices[22] = mv2;
            vertices[23] = mix2;

            vertices[12] = x2 + nx;
            vertices[13] = y2 + ny;
            vertices[14] = col2h;
            vertices[15] = u2;
            vertices[16] = mv2;
            vertices[17] = mix2;

            Draw.vert(region.texture, vertices, 0, 24);
            lastAngle = z2;
        }

        Draw.blend();
    }

    @Override
    public void shorten(){
        if((counter += Time.delta) >= 0.96f){
            if(points.size >= 4) points.removeRange(0, 3);
            counter = 0f;
        }

        calcProgress();
    }

    @Override
    public void update(float x, float y, float widthMultiplier){
        float dst = Mathf.dst(lastX, lastY, x, y);
        float speed = dst / Time.delta;

        float width = baseWidth * widthMultiplier;
        if((counter += Time.delta) >= 0.96f){
            if(points.size > length * 4) points.removeRange(0, 3);

            counter = 0f;
            points.add(x, y, width, 0f);
        }

        lastAngle = Mathf.zero(dst, 0.4f) ? lastAngle : (-Angles.angleRad(x, y, lastX, lastY) + Mathf.pi);
        lastX = x;
        lastY = y;
        lastW = width;
        calcProgress();

        if(points.size > 0 && trailChance > 0f && Mathf.chanceDelta(trailChance * Mathf.clamp(speed / trailThreshold))){
            trailEffect.at(
                x, y, width * trailWidth,
                tmp.set(trailColor).a(fadeInterp.apply(
                    Mathf.clamp(points.items[points.size - 1]) *
                    fadeAlpha + (1f - fadeAlpha)
                ))
            );
        }
    }

    public void calcProgress(){
        int psize = points.size;
        if(psize > 0){
            float[] items = points.items;

            float maxDst = 0f;
            for(int i = 0; i < psize; i += 4){
                float
                    x1 = items[i], y1 = items[i + 1],
                    dst = i < psize - 4 ? Mathf.dst(x1, y1, items[i + 4], items[i + 5]) : Mathf.dst(x1, y1, lastX, lastY);

                maxDst += dst;
                items[i + 3] = dst;
            }

            float frac = (points.size / 4f) / length;
            float first = items[3];

            float last = 0f;
            for(int i = 0; i < psize; i += 4){
                float v = items[i + 3];

                items[i + 3] = Mathf.clamp((v + last - first) / maxDst * frac);
                last += v;
            }
        }
    }
}
