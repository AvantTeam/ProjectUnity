package unity.parts;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;

//Generic base regions for parts. Same as turret's one.
public class PartDoodadPalette{
    public boolean center;
    public boolean sides;
    public int w, h;
    public Seq<PartDoodadType> doodads = new Seq<>();
    int amount;
    String name;

    public PartDoodadPalette(boolean center, boolean sides, int w, int h, String name, int amount){
        this.center = center;
        this.sides = sides;
        this.w = w;
        this.h = h;
        this.amount = amount;
        this.name = name;
    }

    public void load(){
        Point2[] points = new Point2[w * h];
        for(int i = 0; i < w * h; i++){
            points[i] = new Point2(i % w, i / w);
        }
        for(int i = 0; i < amount; i++){
            var d = new PartDoodadType(points, Core.atlas.find("unity-doodad-" + name + "-" + (i + 1)), Core.atlas.find("unity-doodad-" + name + "-outline-" + (i + 1)), w, h);
            doodads.add(d);
        }
    }

    public PartDoodadType get(float b){
        return doodads.get((int)Mathf.clamp(doodads.size * b, 0, doodads.size - 1));
    }

}
