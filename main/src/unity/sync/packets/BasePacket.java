package unity.sync.packets;

import arc.util.io.*;
import mindustry.net.*;

public abstract class BasePacket extends Packet{
    private byte[] data = NODATA;

    @Override
    public void read(Reads read, int length){
        data = read.b(length);
    }

    @Override
    public void handled(){
        BAIS.setBytes(data);
        readFields();
    }

    public void readFields(){}
}
