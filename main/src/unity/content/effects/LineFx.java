package unity.content.effects;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import mindustry.entities.*;
import unity.graphics.*;

import static arc.graphics.g2d.Lines.*;
import static arc.graphics.g2d.Draw.*;

/**
 * Any effect that use 1 or 2 Position class data.
 */
public class LineFx{
    public static Effect endPointDefence = new Effect(17f, 300f * 2f, e -> {
        if(!(e.data instanceof Position)) return;
        Position data = (Position)e.data;

        for(int i = 0; i < 2; i++){
            float width = (2 - i) * 2.2f * e.fout();
            color(i == 0 ? UnityPal.scarColor : Color.white);
            stroke(width);
            line(e.x, e.y, data.getX(), data.getY(), false);
            Fill.circle(e.x, e.y, width);
            Fill.circle(data.getX(), data.getY(), width);
        }
    });
}
