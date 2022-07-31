package unity.parts;

import unity.parts.PartType.*;
import unity.util.*;

public class ModularUnitPart extends Part{
    //editor only fields
    boolean valid = false;


    public ModularUnitPart(ModularUnitPartType type, int x, int y){
        this.type = type;
        this.x = x;
        this.y = y;
        panelingIndexes = new int[type.w * type.h];
    }

    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public boolean isHere(int x_, int y_){
        return x == x_ && y == y_;
    }

    public void setupPanellingIndex(ModularUnitPart[][] grid){
        for(int x = 0; x < type.w; x++){
            for(int y = 0; y < type.h; y++){
                panelingIndexes[x + y * type.w] = TilingUtils.getTilingIndex(grid, this.x + x, this.y + y, b -> b != null && !b.type.open);
            }
        }
    }
}
