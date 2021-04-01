package unity.world.blocks.power;

import arc.Core;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.content.Blocks;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.power.*;
import unity.content.*;

import static mindustry.Vars.*;

public class Absorber extends PowerGenerator {
    public float range = 50f;
    public int capacity = 5;
    public float powerProduction = 0.5f;
    public TextureRegion laserRegion, laserEndRegion;

    public Absorber(String name){
        super(name);
        update = true;
    }

    @Override
    public void load(){
        super.load();

        laserRegion = Core.atlas.find("laser");
        laserEndRegion = Core.atlas.find("laser-end");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing);
    }

    public class AbsorberBuilding extends GeneratorBuild {
        public Seq<Unit> units = new Seq<Unit>();
        public Seq<Unit> units2 = new Seq<Unit>();
        public int index;

        @Override
        public void update(){
            super.update();

            Units.nearbyEnemies(this.team, this.x - range, this.y - range, range * 2, range * 2, e -> {
                if(!e.dead() && !e.isFlying() && Mathf.sqrt(Mathf.sqr(e.x - x) + Mathf.sqr(e.y - y)) <= range && index <= capacity){
                    e.apply(StatusEffects.slow);
                    units.add(e);
                    index += 1;
                }
            });
            productionEfficiency = units.size;
            units2 = units;
            units = new Seq<Unit>();

            index = 0;
        }

        @Override
        public float getPowerProduction(){
            return powerProduction * productionEfficiency;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, team.color);
        }

        @Override
        public void draw(){
            super.draw();

            Draw.color(this.team.color);
            Draw.z(Layer.flyingUnit + 1);
            units2.each(e -> {
                Draw.alpha(1);
                Drawf.laser(team, laserRegion, laserEndRegion, x, y, e.x, e.y, 0.6f);
                Draw.alpha(0.2f);
                for(int i = 0; i < 6; i++){
                    Fill.circle(e.x, e.y, e.hitSize + i + Mathf.sin(Time.time / 8) / 3);
                }
            });
            Draw.alpha(1);
            Draw.reset();
        }
    }
}
