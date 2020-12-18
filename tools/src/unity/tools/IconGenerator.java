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
        content.units().each(type -> {
            if(type.minfo.mod == null) return;

            ObjectSet<String> outlined = new ObjectSet<>();

            try{
                type.load();
                type.init();

                Color outc = Pal.darkerMetal;
                Func<Sprite, Sprite> outline = i -> i.outline(3, outc);
                Func<TextureRegion, String> parseName = reg -> ((AtlasRegion)reg).name.replaceFirst("unity-", "");
                Cons<TextureRegion> outliner = t -> {
                    if(t != null){
                        String fname = parseName.get(t);

                        if(SpriteProcessor.has(fname)){
                            Sprite sprite = SpriteProcessor.get(fname);
                            sprite.draw(outline.get(sprite));

                            sprite.save(fname);
                        }else if(!fname.contains("-joint")){ //joint is optional sprite
                            Log.warn("@ not found", fname);
                        }
                    }else{
                        Log.warn("A region is null");
                    }
                };

                for(Weapon weapon : type.weapons){
                    String fname = weapon.name.replaceFirst("unity-", "");

                    if(outlined.add(fname) && SpriteProcessor.has(fname)){
                        outline.get(SpriteProcessor.get(fname)).save(fname + "-outline");
                    }
                }

                Unit unit = type.constructor.get();

                if(unit instanceof Legsc){
                    outliner.get(type.jointRegion);
                    outliner.get(type.footRegion);
                    outliner.get(type.legBaseRegion);
                    outliner.get(type.baseJointRegion);
                    outliner.get(type.legRegion);
                }

                if(unit instanceof Mechc){
                    outliner.get(type.legRegion);
                }

                if(unit instanceof Copterc){
                    UnityUnitType t = (UnityUnitType)type;
                    Rotor rotor = t.rotors.first();

                    outliner.get(rotor.bladeRegion);
                    outliner.get(rotor.topRegion);
                }

                String fname = parseName.get(type.region);

                if(unit instanceof Wormc){
                    Sprite body = outline.get(SpriteProcessor.get(fname + "-body"));
                    Sprite tail = outline.get(SpriteProcessor.get(fname + "-tail"));

                    body.save(fname + "-outline");
                    tail.save(fname + "-outline");
                }

                Sprite outl = outline.get(SpriteProcessor.get(fname));
                outl.save(fname + "-outline");

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

                icon.save(fname + "-full");

                for(Cicon i : Cicon.scaled){
                    Vec2 size = Scaling.fit.apply(icon.width, icon.height, i.size, i.size);
                    Sprite scaled = new Sprite((int)size.x, (int)size.y);

                    scaled.drawScaled(icon);
                    scaled.save("ui/" + fname + "-" + i.name());
                }
            }catch(IllegalArgumentException e){
                Log.err("Skipping unit @: @", type.name, e.getMessage());
            }
        });
    }
}
