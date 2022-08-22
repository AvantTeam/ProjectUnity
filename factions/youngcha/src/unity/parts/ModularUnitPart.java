package unity.parts;

import unity.parts.PartType.*;
import unity.util.*;

public class ModularUnitPart extends Part{
    public ModularUnitPart(ModularUnitPartType type, int x, int y){
        this.type = type;
        this.x = x;
        this.y = y;
        panelingIndexes = new int[type.w * type.h];
    }

    public void setupPanellingIndex(ModularUnitPart[][] grid){
        for(int x = 0; x < type.w; x++){
            for(int y = 0; y < type.h; y++){
                panelingIndexes[x + y * type.w] = TilingUtils.getTilingIndex(grid, this.x + x, this.y + y, b -> b != null && !b.type.open);
            }
        }
    }

    @Override
    public ModularUnitPart copy(){
        return new ModularUnitPart((ModularUnitPartType)type, x, y);
    }
}
