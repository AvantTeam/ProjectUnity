package unity.world.blocks.effect;

import arc.Core;
import arc.math.*;
import arc.util.*;
import arc.graphics.g2d.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import unity.content.*;


public class Reinforcer extends Block {
    public float range = 20f;
    public TextureRegion rotator;
    public Reinforcer(String name){
        super(name);
        update = true;
        acceptsItems = true;
        itemCapacity = 15;
    }

    @Override
    public void load(){
        region = Core.atlas.find(name);
        teamRegion = Core.atlas.find(name + "team");
        rotator = Core.atlas.find(name + "rotator");
    }

    public class ReinforcerBuilding extends Building {
        public float load = 0f;
        public float angle = 0f;
        public Unit unit;

        @Override
        public void updateTile(){
            if(consValid() && load >= 1f){
                unit = Units.closest(this.team, x, y, range, u -> u != null && !u.hasEffect(UnityStatusEffects.plated));

                if(unit != null) {
                    unit.apply(UnityStatusEffects.plated);
                    load = 0f;
                    items.remove(UnityItems.metalPlating, 15);
                }
            }
            load += 0.01f * Time.delta;
            if(load > 1f) load = 1f;
        }

        public void draw(){
            if(unit != null){
                Draw.color(Pal.thoriumPink);
                Draw.z(Layer.effect);
                Drawf.laser(this.team, Core.atlas.find("unity-pointy-laser"), Core.atlas.find("unity-pointy-laser-end"), x, y, unit.x, unit.y, 1f - load);
                Draw.color();
                angle = Angles.angle(x, y, unit.x, unit.y);
            }

            Draw.z(Layer.block);
            Draw.rect(region, x, y);
            if(teamRegion != Core.atlas.find("error")) Draw.rect(teamRegion, x, y);
            Draw.rect(rotator, x, y, angle - 90);
        }
    }
}
