package unity.net;

import arc.func.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;

import java.io.*;

public class UnityRemoteReadServer{
    private static final ReusableByteInStream out = new ReusableByteInStream();
    private static final Reads read = new Reads(new DataInputStream(out));

    private static final IntMap<Cons<Player>> map = new IntMap<>();

    public static void registerHandlers(){
        map.put(2, player -> {
            UnityCall.tapAbility(player, read.i());
        });
    }

    public static void readPacket(byte[] bytes, byte type, Player player){
        out.setBytes(bytes);
        if(!map.containsKey(type)){
            throw new RuntimeException("Unknown packet type: '" + type + "'");
        }else{
            try{
                map.get(type).get(player);
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
