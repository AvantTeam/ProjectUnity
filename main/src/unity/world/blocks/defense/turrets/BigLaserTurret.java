package unity.world.blocks.defense.turrets;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.util.*;

public class BigLaserTurret extends LaserTurret{
    public BigLaserTurret(String name){
        super(name);
        heatDrawer = tile -> {
            if(tile.heat <= 0.00001f) return;
            
            float r = Interp.pow2Out.apply(tile.heat);
            float g = Interp.pow3In.apply(tile.heat) + ((1 - Interp.pow3In.apply(tile.heat)) * 0.12f);
            float b = Utils.pow6In.apply(tile.heat);
            float a = Interp.pow2Out.apply(tile.heat);
            
            Tmp.c1.set(r, g, b, a);
            Draw.color(Tmp.c1);
            Draw.blend(Blending.additive);
            Draw.rect(heatRegion, tile.x + tr2.x, tile.y + tr2.y, tile.rotation - 90f);
            Draw.color();
            Draw.blend();
        };
    }

    @Override
    public void load(){
        super.load();
        baseRegion = Core.atlas.find("unity-block-" + size);
    }
}
