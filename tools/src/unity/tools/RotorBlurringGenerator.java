package unity.tools;

import arc.math.*;
import arc.util.*;
import arc.util.noise.*;
import unity.content.*;
import unity.type.*;

public class RotorBlurringGenerator implements Generator{
    @Override
    public void generate(){
        Log.info("Generating Rotor Sprites");

        UnityUnitType[] copters = new UnityUnitType[]{
            (UnityUnitType)UnityUnitTypes.caelifera,
            (UnityUnitType)UnityUnitTypes.schistocerca,
            (UnityUnitType)UnityUnitTypes.anthophila, // Has the longest blades for testing with
            (UnityUnitType)UnityUnitTypes.vespula,
            (UnityUnitType)UnityUnitTypes.lepidoptera
        };

        for(UnityUnitType unitType : copters){
            // Normally these should be called after IconGenerator which however already invokes these, so we can just skip.
            // Unless someone else has bigger ideas, I'll leave these here and commented out
            //unitType.load();
            //unitType.init();

            String bladeSpriteName = unitType.name + "-rotor-blade";
            String ghostSpriteName = unitType.name + "-rotor-blade-ghost";

            if(SpriteProcessor.has(ghostSpriteName)){
                Log.info("Rotor Blade sprite override for @ exists, skipping", ghostSpriteName);
                continue;
            }else if(!SpriteProcessor.has(bladeSpriteName)){
                Log.warn("@ not found", bladeSpriteName);
                continue;
            }

            Sprite bladeSprite = SpriteProcessor.get(bladeSpriteName);

            // This array is to be written in the order where colors at index 0 are located towards the center,
            // and colors at the end of the array is located towards at the edge.
            int[] heightAverageColors = new int[(bladeSprite.height >> 1) + 1]; // Go one extra so it becomes transparent especially if blade is full length
            int bladeLength = populateColorArray(heightAverageColors, bladeSprite, bladeSprite.height >> 1);

            @SuppressWarnings("SuspiciousNameCombination") // Shut
            Sprite ghostSprite = new Sprite(bladeSprite.height, bladeSprite.height);

            // Instead of ACTUALLY accounting for the insanity that is the variation of rotor configurations
            // including counter-rotating propellers and that jazz, number 4 will be used instead.
            drawRadial(ghostSprite, heightAverageColors, bladeLength, 4);

            ghostSpriteName = ghostSpriteName.replace("unity-", "");
            Log.info("Saving @ with blade length @", ghostSpriteName, bladeLength);
            ghostSprite.save(ghostSpriteName);

            String shadeSpriteName = unitType.name + "-rotor-blade-shade";

            if(SpriteProcessor.has(shadeSpriteName)){
                Log.info("Rotor Blade shade sprite override for @ exists, skipping", shadeSpriteName);
                continue;
            }

            @SuppressWarnings("SuspiciousNameCombination") // Also shut
            Sprite shadeSprite = new Sprite(bladeSprite.height, bladeSprite.height);

            drawShade(shadeSprite, bladeLength);

            shadeSpriteName = shadeSpriteName.replace("unity-", "");
            Log.info("Saving @ with blade length @", shadeSpriteName, bladeLength);
            shadeSprite.antialias().save(shadeSpriteName);
        }

        Log.info("Rotors complete");
    }

    private int populateColorArray(int[] heightAverageColors, Sprite bladeSprite, int halfHeight){
        Tmp.c1.rgba8888(0x00_00_00_00);
        float hits = 0;
        int length = 0;

        for(int y = halfHeight - 1; y >= 0; y--){
            for(int x = 0; x < bladeSprite.width; x++){
                Tmp.c3.set(bladeSprite.getColor(x, y));

                if(Tmp.c3.a > 0){
                    hits++;
                    Tmp.c1.r += Tmp.c3.r;
                    Tmp.c1.g += Tmp.c3.g;
                    Tmp.c1.b += Tmp.c3.b;
                }
            }

            if(hits > 0){
                Tmp.c1.r = Tmp.c1.r / hits;
                Tmp.c1.g = Tmp.c1.g / hits;
                Tmp.c1.b = Tmp.c1.b / hits;
                Tmp.c1.a = 1f;

                length = Math.max(length, halfHeight - y);

                Tmp.c1.clamp();
                Tmp.c2.set(Tmp.c1).a(0);
            }else{
                // Use color from previous row with alpha 0. This avoids alpha bleeding when interpolating later
                Tmp.c1.set(Tmp.c2);
            }

            heightAverageColors[halfHeight - y] = Tmp.c1.rgba8888();
            Tmp.c1.set(0x00_00_00_00);
            hits = 0;
        }

        heightAverageColors[length + 1] = heightAverageColors[length] & 0xFF_FF_FF_00; // Set final entry to be fully transparent

        return length;
    }

    private void drawRadial(Sprite sprite, final int[] colorTable, final int tableLimit, final int bladeCount){
        final float spriteCenter = 0.5f - (sprite.height >> 1);
        sprite.each((x, y) -> {
            // 0.5f is required since mathematically it'll put the position at an intersection between 4 pixels, since the sprites are even-sized
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            if(positionLength < tableLimit){
                int arrayIndex = Mathf.clamp((int)positionLength, 0, tableLimit);
                float a = Mathf.cos(Mathf.atan2(x + spriteCenter, y + spriteCenter) * (bladeCount << 1)) * 0.05f + 0.95f;
                a *= a;

                sprite.draw(x, y,
                    MathUtil.colorLerp(Tmp.c1.rgba8888(colorTable[arrayIndex]), Tmp.c2.rgba8888(colorTable[arrayIndex + 1]), positionLength % 1f)
                        .mul(a, a, a, a * (1 - 0.5f / (tableLimit - positionLength + 0.5f)))
                );
            }else{
                sprite.draw(x, y, Tmp.c1.rgba8888(0x00_00_00_00));
            }
        });
    }

    // To help visualize the expected output of this algorithm:
    //   Divide the circle of the rotor's blade into rings, with a new ring every 4 pixels.
    // 	 Within each band exists a circumferential parallelogram, which the upper and bottom lines are offset differently.
    //   Entire parallelograms are offset as well.
    // The resulting drawing looks like a very nice swooshy hourglass. It must be antialiased afterwards.
    private void drawShade(Sprite sprite, final int length){
        final float spriteCenter = 0.5f - (sprite.height >> 1);
        // Divide by 2 then round down to nearest even positive number. This array will be accessed by pairs, hence the even number size.
        float[] offsets = new float[length >> 2 & 0xEFFFFFFE];
        for(int i = 0; i < offsets.length; i++){
            // The output values of the noise functions from the noise class are awful that
            // every integer value always result in a 0. Offsetting by 0.5 results in delicious good noise.
            // The additional offset is only that the noise values close to origin make for bad output for the sprite.
            offsets[i] = (float)Noise.rawNoise(i + 2.5f);
        }

        sprite.each((x, y) -> {
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            int arrayIndex = Mathf.clamp((int)positionLength >> 2 & 0xEFFFFFFE, 0, offsets.length - 2);
            float offset = MathUtil.pythagoreanLerp(offsets[arrayIndex], offsets[arrayIndex + 1], (positionLength / 8f) % 1);

            float a = Mathf.sin(Mathf.atan2(x + spriteCenter, y + spriteCenter) + offset);
            a *= a; // Square the sine wave to make it all positive values
            a *= a; // Square sine again to thin out intervals of value increases
            a *= a; // Sine to the 8th power - Perfection
            // To maintain the geometric-sharpness, the resulting alpha fractional is rounded to binary integer.
            sprite.draw(x, y, Tmp.c1.rgb888(0xFF_FF_FF).a(Mathf.round(a) * Mathf.clamp(length - positionLength, 0f, 1f)));
        });
    }
}
