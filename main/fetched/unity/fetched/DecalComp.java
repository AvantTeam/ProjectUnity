package unity.fetched;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import unity.annotations.Annotations.*;
import mindustry.gen.*;
import mindustry.graphics.*;

@EntityComponent(base = true, write = false)
abstract class DecalComp implements Drawc, Timedc, Rotc, Posc{
    @Import float x, y, rotation;

    Color color = new Color(1, 1, 1, 1);
    TextureRegion region;

    @Override
    public void draw(){
        Draw.z(Layer.scorch);

        Draw.mixcol(color, color.a);
        Draw.alpha(1f - Mathf.curve(fin(), 0.98f));
        Draw.rect(region, x, y, rotation);
        Draw.reset();
    }

    @Replace
    public float clipSize(){
        return region.width *2;
    }

}
