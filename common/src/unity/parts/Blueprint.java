package unity.parts;

import arc.struct.*;
import mindustry.type.*;
import unity.parts.PanelDoodadType.*;
import unity.parts.PartType.*;

//TODO is two Generics inevitable?
//Assumes fields especially collections are immutable from outer direct access.
public abstract class Blueprint<T extends PartType, P extends Part>{
    public P[][] parts;
    //valid parts only.
    public OrderedSet<P> partsList;
    public boolean[][] valid;
    public final int w, h;
    public P root;
    protected Runnable onChange = () -> {};
    boolean itemReqChanged;
    public ItemSeq itemRequirements;

    public OrderedSet<Data> data;

    public Blueprint(int w, int h){
        this.w = w;
        this.h = h;
        itemRequirements = new ItemSeq();
    }

    public void setOnChange(Runnable onChange){
        this.onChange = onChange;
    }

    public void clear(){
        root = null;
        onChange.run();
        itemReqChanged = true;
    }

    public abstract void decode(byte[] data);

    protected abstract void rebuildValid();

    protected void onChange(){
        rebuildValid();
        onChange.run();
        itemReqChanged = true;
    }

    public boolean canPlace(int x, int y){
        return x >= 0 && y >= 0 && x < w && y < h;
    }

    public abstract boolean canPlace(T type, int x, int y);

    public abstract boolean tryPlace(T type, int x, int y);

    protected abstract void place(T part, int x, int y);

    public abstract void displace(int x, int y);

    public abstract ItemSeq itemRequirements();

    //Eliminate invalid parts.
    protected abstract Blueprint validate();

    public abstract Construct construct();

    public abstract byte[] encode();

    //trims empty tiles.
    public abstract byte[] encodeCropped();

    //unsigned
    public static int ub(byte a){
        return (int)(a) + 128;
    }

    //signed
    public static byte sb(int a){
        return (byte)(a - 128);
    }

    public static abstract class Data{
        public abstract void pack(byte[] data, int offset);

        public <D extends Data> D self(){
            return (D)this;
        }
    }

    public static abstract class Construct<P extends Part>{
        public final P[][] parts;
        public final Seq<P> partsList;
        public final Seq<P> hasCustomDraw = new Seq<>();
        public final Seq<PanelDoodad> doodads = new Seq<>();

        public Construct(P[][] parts, Seq<P> partsList){
            this.parts = parts;
            this.partsList = partsList;
        }

        public abstract byte[] toData();

        //Initialize draw config of ModularUnit.
        public abstract void initDoodads();
    }
}
