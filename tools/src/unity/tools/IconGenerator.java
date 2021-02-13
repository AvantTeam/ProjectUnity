package unity.tools;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.entities.comp.*;
import unity.type.*;

import static mindustry.Vars.*;

public class IconGenerator implements Generator{
    @Override
    public void generate(){
        content.units().each(t -> {
            if(t.minfo.mod == null || !(t instanceof UnityUnitType)) return;

            UnityUnitType type = (UnityUnitType)t;
            ObjectSet<String> outlined = new ObjectSet<>();

            try{
                type.load();
                type.init();

                Color outc = Pal.darkerMetal;
                Func<Sprite, Sprite> outline = i -> i.outline(3, outc);
                Func<TextureRegion, String> parseName = reg -> ((AtlasRegion)reg).name.replaceFirst("unity-", "");
                Seq<String> optional = Seq.with("-joint", "-leg-back", "-leg-base-back", "-foot-back");

                Cons<TextureRegion> outliner = tr -> {
                    if(tr != null){
                        String fname = parseName.get(tr);

                        if(SpriteProcessor.has(fname)){
                            Sprite sprite = SpriteProcessor.get(fname);
                            sprite.draw(outline.get(sprite));

                            sprite.save(fname);
                        }else if(!optional.contains(fname::contains)){
                            Log.warn("@ not found", fname);
                        }
                    }else{
                        Log.warn("A region is null");
                    }
                };
                Cons2<String, TextureRegion> outlSeparate = (suff, tr) -> {
                    if(tr != null){
                        String fname = parseName.get(tr);

                        if(SpriteProcessor.has(fname)){
                            Sprite sprite = SpriteProcessor.get(fname);
                            sprite.draw(outline.get(sprite));

                            sprite.save(fname + "-" + suff);
                        }else{
                            Log.warn("@ not found", fname);
                        }
                    }else{
                        Log.warn("A region is null");
                    }
                };

                for(Weapon weapon : type.weapons){
                    String fname = weapon.name.replaceFirst("unity-", "");

                    if(outlined.add(fname) && SpriteProcessor.has(fname)){
                        outlSeparate.get("outline", weapon.region);
                    }
                }

                Unit unit = type.constructor.get();

                if(unit instanceof Legsc){
                    outliner.get(type.jointRegion);
                    outliner.get(type.footRegion);
                    outliner.get(type.legBaseRegion);
                    outliner.get(type.baseJointRegion);
                    outliner.get(type.legRegion);

                    outliner.get(type.legBackRegion);
                    outliner.get(type.legBaseBackRegion);
                    outliner.get(type.footBackRegion);
                }

                if(unit instanceof Mechc){
                    outliner.get(type.legRegion);
                }

                if(unit instanceof Copterc){
                    for(Rotor rotor : type.rotors){
                        String fname = type.name.replaceFirst("unity-", "") + "-rotor";

                        if(outlined.add(fname + "-blade")){
                            outlSeparate.get("outline", rotor.bladeRegion);
                        }
                        if(outlined.add(fname + "-top")){
                            outliner.get(rotor.topRegion);
                        }
                    }
                }

                if(unit instanceof Wormc){
                    outlSeparate.get("outline", type.bodyRegion);
                    outlSeparate.get("outline", type.tailRegion);
                }

                for(TextureRegion reg : type.abilityRegions){
                    String fname = parseName.get(reg);
                    if(outlined.add(fname) && SpriteProcessor.has(fname)){
                        outliner.get(reg);
                    }
                }

                outlSeparate.get("outline", type.region);

                String fname = parseName.get(type.region);
                Sprite outl = outline.get(SpriteProcessor.get(fname));
                Sprite region = SpriteProcessor.get(fname);

                Sprite icon = Sprite.createEmpty(region.width, region.height);
                icon.draw(region);
                icon.draw(outline.get(region));

                if(unit instanceof Mechc){
                    Sprite leg = SpriteProcessor.get(parseName.get(type.legRegion));
                    icon.drawCenter(leg);
                    icon.drawCenter(leg, true, false);

                    icon.draw(region);
                    icon.draw(outl);
                }

                for(Weapon weapon : type.weapons){
                    weapon.load();
                    String wname = weapon.name.replaceFirst("unity-", "");

                    if(!weapon.top && SpriteProcessor.has(wname)){
                        Sprite weapSprite = SpriteProcessor.get(wname);

                        icon.draw(weapSprite,
                            (int)(weapon.x * 4f / Draw.scl + icon.width / 2f - weapSprite.width / 2f),
                            (int)(-weapon.y * 4f / Draw.scl + icon.height / 2f - weapSprite.height / 2f),
                            weapon.flipSprite, false
                        );

                        icon.draw(outline.get(weapSprite),
                            (int)(weapon.x * 4f / Draw.scl + icon.width / 2f - weapSprite.width / 2f),
                            (int)(-weapon.y * 4f / Draw.scl + icon.height / 2f - weapSprite.height / 2f),
                            weapon.flipSprite, false
                        );
                    }
                }

                icon.draw(region);
                icon.draw(outl);

                Sprite baseCell = SpriteProcessor.get(parseName.get(type.cellRegion));
                Sprite cell = new Sprite(baseCell.width, baseCell.height);
                cell.each((x, y) -> cell.draw(x, y, baseCell.getColor(x, y).mul(Color.valueOf("ffa665"))));

                icon.draw(cell, icon.width / 2 - cell.width / 2, icon.height / 2 - cell.height / 2);

                for(Weapon weapon : type.weapons){
                    weapon.load();
                    String wname = weapon.name.replaceFirst("unity-", "");

                    if(SpriteProcessor.has(wname)){
                        Sprite weapSprite = SpriteProcessor.get(wname);

                        icon.draw(weapSprite,
                            (int)(weapon.x * 4f / Draw.scl + icon.width / 2f - weapSprite.width / 2f),
                            (int)(-weapon.y * 4f / Draw.scl + icon.height / 2f - weapSprite.height / 2f),
                            weapon.flipSprite, false
                        );

                        if(weapon.top){
                            icon.draw(outline.get(weapSprite),
                                (int)(weapon.x * 4f / Draw.scl + icon.width / 2f - weapSprite.width / 2f),
                                (int)(-weapon.y * 4f / Draw.scl + icon.height / 2f - weapSprite.height / 2f),
                                weapon.flipSprite, false
                            );
                        }
                    }
                }

                genIcon(icon, fname);
            }catch(Exception e){
                if(e instanceof IllegalArgumentException i){
                    Log.err("Skipping unit @: @", type.name, i.getMessage());
                }else{
                    Log.err(e);
                }
            }
        });
    }

    private void genIcon(Sprite sprite, String name) {
        sprite.save(name + "-full");
        for(Cicon i : Cicon.scaled){
            Vec2 size = Scaling.fit.apply(sprite.width, sprite.height, i.size, i.size);
            Sprite scaled = new Sprite((int)size.x, (int)size.y);

            scaled.drawScaled(sprite);
            scaled.save("ui/" + name + "-" + i.name());
        }
    }
}
