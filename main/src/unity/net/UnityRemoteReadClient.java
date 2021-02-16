package unity.net;

import java.io.*;

import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.io.*;
import unity.*;
import unity.ai.KamiAI.*;

public class UnityRemoteReadClient{
    private static final ReusableByteInStream out = new ReusableByteInStream();
    private static final Reads read = new Reads(new DataInputStream(out));

    private static final IntMap<Runnable> map = new IntMap<>();

    public static void registerHandlers(){
        map.put(0, () -> {
            Bullet b = TypeIO.readBulletType(read).create(
                TypeIO.readEntity(read),
                TypeIO.readTeam(read),
                read.f(), read.f(), read.f()
            );
            b.vel.scl(read.f());
            b.lifetime = b.type.lifetime * read.f();

            float h = read.f();
            b.hitSize = h < 0f ? b.type.hitSize : h;
            b.data = KamiBulletDatas.get(read.i());
            b.fdata = read.f();
            b.time = read.f();
        });

        map.put(1, () -> {
            String name = read.str();
            boolean play = read.bool();

            if(play){
                Unity.musicHandler.play(name);
            }else{
                Unity.musicHandler.stop(name);
            }
        });
    }

    public static void readPacket(byte[] bytes, byte type){
        out.setBytes(bytes);
        if(!map.containsKey(type)){
            throw new RuntimeException("Unknown packet type: '" + type + "'");
        }else{
            try{
                map.get(type).run();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}
