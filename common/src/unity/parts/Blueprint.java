package unity.parts;

import arc.struct.*;
import mindustry.type.*;

public abstract class Blueprint<T>{
    public T[][] parts;
    public OrderedMap<T, Data> partsList;
    public boolean[][] valid;
    public int w, h;
    public T root;
    protected Runnable onChange = () -> {};
    boolean itemReqChanged;
    public ItemSeq itemRequirements;

    public Data[] data;

    protected Blueprint(){}

    public Blueprint(byte[] data){
        decode(data);
    }

    public abstract void decode(byte[] data);

    protected abstract void setUpRoot();

    protected abstract void validate();

    public abstract ItemSeq itemRequirements();

    public abstract byte[] encode();

    public abstract byte[] encodeCropped();

    //unsigned
    public static byte ub(byte a){
        return (byte)(a + 128);
    }

    //signed
    public static byte sb(byte a){
        return (byte)(a - 128);
    }

    public static abstract class Data{
        public abstract void pack(byte[] data, int offset);

        public <D extends Data> D self(){
            return (D)this;
        }

        public abstract <D extends Data> D copy();
    }
}
