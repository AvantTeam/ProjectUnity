package unity.util;

import java.util.*;

public class LongGrid{
    public long[] items;
    public int width, height;

    public LongGrid(){
        this(0, 0);
    }

    public LongGrid(int width, int height){
        resize(width, height);
    }

    public void resize(int width, int height){
        this.width = width;
        this.height = height;
        items = new long[width * height];
    }

    public long get(int x, int y){
        return items[x + y * width];
    }

    public void set(int x, int y, long value){
        items[x + y * width] = value;
    }

    public void clear(){
        fill(0);
    }

    public void fill(long value){
        Arrays.fill(items, value);
    }

    public boolean within(int x, int y){
        return x >= 0 && y >= 0 && x < width && y < height;
    }
}
