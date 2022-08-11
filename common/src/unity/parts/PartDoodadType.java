package unity.parts;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import unity.parts.PartType.*;
import unity.util.*;

public class PartDoodadType{
    Point2[] needsPoints;
    TextureRegion front, outline;
    public int w;
    public int h;

    public PartDoodadType(Point2[] needsPoints, TextureRegion front, TextureRegion outline, int w, int h){
        this.needsPoints = needsPoints;
        this.front = front;
        this.outline = outline;
        this.w = w;
        this.h = h;
    }

    public partDoodad create(float x, float y, boolean flipped){
        return new partDoodad(this, x, y, flipped);
    }

    //from the corner
    public boolean canFit(Part[][] grid, int x, int y){
        if(x + w >= grid.length || y + h >= grid[0].length){
            return false;
        }
        for(Point2 p : needsPoints){
            if(grid[p.x + x][p.x + y] == null || grid[p.x + x][p.x + y].type.open){
                return false;
            }
        }
        return true;
    }

    public static class partDoodad{
        public PartDoodadType type;
        float x, y;
        boolean flipped;

        public partDoodad(PartDoodadType type, float x, float y, boolean flipped){
            this.type = type;
            this.x = x;
            this.y = y;
            this.flipped = flipped;
        }

        public void drawTop(DrawTransform dt){
            dt.drawRectScl(type.front, x, y, flipped ? -1 : 1, 1);
        }

        public void drawOutline(DrawTransform dt){
            dt.drawRectScl(type.outline, x, y, flipped ? -1 : 1, 1);
        }
    }
}
