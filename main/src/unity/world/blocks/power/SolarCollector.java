package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.world.blocks.power.SolarReflector.*;

import static arc.Core.*;

public class SolarCollector extends HeatGenerator{
    public final TextureRegion[] regions = new TextureRegion[4];
    public TextureRegion lightRegion;

    public SolarCollector(String name){
        super(name);

        rotate = solid = true;
    }

    @Override
    public void load(){
        super.load();

        lightRegion = atlas.find(name + "-light");
        for(int i = 0; i < 4; i++) regions[i] = atlas.find(name + (i + 1));
    }

    public class SolarCollectorBuild extends HeatGeneratorBuild{
        final OrderedSet<SolarReflectorBuild> linkedReflect = new OrderedSet<>(8);
        float thermalPwr;

        float getThermalPowerCoeff(SolarReflectorBuild ref){
            float dst = Mathf.dst(ref.x, ref.y, x, y);

            Point2 dir = Geometry.d4(rotation);

            return Mathf.clamp((dir.x * (ref.x - x) / dst + dir.y * (ref.y - y) / dst) * 1.5f);
        }

        void recalcThermalPwr(){
            thermalPwr = 0f;

            if(linkedReflect.isEmpty()) return;
            for(var i : linkedReflect) thermalPwr += getThermalPowerCoeff(i);
        }

        public void appendSolarReflector(SolarReflectorBuild ref){
            linkedReflect.add(ref);
            recalcThermalPwr();
        }

        public void removeReflector(SolarReflectorBuild ref){
            if(linkedReflect.remove(ref)) recalcThermalPwr();
        }

        @Override
        public void onDelete(){
            Seq<SolarReflectorBuild> items = linkedReflect.orderedItems();

            while(!items.isEmpty()) items.first().setLink(-1);
        }

        @Override
        public void updatePost(){
            generateHeat(thermalPwr, thermalPwr);
        }

        @Override
        public void draw(){
            Draw.rect(regions[rotation], x, y);
            UnityDrawf.drawHeat(heatRegion, x, y, rotdeg(), heat().getTemp());

            if(thermalPwr > 0f){
                Draw.z(Layer.effect);
                Draw.color(thermalPwr, thermalPwr, thermalPwr);
                Draw.rect(lightRegion, x, y, rotdeg());
                Draw.z();
            }

            drawTeamTop();
        }
    }
}
