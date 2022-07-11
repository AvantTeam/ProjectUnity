package unity.graphics;

import arc.graphics.Color;
import arc.math.geom.Vec3;

//Unfortunately, I don't know how this generated.
public final class SColor{
    private static final Color STRUCT_LOCK = new Color();

    public static float a(int scolor){
        return ((int)(scolor & 0b00000000000000000000000011111111L) / 255f);
    }

    public static int a(int scolor, float value){
        return (int)((scolor & ~0b00000000000000000000000011111111L) | ((int)(value * 255f)));
    }

    public static float b(int scolor){
        return ((int)((scolor >>> 8) & 0b00000000000000000000000011111111L) / 255f);
    }

    public static int b(int scolor, float value){
        return (int)((scolor & ~0b00000000000000001111111100000000L) | ((int)(value * 255f) << 8L));
    }

    public static float g(int scolor){
        return ((int)((scolor >>> 16) & 0b00000000000000000000000011111111L) / 255f);
    }

    public static int g(int scolor, float value){
        return (int)((scolor & ~0b00000000111111110000000000000000L) | ((int)(value * 255f) << 16L));
    }

    public static float r(int scolor){
        return ((int)((scolor >>> 24) & 0b00000000000000000000000011111111L) / 255f);
    }

    public static int r(int scolor, float value){
        return (int)((scolor & ~0b11111111000000000000000000000000L) | ((int)(value * 255f) << 24L));
    }

    public static int construct(Color scolor){
        return construct(scolor.r, scolor.g, scolor.b, scolor.a);
    }

    public static int rgb565(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.rgb565();
        }
    }

    public static int rgba4444(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.rgba4444();
        }
    }

    public static int rgb888(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.rgb888();
        }
    }

    public static int rgba8888(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.rgba8888();
        }
    }

    public static int argb8888(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.argb8888();
        }
    }

    public static int rgb565(int scolor, int pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.rgb565(pvalue));
        }
    }

    public static int rgba4444(int scolor, int pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.rgba4444(pvalue));
        }
    }

    public static int rgb888(int scolor, int pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.rgb888(pvalue));
        }
    }

    public static int rgba8888(int scolor, int pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.rgba8888(pvalue));
        }
    }

    public static int argb8888(int scolor, int pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.argb8888(pvalue));
        }
    }

    public static int abgr8888(int scolor, float pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.abgr8888(pvalue));
        }
    }

    public static int rand(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.rand());
        }
    }

    public static int randHue(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.randHue());
        }
    }

    public static int rgba(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.rgba();
        }
    }

    public static int set(int scolor, Vec3 pvec){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.set(pvec));
        }
    }

    public static int mul(int scolor, float pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.mul(pvalue));
        }
    }

    public static int mula(int scolor, float pvalue){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.mula(pvalue));
        }
    }

    public static int clamp(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.clamp());
        }
    }

    public static int set(int scolor, float pr, float pg, float pb, float pa){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.set(pr, pg, pb, pa));
        }
    }

    public static int set(int scolor, float pr, float pg, float pb){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.set(pr, pg, pb));
        }
    }

    public static int set(int scolor, int prgba){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.set(prgba));
        }
    }

    public static float sum(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.sum();
        }
    }

    public static int add(int scolor, float pr, float pg, float pb, float pa){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.add(pr, pg, pb, pa));
        }
    }

    public static int add(int scolor, float pr, float pg, float pb){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.add(pr, pg, pb));
        }
    }

    public static int sub(int scolor, float pr, float pg, float pb, float pa){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.sub(pr, pg, pb, pa));
        }
    }

    public static int sub(int scolor, float pr, float pg, float pb){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.sub(pr, pg, pb));
        }
    }

    public static int inv(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.inv());
        }
    }

    public static int mul(int scolor, float pr, float pg, float pb, float pa){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.mul(pr, pg, pb, pa));
        }
    }

    public static int lerp(int scolor, float pr, float pg, float pb, float pa, float pt){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.lerp(pr, pg, pb, pa, pt));
        }
    }

    public static int premultiplyAlpha(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.premultiplyAlpha());
        }
    }

    public static float hue(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.hue();
        }
    }

    public static float saturation(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.saturation();
        }
    }

    public static float value(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.value();
        }
    }

    public static int hue(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.hue(pamount));
        }
    }

    public static int saturation(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.saturation(pamount));
        }
    }

    public static int value(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.value(pamount));
        }
    }

    public static int shiftHue(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.shiftHue(pamount));
        }
    }

    public static int shiftSaturation(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.shiftSaturation(pamount));
        }
    }

    public static int shiftValue(int scolor, float pamount){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.shiftValue(pamount));
        }
    }

    public static boolean equals(int scolor, Object po){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.equals(po);
        }
    }

    public static int hashCode(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.hashCode();
        }
    }

    public static float toFloatBits(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.toFloatBits();
        }
    }

    public static int abgr(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.abgr();
        }
    }

    public static String toString(int scolor){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.toString();
        }
    }

    public static void toString(int scolor, StringBuilder pbuilder){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            STRUCT_LOCK.toString(pbuilder);
        }
    }

    public static int fromHsv(int scolor, float ph, float ps, float pv){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.fromHsv(ph, ps, pv));
        }
    }

    public static int fromHsv(int scolor, float[] phsv){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return construct(STRUCT_LOCK.fromHsv(phsv));
        }
    }

    public static float[] toHsv(int scolor, float[] phsv){
        synchronized(STRUCT_LOCK){
            STRUCT_LOCK.r = r(scolor);
            STRUCT_LOCK.g = g(scolor);
            STRUCT_LOCK.b = b(scolor);
            STRUCT_LOCK.a = a(scolor);

            return STRUCT_LOCK.toHsv(phsv);
        }
    }

    public static int construct(float r, float g, float b, float a){
        return (((int)(a * 255f) << 0L) | ((int)(b * 255f) << 8L) | ((int)(g * 255f) << 16L) | ((int)(r * 255f) << 24L));
    }
}
