package unity.tools.proc;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.noise.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.entities.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.tools.*;
import unity.tools.GenAtlas.*;
import unity.type.*;
import unity.type.decal.*;
import unity.type.weapons.*;
import unity.util.*;

import java.util.concurrent.*;

import static mindustry.Vars.*;
import static unity.tools.Tools.*;

public class UnitProcessor implements Processor{
    private final ObjectSet<String> outlined = new ObjectSet<>();

    private boolean outline(String region){
        synchronized(outlined){
            return outlined.add(region);
        }
    }

    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    public void process(ExecutorService exec){
        content.units().each(type -> type instanceof UnityUnitType && !type.isHidden(), (UnityUnitType type) -> submit(exec, () -> {
            init(type);
            load(type);

            Color color = new Color();
            float scl = Draw.scl / 4f;

            var optional = Seq.with("-joint", "-joint-base", "-leg-back", "-leg-base-back", "-foot");
            Boolf<GenRegion> opt = r -> !optional.contains(e -> r.name.contains(e)) || r.found();

            Cons3<GenRegion, String, Pixmap> add = (relative, name, pixmap) -> {
                if(!relative.found()) throw new IllegalArgumentException("Cannot use a non-existent region as a relative point: " + relative);

                var reg = new GenRegion(name, pixmap);
                reg.relativePath = relative.relativePath;
                reg.save();
            };

            Cons<TextureRegion> outliner = t -> {
                if(t instanceof GenRegion at && opt.get(at) && outline(at.name)){
                    var reg = new GenRegion(at.name, Pixmaps.outline(new PixmapRegion(at.pixmap()), type.outlineColor, type.outlineRadius));
                    reg.relativePath = at.relativePath;
                    reg.save();

                    replace(at);
                }
            };

            Cons2<TextureRegion, String> outlSeparate = (t, suffix) -> {
                if(t instanceof GenRegion at && opt.get(at)){
                    var reg = new GenRegion(at.name + "-" + suffix, Pixmaps.outline(new PixmapRegion(at.pixmap()), type.outlineColor, type.outlineRadius));
                    reg.relativePath = at.relativePath;
                    reg.save();
                }
            };

            Unit unit = type.constructor.get();

            if(unit instanceof Legsc || unit instanceof TriJointLegsc){
                outliner.get(type.jointRegion);
                outliner.get(type.footRegion);
                outliner.get(type.legBaseRegion);
                outliner.get(type.baseJointRegion);
                outliner.get(type.legRegion);

                outliner.get(type.legMiddleRegion);

                outliner.get(type.legBackRegion);
                outliner.get(type.legBaseBackRegion);
                outliner.get(type.footBackRegion);
            }

            if(unit instanceof Mechc){
                outliner.get(type.legRegion);
            }

            if(unit instanceof Copterc){
                for(Rotor rotor : type.rotors){
                    var region = conv(rotor.bladeRegion);

                    outlSeparate.get(region, "outline");
                    outliner.get(rotor.topRegion);

                    if(atlas.has(rotor.name + "-blade-ghost") || !atlas.has(rotor.name + "-blade")){
                        rotor.load();
                        continue;
                    }

                    var bladeSprite = region.pixmap();

                    // This array is to be written in the order where colors at index 0 are located towards the center,
                    // and colors at the end of the array is located towards at the edge.
                    int[] heightAverageColors = new int[(bladeSprite.height >> 1) + 1]; // Go one extra so it becomes transparent especially if blade is full length
                    int bladeLength = populateColorArray(heightAverageColors, bladeSprite, bladeSprite.height >> 1);

                    var ghostSprite = new Pixmap(bladeSprite.height, bladeSprite.height);
                    drawRadial(ghostSprite, heightAverageColors, bladeLength);
                    add.get(region, rotor.name + "-blade-ghost", ghostSprite);

                    if(atlas.has(rotor.name + "-blade-shade")){
                        rotor.load();
                        continue;
                    }

                    var shadeSprite = new Pixmap(bladeSprite.height, bladeSprite.height);
                    drawShade(shadeSprite, bladeLength);
                    add.get(region, rotor.name + "-blade-shade", shadeSprite);

                    rotor.load();
                }
            }

            if(unit instanceof WormDefaultUnit || unit instanceof Wormc){
                outlSeparate.get(type.segmentRegion, "outline");
                outlSeparate.get(type.tailRegion, "outline");
            }

            for(TextureRegion reg : type.abilityRegions){
                if(reg.found()) outliner.get(reg);
            }

            for(TentacleType tentacle : type.tentacles){
                outliner.get(tentacle.region);
                outliner.get(tentacle.tipRegion);
            }

            Pixmap icon = Pixmaps.outline(new PixmapRegion(conv(type.region).pixmap()), type.outlineColor, type.outlineRadius);
            add.get(conv(type.region), type.name + "-outline", icon.copy());

            if(unit instanceof Mechc){
                GraphicUtils.drawCenter(icon, conv(type.baseRegion).pixmap());
                GraphicUtils.drawCenter(icon, conv(type.legRegion).pixmap());

                var flip = conv(type.legRegion).pixmap().flipX();
                GraphicUtils.drawCenter(icon, flip);
                flip.dispose();

                icon.draw(conv(type.region).pixmap(), true);
            }

            for(UnitDecorationType decoration : type.decorations){
                if(!decoration.top) decoration.drawIcon(r -> conv(r).pixmap(), icon, outliner);
            }

            for(Weapon weapon : type.weapons){
                if(weapon.name.isEmpty()) continue;

                var reg = conv(weapon.region);
                add.get(reg, weapon.name + "-outline", Pixmaps.outline(new PixmapRegion(reg.pixmap()), type.outlineColor, type.outlineRadius));

                if(weapon instanceof MultiBarrelWeapon m && outline(weapon.name + "-barrel")){
                    outlSeparate.get(m.barrelRegion, "outline");
                }

                if(weapon instanceof MortarWeapon m){
                    outliner.get(m.barrelRegion);
                    outliner.get(m.barrelEndRegion);
                }

                if((!weapon.top || type.bottomWeapons.contains(weapon.name))){
                    var out = atlas.find(weapon.name + "-outline");
                    var pix = out.pixmap().copy();

                    if(weapon.flipSprite){
                        var newPix = pix.flipX();
                        pix.dispose();
                        pix = newPix;
                    }

                    icon.draw(pix,
                        (int)(weapon.x / scl + icon.width / 2f - out.width / 2f),
                        (int)(-weapon.y / scl + icon.height / 2f - out.height / 2f),
                        true
                    );

                    if(weapon.mirror){
                        var mirror = pix.flipX();

                        icon.draw(mirror,
                            (int)(-weapon.x / scl + icon.width / 2f - out.width / 2f),
                            (int)(-weapon.y / scl + icon.height / 2f - out.height / 2f),
                            true
                        );
                        mirror.dispose();
                    }
                    pix.dispose();
                }
                weapon.load();
            }

            icon.draw(conv(type.region).pixmap(), true);
            int baseColor = Color.valueOf(color, "ffa665").rgba();

            Pixmap baseCell = conv(type.cellRegion).pixmap();
            Pixmap cell = new Pixmap(type.cellRegion.width, type.cellRegion.height);
            cell.each((x, y) -> cell.setRaw(x, y, Color.muli(baseCell.getRaw(x, y), baseColor)));

            icon.draw(cell, icon.width / 2 - cell.width / 2, icon.height / 2 - cell.height / 2, true);

            for(Weapon weapon : type.weapons){
                if(weapon.name.isEmpty()) continue;

                var wepReg = weapon.top ? atlas.find(weapon.name + "-outline") : conv(weapon.region);
                var pix = wepReg.pixmap().copy();

                if(weapon.flipSprite){
                    var newPix = pix.flipX();
                    pix.dispose();
                    pix = newPix;
                }

                icon.draw(pix,
                    (int)(weapon.x / scl + icon.width / 2f - weapon.region.width / 2f),
                    (int)(-weapon.y / scl + icon.height / 2f - weapon.region.height / 2f),
                    true
                );

                if(weapon.mirror){
                    var mirror = pix.flipX();

                    icon.draw(mirror,
                        (int)(-weapon.x / scl + icon.width / 2f - weapon.region.width / 2f),
                        (int)(-weapon.y / scl + icon.height / 2f - weapon.region.height / 2f),
                        true
                    );

                    mirror.dispose();
                }

                pix.dispose();
                weapon.load();
            }

            for(UnitDecorationType decoration : type.decorations){
                if(decoration.top) decoration.drawIcon(r -> conv(r).pixmap(), icon, outliner);
            }

            if(unit instanceof Copterc){
                Pixmap propellers = new Pixmap(icon.width, icon.height);
                Pixmap tops = new Pixmap(icon.width, icon.height);

                for(Rotor rotor : type.rotors){
                    var bladeSprite = conv(rotor.bladeRegion).pixmap();

                    float bladeSeparation = 360f / rotor.bladeCount;

                    float propXCenter = (rotor.x / scl + icon.width / 2f) - 0.5f;
                    float propYCenter = (-rotor.y / scl + icon.height / 2f) - 0.5f;

                    float bladeSpriteXCenter = bladeSprite.width / 2f - 0.5f;
                    float bladeSpriteYCenter = bladeSprite.height / 2f - 0.5f;

                    int propWidth = propellers.width;
                    int propHeight = propellers.height;
                    for(int x = 0; x < propWidth; x++){
                        for(int y = 0; y < propHeight; y++){
                            for(int blade = 0; blade < rotor.bladeCount; blade++){
                                float deg = blade * bladeSeparation;
                                float cos = Mathf.cosDeg(deg);
                                float sin = Mathf.sinDeg(deg);
                                int col = GraphicUtils.getColor(
                                    new PixmapRegion(bladeSprite), color,
                                    ((propXCenter - x) * cos + (propYCenter - y) * sin) / rotor.scale + bladeSpriteXCenter,
                                    ((propXCenter - x) * sin - (propYCenter - y) * cos) / rotor.scale + bladeSpriteYCenter
                                ).rgba();

                                propellers.setRaw(x, y, Pixmap.blend(
                                    propellers.getRaw(x, y),
                                    col
                                ));
                            }
                        }
                    }

                    var topSprite = conv(rotor.topRegion).pixmap();
                    int topXCenter = (int)(rotor.x / scl + icon.width / 2f - topSprite.width / 2f);
                    int topYCenter = (int)(-rotor.y / scl + icon.height / 2f - topSprite.height / 2f);

                    tops.draw(topSprite, topXCenter, topYCenter, true);

                    if(rotor.mirror){
                        propXCenter = (-rotor.x / scl + icon.width / 2f) - 0.5f;
                        topXCenter = (int)(-rotor.x / scl + icon.width / 2f - topSprite.width / 2f);

                        for(int x = 0; x < propWidth; x++){
                            for(int y = 0; y < propHeight; y++){
                                for(int blade = 0; blade < rotor.bladeCount; blade++){
                                    float deg = blade * bladeSeparation;
                                    float cos = Mathf.cosDeg(deg);
                                    float sin = Mathf.sinDeg(deg);
                                    int col = GraphicUtils.getColor(
                                        new PixmapRegion(bladeSprite), color,
                                        ((propXCenter - x) * cos + (propYCenter - y) * sin) / rotor.scale + bladeSpriteXCenter,
                                        ((propXCenter - x) * sin - (propYCenter - y) * cos) / rotor.scale + bladeSpriteYCenter
                                    ).rgba();

                                    propellers.setRaw(x, y, Pixmap.blend(
                                        propellers.getRaw(x, y),
                                        col
                                    ));
                                }
                            }
                        }

                        tops.draw(topSprite, topXCenter, topYCenter, true);
                    }
                }

                var propOutlined = Pixmaps.outline(new PixmapRegion(propellers), type.outlineColor, type.outlineRadius);
                icon.draw(propOutlined, true);
                icon.draw(tops, true);

                propellers.dispose();
                tops.dispose();

                var payloadCell = new Pixmap(baseCell.width, baseCell.height);
                int cellCenterX = payloadCell.width / 2;
                int cellCenterY = payloadCell.height / 2;
                int propCenterX = propOutlined.width / 2;
                int propCenterY = propOutlined.height / 2;

                payloadCell.each((x, y) -> {
                    int cellX = x - cellCenterX;
                    int cellY = y - cellCenterY;

                    float alpha = color.set(propOutlined.get(cellX + propCenterX, cellY + propCenterY)).a;
                    payloadCell.setRaw(x, y, color.set(baseCell.getRaw(x, y)).mul(1, 1, 1, 1 - alpha).rgba());
                });

                propOutlined.dispose();
                add.get(conv(type.region), type.name + "-cell-payload", payloadCell);
            }

            add.get(conv(type.region), type.name + "-full", icon);


        }));
    }

    private int populateColorArray(int[] heightAverageColors, Pixmap bladeSprite, int halfHeight){
        Color
            c1 = new Color(),
            c2 = new Color(),
            c3 = new Color();
        float hits = 0;
        int length = 0;

        for(int y = halfHeight - 1; y >= 0; y--){
            for(int x = 0; x < bladeSprite.width; x++){
                c2.set(bladeSprite.get(x, y));

                if(c2.a > 0){
                    hits++;
                    c1.r += c2.r;
                    c1.g += c2.g;
                    c1.b += c2.b;
                }
            }

            if(hits > 0){
                c1.r = c1.r / hits;
                c1.g = c1.g / hits;
                c1.b = c1.b / hits;
                c1.a = 1f;

                length = Math.max(length, halfHeight - y);

                c1.clamp();
                c3.set(c1).a(0);
            }else{
                // Use color from previous row with alpha 0. This avoids alpha bleeding when interpolating later
                c1.set(c3);
            }

            heightAverageColors[halfHeight - y] = c1.rgba();
            c1.set(0f, 0f, 0f, 0f);

            hits = 0;
        }

        heightAverageColors[length + 1] = heightAverageColors[length] & 0xFF_FF_FF_00; // Set final entry to be fully transparent

        return length;
    }

    // Instead of ACTUALLY accounting for the insanity that is the variation of rotor configurations
    // including counter-rotating propellers and that jazz, number 4 will be used instead.
    private void drawRadial(Pixmap sprite, int[] colorTable, int tableLimit){
        Color
            c1 = new Color(),
            c2 = new Color();

        float spriteCenter = 0.5f - (sprite.height >> 1);

        sprite.each((x, y) -> {
            // 0.5f is required since mathematically it'll put the position at an intersection between 4 pixels, since the sprites are even-sized
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            if(positionLength < tableLimit){
                int arrayIndex = Mathf.clamp((int)positionLength, 0, tableLimit);
                float a = Mathf.cos(Mathf.atan2(x + spriteCenter, y + spriteCenter) * (4 << 1)) * 0.05f + 0.95f;
                a *= a;

                sprite.set(x, y,
                    GraphicUtils.colorLerp(
                        c1.rgba8888(colorTable[arrayIndex]),
                        c2.rgba8888(colorTable[arrayIndex + 1]), positionLength % 1f
                    ).mul(a, a, a, a * (1 - 0.5f / (tableLimit - positionLength + 0.5f)))
                );
            }else{
                sprite.set(x, y, c1.rgba8888(0x00_00_00_00));
            }
        });
    }

    // To help visualize the expected output of this algorithm:
    // - Divide the circle of the rotor's blade into rings, with a new ring every 4 pixels.
    // - Within each band exists a circumferential parallelogram, which the upper and bottom lines are offset differently.
    // - Entire parallelograms are offset as well.
    // The resulting drawing looks like a very nice swooshy hourglass. It must be anti-aliased afterwards.
    private void drawShade(Pixmap sprite, int length){
        float spriteCenter = 0.5f - (sprite.height >> 1);
        // Divide by 2 then round down to nearest even positive number. This array will be accessed by pairs, hence the even number size.
        float[] offsets = new float[length >> 2 & 0xEFFFFFFE];
        for(int i = 0; i < offsets.length; i++){
            // The output values of the noise functions from the noise class are awful that
            // every integer value always result in a 0. Offsetting by 0.5 results in delicious good noise.
            // The additional offset is only that the noise values close to origin make for bad output for the sprite.

            offsets[i] = (float) Noise.rawNoise(i + 2.5f);
        }

        Color c1 = new Color();
        sprite.each((x, y) -> {
            float positionLength = Mathf.len(x + spriteCenter, y + spriteCenter);

            int arrayIndex = Mathf.clamp((int)positionLength >> 2 & 0xEFFFFFFE, 0, offsets.length - 2);
            float offset = GraphicUtils.pythagoreanLerp(offsets[arrayIndex], offsets[arrayIndex + 1], (positionLength / 8f) % 1);

            float a = Mathf.sin(Mathf.atan2(x + spriteCenter, y + spriteCenter) + offset);
            a *= a; // Square the sine wave to make it all positive values
            a *= a; // Square sine again to thin out intervals of value increases
            a *= a; // Sine to the 8th power - Perfection
            // To maintain the geometric-sharpness, the resulting alpha fractional is rounded to binary integer.
            sprite.set(x, y, c1.rgb888(0xFF_FF_FF).a(Mathf.round(a) * Mathf.clamp(length - positionLength, 0f, 1f)));
        });
    }
}
