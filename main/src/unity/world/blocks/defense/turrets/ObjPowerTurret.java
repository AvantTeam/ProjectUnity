package unity.world.blocks.defense.turrets;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.util.*;

public class ObjPowerTurret extends PowerTurret{
    public WavefrontObject object;

    public ObjPowerTurret(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        baseRegion = region;
    }

    public class ObjPowerTurretBuild extends PowerTurretBuild{
        float time = 0f;
        float distortionTime = 0f;

        @Override
        public void updateTile(){
            super.updateTile();
            if(Float.isNaN(time)) time = 0f;
            time += efficiency() * (1f + ((reload * 2.5f) / reloadTime)) * Time.delta;
            distortionTime = Math.max(0f, distortionTime - (Time.delta * 0.2f));
        }

        @Override
        public void damage(float damage){
            distortionTime = Mathf.clamp(Mathf.sqrt(Math.max(0f, damage / 20f)), 0f, 3f);
            super.damage(damage);
        }

        protected float getDistortion(){
            return ((Mathf.clamp(1f - (healthf() * 2f)) * 2f) + distortionTime) / 16f;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            Cons<Vec3> distort = v -> {
                if(getDistortion() >= 0.001f){
                    v.add(Mathf.range(getDistortion()), Mathf.range(getDistortion()), Mathf.range(getDistortion()));
                }
            };

            object.draw(x, y, Mathf.cos(time, 76f, 120f), Mathf.sin(time, 76f, 120f), -rotation, distort);
            //model.draw(x, y, 0f, 0f, -rotation);
        }
    }
}
