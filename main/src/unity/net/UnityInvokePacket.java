package unity.net;

import arc.util.pooling.Pools;
import mindustry.net.Packets.*;

/** Just to differentiate the identity */
public class UnityInvokePacket extends InvokePacket{
    public static UnityInvokePacket create(){
        return Pools.obtain(UnityInvokePacket.class, UnityInvokePacket::new);
    }
}
