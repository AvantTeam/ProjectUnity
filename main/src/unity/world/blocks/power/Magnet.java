package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import unity.entities.bullet.*;
import unity.world.blocks.*;

import static arc.Core.atlas;

public class Magnet extends GraphBlock{
    final TextureRegion[] regions = new TextureRegion[4];//basesprite

    public Magnet(String name){
        super(name);
        rotate = solid = true;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) regions[i] = atlas.find(name + (i + 1));
    }

    public class MagnetBuild extends GraphBuild{
        @Override
        public void updatePre(){
            if(hasPower) flux().mulFlux(power.graph.getSatisfaction());
        }

        @Override
        public void draw(){
            Draw.rect(regions[rotation], x, y);
            drawTeamTop();
        }

        @Override
        public void updatePost(){
            float f = flux().flux();//lmao
            Groups.bullet.intersect(x - f * 2f, y - f * 2f, f * 4f, f * 4f, bullet -> {
                if(bullet.type == null) return;
                boolean isOrb = bullet.type instanceof ExpOrb;
                if(bullet.type.hittable || isOrb){
                    float dx = bullet.x - x;
                    float dy = bullet.y - y;
                    float dis = Mathf.sqrt(dx * dx + dy * dy);//ldis
                    if(dis < f * 2f){
                        float mul = 1f / Math.max(1f, bullet.type.estimateDPS() / 10f) * (isOrb ? 5f : 1f) * Time.delta * 0.1f * f / (8f + dis);//invmass*forcemag
                        bullet.vel.x += mul * Geometry.d4x(rotation);
                        bullet.vel.y += mul * Geometry.d4y(rotation);
                    }
                }
            });
        }
    }
}
