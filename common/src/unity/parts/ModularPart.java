package unity.parts;

import unity.util.*;

public class ModularPart{
    public ModularPartType type;
    int x, y;
    //position of lowest left tile
    public float ax, ay;
    //middle
    public float cx, cy;
    public int[] panelingIndexes;
    //which lighting variation to draw
    int front = 0;

    //editor only fields
    boolean valid = false;


    public ModularPart(ModularPartType type, int x, int y){
        this.type = type;
        this.x = x;
        this.y = y;
        panelingIndexes = new int[type.w * type.h];
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setPos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public float getAx(){
        return ax;
    }

    public float getAy(){
        return ay;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean isHere(int x_, int y_){
        return x == x_ && y == y_;
    }

    public float getCx(){
        return cx;
    }

    public float getCy(){
        return cy;
    }

    public void setupPanellingIndex(ModularPart[][] grid){
        for(int x = 0; x < type.w; x++){
            for(int y = 0; y < type.h; y++){
                panelingIndexes[x + y * type.w] = TilingUtils.getTilingIndex(grid, this.x + x, this.y + y, b -> b != null && !b.type.open);
            }
        }
    }
}
