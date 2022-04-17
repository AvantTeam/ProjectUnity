package unity.graphics;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.graphics.*;

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
    /** The trail's blending. */
    public Blending blend = Blending.normal;

    private static final float[] vertices = new float[24];
    private static final Color tmp = new Color();

    // sigh.
    protected final FloatSeq points;
    protected float lastX = -1f, lastY = -1f, lastAngle = -1f, counter = 0f, lastW = 0f;

    public TexturedTrail(TextureRegion region, TextureRegion capRegion, int length){
        this(length);
        this.region = region;
        this.capRegion = capRegion;
    }

    public TexturedTrail(TextureRegion region, int length){
        this(length);
        this.region = region;
        if(region instanceof AtlasRegion reg) capRegion = Core.atlas.find(reg.name + "-cap", "circle");
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
        out.blend = blend;
        out.points.addAll(points);
        out.lastX = lastX;
        out.lastY = lastY;
        out.lastAngle = lastAngle;

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
    public void drawCap(Color color, float width){
        if(capRegion == null) capRegion = Core.atlas.find("circle");

        int psize = points.size;
        if(psize > 0){
            float v = points.items[psize - 1], w = lastW * width / (psize / 4f) * (psize - 4) / 4f * 2f;
            if(w <= 0.001f) return;

            Draw.blend(blend);
            Draw.mixcol(color, mixAlpha);

            Draw.alpha(v * fadeAlpha + (1f - fadeAlpha));
            Draw.rect(capRegion, lastX, lastY, w, w, -Mathf.radDeg * lastAngle - 90f);

            Draw.reset();
            Draw.blend();
        }
    }

    @Override
    public void draw(Color color, float width){
        if(region == null) region = Core.atlas.find("white");
        if(points.isEmpty()) return;

        float[] items = points.items;
        int psize = points.size;

        float
            endAngle = this.lastAngle, lastAngle = endAngle,
            size = width / (psize / 4f), fracOffset = psize - psize * shrink, fracStride = 1f - fracOffset / psize,
            u = region.u2, v = region.v, u2 = region.u, v2 = region.v2;

        Draw.blend(blend);
        for(int i = 0; i < psize; i += 4){
            float
                x1 = items[i], y1 = items[i + 1], w1 = items[i + 2], rv1 = items[i + 3],
                x2, y2, w2, rv2;

            if(i < psize - 4){
                x2 = items[i + 4];
                y2 = items[i + 5];
                w2 = items[i + 6];
                rv2 = items[i + 7];
            }else{
                x2 = lastX;
                y2 = lastY;
                w2 = lastW;
                rv2 = 1f;
            }

            float
                z2 = i == psize - 4 ? endAngle : -Angles.angleRad(x1, y1, x2, y2), z1 = i == 0 ? z2 : lastAngle,
                fs1 = ((fracOffset + i * fracStride) / 4f) * size * w1,
                fs2 = ((fracOffset + (i + 4f) * fracStride) / 4f) * size * w2,

                cx = Mathf.sin(z1) * fs1, cy = Mathf.cos(z1) * fs1,
                nx = Mathf.sin(z2) * fs2, ny = Mathf.cos(z2) * fs2,

                mv1 = Mathf.lerp(v2, v, rv1), mv2 = Mathf.lerp(v2, v, rv2),
                col1 = tmp.set(Draw.getColor()).a(rv1 * fadeAlpha + (1f - fadeAlpha)).clamp().toFloatBits(),
                col2 = tmp.set(Draw.getColor()).a(rv2 * fadeAlpha + (1f - fadeAlpha)).clamp().toFloatBits(),
                mix1 = tmp.set(color).a(rv1 * mixAlpha).clamp().toFloatBits(),
                mix2 = tmp.set(color).a(rv2 * mixAlpha).clamp().toFloatBits();

            vertices[0] = x1 - cx;
            vertices[1] = y1 - cy;
            vertices[2] = col1;
            vertices[3] = u;
            vertices[4] = mv1;
            vertices[5] = mix1;

            vertices[6] = x1 + cx;
            vertices[7] = y1 + cy;
            vertices[8] = col1;
            vertices[9] = u2;
            vertices[10] = mv1;
            vertices[11] = mix1;

            vertices[12] = x2 + nx;
            vertices[13] = y2 + ny;
            vertices[14] = col2;
            vertices[15] = u2;
            vertices[16] = mv2;
            vertices[17] = mix2;

            vertices[18] = x2 - nx;
            vertices[19] = y2 - ny;
            vertices[20] = col2;
            vertices[21] = u;
            vertices[22] = mv2;
            vertices[23] = mix2;

            Draw.vert(region.texture, vertices, 0, 24);
            lastAngle = z2;
        }
        Draw.blend();
    }

    @Override
    public void shorten(){
        if((counter += Time.delta) >= 1f){
            if(points.size >= 4) points.removeRange(0, 3);
            counter %= 1f;
        }

        calcProgress();
    }

    @Override
    public void update(float x, float y, float width){
        if((counter += Time.delta) >= 1f){
            if(points.size > length * 4) points.removeRange(0, 3);

            counter %= 1f;
            points.add(x, y, width, 0f);
        }

        lastAngle = -Angles.angleRad(x, y, lastX, lastY) + Mathf.pi;
        lastX = x;
        lastY = y;
        lastW = width;

        calcProgress();
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
