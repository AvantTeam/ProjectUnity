package younggamExperimental;

import arc.struct.*;

public class IntPacker{
    public final IntSeq packed = new IntSeq(), raw = new IntSeq();
    int prev = -1, count, packIndex = -1;
    boolean highi;

    public void add(int bytef){
        if(bytef != prev){
            if(prev != -1){
                if(!highi){
                    packed.add(0);
                    packIndex++;
                }
                raw.add(count);
                raw.add(prev);
                int comb = prev + count * 256;
                packed.incr(packIndex, comb << (highi ? 16 : 0));
                highi = !highi;
            }
            count = 1;
            prev = bytef;
        }else count++;
    }

    public IntSeq end(){
        if(prev != -1){
            if(!highi){
                packed.add(0);
                packIndex++;
            }
            raw.add(count);
            raw.add(prev);
            int comb = prev + count * 256;
            packed.incr(packIndex, comb << (highi ? 16 : 0));
            highi = !highi;
        }
        return packed;
    }

    public String toStringPack(){
        var str = new StringBuilder("");
        for(int i = 0, len = raw.size; i < len; i++) str.append((char)raw.get(i));
        return str.toString();
    }

    public static IntPacker packArray(IntSeq a){
        var packer = new IntPacker();
        for(int i = 0, len = a.size; i < len; i++) packer.add(a.get(i));
        packer.end();
        return packer;
    }
}

