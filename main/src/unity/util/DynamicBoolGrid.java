package unity.util;

import arc.math.*;

import java.util.*;

public class DynamicBoolGrid{
    boolean[][] array;
    boolean multiArray;
    int width, height;

    public DynamicBoolGrid(){
        this(true);
    }

    public DynamicBoolGrid(boolean multi){
        multiArray = multi;
        if(!multi){
            array = new boolean[1][1];
        }
    }

    public void updateSize(int newWidth, int newHeight){
        if(newWidth != width || newHeight != height){
            if(multiArray){
                array = new boolean[newWidth][newHeight];
            }else{
                array[0] = new boolean[newWidth * newHeight];
            }
        }
        width = newWidth;
        height = newHeight;
    }

    public void clear(){
        if(multiArray){
            array = new boolean[width][height];
        }else{
            Arrays.fill(array[0], false);
        }
    }

    public boolean within(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public boolean get(int x, int y){
        if(!multiArray) return array[0][x + (y * width)];
        return array[x][y];
    }

    public int clampX(int x){
        return Mathf.clamp(x, 0, width - 1);
    }

    public int clampY(int y){
        return Mathf.clamp(y, 0, height - 1);
    }

    public void set(int x, int y, boolean b){
        if(multiArray){
            array[x][y] = b;
        }else{
            array[0][x + (y * width)] = b;
        }
    }
}
