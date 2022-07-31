package unity.parts;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.gen.entities.*;
import unity.util.*;

public abstract class PartType implements Displayable{
    public static final float partSize = 4;
    private static int idAcc = 0;
    public final int id = idAcc++;
    public ItemStack[] cost = {};
    public int w = 1;
    public int h = 1;
    public boolean hasCustomDraw = false;
    //graphics
    public TextureRegion icon;
    public String name;
    //stats
    protected Seq<PartStat> stats = new Seq<>();
    /** if true will not have paneling **/
    public boolean open = false;

    public PartType(String name){this.name = name;}

    //stats.
    public void appendStats(PartStatMap statMap, Part part){
        for(var stat : stats){
            stat.merge(statMap, part);
        }
    }

    public void appendStatsPost(PartStatMap statMap, Part part){
        for(var stat : stats){
            stat.mergePost(statMap, part);
        }
    }

    public abstract void drawCell(DrawTransform transform, Part part);

    public abstract void drawTop(DrawTransform transform, Part part);

    public abstract void draw(DrawTransform transform, Part part, Modularc parent);

    public abstract void drawOutline(DrawTransform transform, Part part);

    public static abstract class Part{
        public PartType type;
        //which lighting variation to draw
        public int[] panelingIndexes;
        protected int x, y;
        //position of lowest left tile
        protected float ax, ay;
        //middle
        protected float cx, cy;
        int front = 0;

        public int x(){
            return x;
        }

        public int y(){
            return y;
        }

        public void x(int x){
            this.x = x;
        }

        public void y(int y){
            this.y = y;
        }

        public float ax(){
            return ax;
        }

        public float ay(){
            return ay;
        }

        public float cx(){
            return cx;
        }

        public float cy(){
            return cy;
        }
    }
}
