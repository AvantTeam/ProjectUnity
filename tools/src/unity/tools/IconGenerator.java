package unity.tools;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.entities.*;
import unity.entities.units.*;
import unity.gen.*;
import unity.type.*;

import static mindustry.Vars.*;
import static unity.tools.SpriteProcessor.*;

public class IconGenerator implements Generator{
    @Override
    public void generate(){
        content.units().each(t -> {
            if(t.minfo.mod == null || !(t instanceof UnityUnitType type)) return;

            ObjectSet<String> outlined = new ObjectSet<>();
            try{
                type.init();
                type.load();
                type.loadIcon();

                Func<Pixmap, Pixmap> outline = i -> i.outline(type.outlineColor, type.outlineRadius);
                Cons<TextureRegion> outliner = tr -> {
                    if(tr != null && tr.found()){
                        replace(tr, outline.get(get(tr)));
                    }
                };

                for(Weapon weapon : type.weapons){
                    String fname = fixName(weapon.name);
                    if(outlined.add(fname) && has(fname)){
                        save(outline.get(get(fname)), fname, fname + "-outline");
                    }
                }

                for(Weapon weapon : type.segWeapSeq){
                    String fname = fixName(weapon.name);
                    if(outlined.add(fname) && has(fname)){
                        save(outline.get(get(fname)), fname, fname + "-outline");
                    }
                }

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
                        String fname = fixName(type.name) + "-rotor";
                        if(outlined.add(fname + "-blade")){
                            save(outline.get(get(fname + "-blade")), fixName(type.name), fname + "-outline");
                        }

                        if(outlined.add(fname + "-top")){
                            outliner.get(rotor.topRegion);
                        }
                    }
                }

                if(unit instanceof WormDefaultUnit){
                    save(outline.get(get(type.segmentRegion)), fixName(type.segmentRegion), fixName(type.segmentRegion) + "-outline");
                    save(outline.get(get(type.tailRegion)), fixName(type.tailRegion), fixName(type.tailRegion) + "-outline");
                }

                for(TextureRegion reg : type.abilityRegions){
                    String fname = fixName(reg);
                    if(outlined.add(fname) && has(fname)){
                        outliner.get(reg);
                    }
                }

                for(TentacleType tentacle : type.tentacles){
                    String fname = fixName(tentacle.name);
                    if(outlined.add(fname) && has(fname)){
                        outliner.get(tentacle.region);
                    }

                    if(outlined.add(fname + "-tip") && has(fname + "-tip")){
                        outliner.get(tentacle.tipRegion);
                    }
                }

                String fname = fixName(type.name);

                Pixmap icon = outline.get(get(type.region));
                save(icon, fname, fname + "-outline");

                if(unit instanceof Mechc){
                    drawCenter(icon, get(type.baseRegion));
                    drawCenter(icon, get(type.legRegion));
                    drawCenter(icon, get(type.legRegion).flipX());
                    icon.draw(get(type.region), true);
                }

                for(Weapon weapon : type.weapons){
                    weapon.load();
                    String wname = fixName(weapon.name);

                    if((!weapon.top || type.bottomWeapons.contains(weapon.name)) && has(wname)){
                        icon.draw(weapon.flipSprite ? outline.get(get(weapon.region)).flipX() : outline.get(get(weapon.region)),
                            (int)(weapon.x / Draw.scl + icon.width / 2f - weapon.region.width / 2f),
                            (int)(-weapon.y / Draw.scl + icon.height / 2f - weapon.region.height / 2f),
                            true
                        );
                    }
                }

                icon.draw(get(type.region), true);
                int baseColor = Color.valueOf("ffa665").rgba();

                Pixmap baseCell = get(type.cellRegion);
                Pixmap cell = new Pixmap(type.cellRegion.width, type.cellRegion.height);
                cell.each((x, y) -> cell.set(x, y, Color.muli(baseCell.get(x, y), baseColor)));

                icon.draw(cell, icon.width / 2 - cell.width / 2, icon.height / 2 - cell.height / 2, true);

                for(Weapon weapon : type.weapons){
                    weapon.load();

                    Pixmap wepReg = weapon.top ? outline.get(get(weapon.region)) : get(weapon.region);
                    if(weapon.flipSprite){
                        wepReg = wepReg.flipX();
                    }

                    icon.draw(wepReg,
                        (int)(weapon.x / Draw.scl + icon.width / 2f - weapon.region.width / 2f),
                        (int)(-weapon.y / Draw.scl + icon.height / 2f - weapon.region.height / 2f),
                        true
                    );
                }

                if(unit instanceof Copterc){
                    Pixmap propellers = new Pixmap(icon.width, icon.height);
                    Pixmap tops = new Pixmap(icon.width, icon.height);

                    for(Rotor rotor : type.rotors){
                        String rname = fixName(rotor.name);
                        Pixmap bladeSprite = get(rname + "-blade");

                        float bladeSeparation = 360f / rotor.bladeCount;

                        float propXCenter = (rotor.x / Draw.scl + icon.width / 2f) - 0.5f;
                        float propYCenter = (-rotor.y / Draw.scl + icon.height / 2f) - 0.5f;

                        float bladeSpriteXCenter = bladeSprite.width / 2f - 0.5f;
                        float bladeSpriteYCenter = bladeSprite.height / 2f - 0.5f;

                        propellers.each((x, y) -> {
                            for(int blade = 0; blade < rotor.bladeCount; blade++){
                                // Ideally the rotation would be offset, but as per Mindustry's significance of symmetry
                                // unit composites should have rotors in symmetrical configurations. This is at EoD's request
                                float deg = blade * bladeSeparation/* + rotor.rotOffset*/;
                                float cos = Mathf.cosDeg(deg);
                                float sin = Mathf.sinDeg(deg);

                                Tmp.c1.set(getColor(
                                    bladeSprite,
                                    ((propXCenter - x) * cos + (propYCenter - y) * sin) / rotor.scale + bladeSpriteXCenter,
                                    ((propXCenter - x) * sin - (propYCenter - y) * cos) / rotor.scale + bladeSpriteYCenter
                                ));

                                propellers.set(x, y, Tmp.c1);
                            }
                        });

                        Pixmap topSprite = get(rname + "-top");
                        int topXCenter = (int)(rotor.x / Draw.scl + icon.width / 2f - topSprite.width / 2f);
                        int topYCenter = (int)(-rotor.y / Draw.scl + icon.height / 2f - topSprite.height / 2f);

                        tops.draw(topSprite, topXCenter, topYCenter, true);
                    }

                    Pixmap propOutlined = outline.get(propellers);

                    icon.draw(propOutlined, true);
                    icon.draw(outline.get(tops), true);

                    Pixmap payloadCell = new Pixmap(baseCell.width, baseCell.height);
                    int cellCenterX = payloadCell.width / 2;
                    int cellCenterY = payloadCell.height / 2;
                    int propCenterX = propOutlined.width / 2;
                    int propCenterY = propOutlined.height / 2;

                    payloadCell.each((x, y) -> {
                        int cellX = x - cellCenterX;
                        int cellY = y - cellCenterY;

                        float alpha = color.set(propOutlined.get(cellX + propCenterX, cellY + propCenterY)).a;
                        payloadCell.set(x, y, color.set(baseCell.get(x, y)).mul(1, 1, 1, 1 - alpha));
                    });

                    save(payloadCell, fname, fname + "-cell-payload");
                }

                //icon.antialias().save(fname + "-full");
                save(icon, fname, fname + "-full");
            }catch(Throwable e){
                if(e instanceof IllegalArgumentException i){
                    Log.err("Skipping unit @: @", type, i.getMessage());
                }else{
                    Log.err(Strings.format("Problematic unit @", type), e);
                }
            }
        });
    }
}
