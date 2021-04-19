package unity.world.blocks.effect;

import arc.Core;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.content.*;

import static mindustry.Vars.*;


public class Reinforcer extends Block {
    public float range = 60f;
    public TextureRegion baseRegion, laserRegion, laserEndRegion;
    public float laserLength = -1f;
    public Color laserColor = Pal.thoriumPink;
    public float rotateSpeed = 2f;
    public float loadThreshold = 1f;
    public float cone = 2f;

    public Reinforcer(String name){
        super(name);
        update = true;
        acceptsItems = true;
        hasItems = true;
        outlineIcon = true;
    }

    @Override
    public void load(){
        super.load();

        baseRegion = Core.atlas.find("block-" + size);
        laserRegion = Core.atlas.find("unity-pointy-laser");
        laserEndRegion = Core.atlas.find("unity-pointy-laser-end");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{baseRegion, region};
    }

    @Override
    public void init(){
        super.init();
        if(laserLength < 0) laserLength = size * tilesize / 2f;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class ReinforcerBuilding extends Building {
        public float load = loadThreshold;
        public float laserWidth = 0.3f;
        public float rotation = 90f;
        public float targetRot = 90f;
        public Vec2 posOffset = new Vec2(0, 0);
        public Unit unit;
        public boolean canReinforce = false;
        public Unit prevUnit;

        @Override
        public void updateTile(){
            if(consValid()){
                unit = Units.closest(this.team, x, y, range, u -> u != null && !u.hasEffect(UnityStatusEffects.plated));

                if(unit != null) {
                    prevUnit = unit;

                    turnToTarget(Angles.angle(x, y, unit.x, unit.y));

                    targetRot = Angles.angle(x, y, unit.x, unit.y);

                    canReinforce = Angles.angleDist(rotation, targetRot) <= cone;

                    if (canReinforce && load >= loadThreshold) {
                        unit.apply(UnityStatusEffects.plated);
                        load = 0f;
                        laserWidth = 0f;
                        items.remove(consumes.getItem().items);
                    }
                }
            }


            load += 0.01f * Time.delta;
            laserWidth += 0.01f * Time.delta;
            if(load > loadThreshold) load = loadThreshold;
            if(laserWidth > 0.3f) laserWidth = 0.3f;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, team.color);
        }

        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.z(Layer.block);
            Draw.rect(region, x, y, rotation - 90);

            if(prevUnit != null && laserWidth < 0.3f) {
                Draw.color(laserColor);
                Draw.z(Layer.effect);
                if(laserLength > 0f) posOffset.trns(rotation, laserLength);
                Drawf.laser(this.team, laserRegion, laserEndRegion, x + posOffset.x, y + posOffset.y, prevUnit.x, prevUnit.y, (0.3f - laserWidth) / 0.3f);
                Draw.color();
            }
        }

        public void turnToTarget(float targetRot){
            rotation = Angles.moveToward(rotation, targetRot, rotateSpeed * Time.delta);
        }
    }
}
