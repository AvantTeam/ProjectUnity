package unity.content;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import unity.graphics.*;
import unity.graphics.MultiTrail.*;
import unity.util.*;

public final class Trails{
    public static TexturedTrail singlePhantasmal(int length){
        return new TexturedTrail(Core.atlas.find("unity-phantasmal-trail"), length){{
            blend = Blending.additive;
            fadeInterp = Interp.pow2In;
            sideFadeInterp = Interp.pow3In;
            mixInterp = Interp.pow10In;
            gradientInterp = Interp.pow10Out;
            fadeColor = new Color(0.3f, 0.5f, 1f);
            shrink = 0f;
            fadeAlpha = 1f;
            mixAlpha = 1f;
            trailChance = 0.4f;
            trailWidth = 1.6f;
            trailColor = UnityPal.monolithLight;
        }};
    }

    public static TexturedTrail phantasmalExhaust(int length){
        return Utils.with(singlePhantasmal(length), t -> {
            t.fadeInterp = Interp.pow3In;
            t.sideFadeInterp = Interp.pow5In;
            t.mixAlpha = 0f;
            t.trailChance = 0f;
            t.shrink = -3.6f;
        });
    }

    public static MultiTrail phantasmal(int length){
        return phantasmal(length, 3.6f, 3.5f, 0f);
    }

    public static MultiTrail phantasmal(RotationHandler rot, int length){
        return phantasmal(rot, length, 3.6f, 3.5f, 0f);
    }

    public static MultiTrail phantasmal(int length, float scale, float magnitude, float offsetY){
        return phantasmal(MultiTrail::calcRot, length, scale, magnitude, offsetY);
    }

    public static MultiTrail phantasmal(RotationHandler rot, int length, float scale, float magnitude, float offsetY){
        int strandsAmount = 2;

        TrailHold[] trails = new TrailHold[strandsAmount + 2];
        for(int i = 0; i < strandsAmount; i++) trails[i] = new TrailHold(Utils.with(singlePhantasmal(Mathf.round(length * 1.5f)), t -> t.trailWidth = 4.8f), 0f, 0f, 0.16f);
        trails[strandsAmount] = new TrailHold(singlePhantasmal(length));
        trails[strandsAmount + 1] = new TrailHold(phantasmalExhaust(Mathf.round(length * 0.5f)), 0f, 1.6f, 1f);

        float offset = Mathf.random(Mathf.PI2 * scale);
        return new MultiTrail(rot, trails){
            @Override
            public void update(float x, float y, float width){
                float angle = rotation.get(this, x, y) - 90f;
                for(int i = 0; i < strandsAmount; i++){
                    Tmp.v1.trns(angle, Mathf.sin((Time.time + offset + (Mathf.PI2 * scale) * ((float)i / strandsAmount)), scale, magnitude * width), offsetY);

                    TrailHold trail = trails[i];
                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
                }

                for(int i = strandsAmount; i < trails.length; i++){
                    TrailHold trail = trails[i];
                    Tmp.v1.trns(angle, trail.x, trail.y);

                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width);
                }

                lastX = x;
                lastY = y;
            }
        };
    }

    public static TexturedTrail singleSoul(int length){
        return new TexturedTrail(Core.atlas.find("unity-soul-trail"), length){{
            blend = Blending.additive;
            fadeInterp = Interp.pow5In;
            sideFadeInterp = Interp.pow10In;
            mixInterp = Interp.pow5In;
            gradientInterp = Interp.pow5Out;
            fadeColor = new Color(0.1f, 0.2f, 1f);
            shrink = 1f;
            mixAlpha = 0.8f;
            fadeAlpha = 0.5f;
            trailChance = 0f;
        }};
    }

    public static MultiTrail soul(int length){
        return soul(length, 6f, 2.2f);
    }

    public static MultiTrail soul(RotationHandler rot, int length){
        return soul(rot, length, 6f, 2.2f);
    }

    public static MultiTrail soul(int length, float scale, float magnitude){
        return soul(MultiTrail::calcRot, length, scale, magnitude);
    }

    public static MultiTrail soul(RotationHandler rot, int length, float scale, float magnitude){
        int strandsAmount = 3;

        TrailHold[] trails = new TrailHold[strandsAmount + 1];
        for(int i = 0; i < strandsAmount; i++) trails[i] = new TrailHold(Utils.with(singleSoul(Mathf.round(length * 1.5f)), t -> t.mixAlpha = 0f), 0f, 0f, 0.56f);
        trails[strandsAmount] = new TrailHold(singlePhantasmal(length), UnityPal.monolith);

        float dir = Mathf.sign(Mathf.chance(0.5f));
        float offset = Mathf.random(Mathf.PI2 * scale);
        return new MultiTrail(rot, trails){
            @Override
            public void update(float x, float y, float width){
                float angle = rotation.get(this, x, y) - 90f;
                for(int i = 0; i < strandsAmount; i++){
                    float rad = (Time.time + offset + (Mathf.PI2 * scale) * ((float)i / strandsAmount)) * dir;
                    float scl = Mathf.map(Mathf.sin(rad, scale, 1f), -1f, 1f, 0.2f, 1f);
                    Tmp.v1.trns(angle, Mathf.cos(rad, scale, magnitude * width));

                    TrailHold trail = trails[i];
                    trail.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * trail.width * scl);
                }

                TrailHold main = trails[trails.length - 1];
                Tmp.v1.trns(angle, main.x, main.y);

                main.trail.update(x + Tmp.v1.x, y + Tmp.v1.y, width * main.width);

                lastX = x;
                lastY = y;
            }
        };
    }

    private Trails(){
        throw new AssertionError();
    }
}
