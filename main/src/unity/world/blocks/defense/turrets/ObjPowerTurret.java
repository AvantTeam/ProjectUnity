package unity.world.blocks.defense.turrets;

import arc.graphics.g2d.*;
import arc.math.*;
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
        object.load();

        baseRegion = region;
    }

    public class ObjPowerTurretBuild extends PowerTurretBuild{
        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color();

            object.draw(x, y, Mathf.cos(Time.time, 76f, 120f), Mathf.sin(Time.time, 76f, 120f), -rotation);
            //object.draw(x, y, 0f, 0f, -rotation);
        }
    }
}
