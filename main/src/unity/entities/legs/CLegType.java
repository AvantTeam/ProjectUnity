package unity.entities.legs;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;

public class CLegType<T extends CLeg>{
    public float targetX, targetY, x, y;

    public float legSplashDamage = 0f, legSplashRange = 5, legForwardScl = 1f;
    public boolean flipped = false;

    public Prov<T> prov;
    public Runnable loadPost;
    public String name;

    public TextureRegion footRegion;

    public CLegType(Prov<T> prov, String name){
        this.prov = prov;
        this.name = name;
    }

    public T create(){
        T t = prov.get();
        t.type = this;
        return t;
    }

    public float length(){
        return Mathf.dst(x, y, targetX, targetY);
    }

    public void load(){
        footRegion = Core.atlas.find(name + "-foot");
    }

    @SafeVarargs
    public static ClegGroupType createGroup(String name, Cons<ClegGroupType> cons, CLegType<? extends CLeg>... legs){
        ClegGroupType g = new ClegGroupType();
        g.name = name;
        g.legs = legs;
        cons.get(g);
        return g;
    }

    public static class ClegGroupType{
        public float moveSpacing = 1f, legSpeed = 0.1f, maxStretch = 1.7f;
        public float baseRotateSpeed = 5f;
        public int legGroupSize = 2;
        public CLegType<? extends CLeg>[] legs;
        public TextureRegion baseRegion;
        public String name;

        public CLegGroup create(){
            CLegGroup g = new CLegGroup();
            g.init(this);
            return g;
        }

        public void load(){
            baseRegion = Core.atlas.find(name + "-base");
            for(CLegType<? extends CLeg> leg : legs){
                leg.load();
                if(leg.loadPost != null) leg.loadPost.run();
            }
        }
    }
}
