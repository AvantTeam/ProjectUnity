package unity.world.blocks.exp;

import arc.*;
import arc.func.*;
import arc.util.*;
import mindustry.world.meta.*;

public abstract class EField<T> {
    public @Nullable
    Stat stat;
    public EField(Stat stat){
        this.stat = stat;
    }

    public abstract T fromLevel(int l);
    public abstract void setLevel(int l);

    @Override
    public String toString(){
        return "[#84ff00]NULL[]";
    }

    public static class ELinear extends EField<Float> {
        public Floatc set;
        public float start, scale;
        public Func<Float, String> format;

        public ELinear(Floatc set, float start, float scale, Stat stat, Func<Float, String> format){
            super(stat);
            this.start = start;
            this.scale = scale;
            this.set = set;
            this.format = format;
        }

        public ELinear(Floatc set, float start, float scale, Stat stat){
            this(set, start, scale, stat, f -> Strings.autoFixed(f, 1));
        }

        @Override
        public Float fromLevel(int l){
            return start + l * scale;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            //return Strings.autoFixed(start, 1) + " + " + "[#84ff00]" + Strings.autoFixed(scale, 1) + " per level[]";
            return Core.bundle.format("field.linear", format.get(start), format.get(scale));
        }
    }

    public static class ELinearCap extends ELinear {
        public int cap; //after this level, the stats do not rise

        public ELinearCap(Floatc set, float start, float scale, int cap, Stat stat, Func<Float, String> format){
            super(set, start, scale, stat, format);
            this.cap = cap;
        }

        public ELinearCap(Floatc set, float start, float scale, int cap, Stat stat){
            this(set, start, scale, cap, stat, f -> Strings.autoFixed(f, 1));
        }

        @Override
        public Float fromLevel(int l){
            return start + Math.min(l, cap) * scale;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            //return Strings.autoFixed(start, 1) + " + " + "[#84ff00]" + Strings.autoFixed(scale, 1) + " per level[]";
            return Core.bundle.format("field.linearcap", format.get(start), format.get(scale), cap);
        }
    }

    public static class EBool extends EField<Boolean> {
        public Boolc set;
        public boolean start;
        public int thresh;

        public EBool(Boolc set, boolean start, int thresh, Stat stat){
            super(stat);
            this.start = start;
            this.thresh = thresh;
            this.set = set;
        }

        @Override
        public Boolean fromLevel(int l){
            return (l >= thresh) != start;
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            return Core.bundle.format("field.bool", bs(start), bs(!start), thresh);
        }

        public String bs(boolean b){
            return Core.bundle.get(b ? "yes" : "no");
        }
    }

    public static class EList<T> extends EField<T> {
        public Cons<T> set;
        public T[] list;
        public String unit;

        public EList(Cons<T> set, T[] list, Stat stat, String unit){
            super(stat);
            this.set = set;
            this.list = list;
            this.unit = unit;
        }

        public EList(Cons<T> set, T[] list, Stat stat){
            this(set, list, stat, "");
        }

        @Override
        public T fromLevel(int l){
            return list[Math.min(list.length - 1, l)];
        }

        @Override
        public void setLevel(int l){
            set.get(fromLevel(l));
        }

        @Override
        public String toString(){
            return Core.bundle.format("field.list", list[0], list[list.length - 1], unit);
        }
    }
}

