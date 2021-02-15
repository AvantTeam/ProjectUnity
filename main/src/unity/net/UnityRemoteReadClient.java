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

    private static final ObjectMap<String, Runnable> map = new ObjectMap<>();

    public static void registerHandlers(){
        map.put("createKamiBullet", () -> {
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

        map.put("bossMusic", () -> {
            Healthc boss = TypeIO.readEntity(read);
            String name = read.str();

            Unity.musicHandler.play(name, () -> !boss.dead() && boss.isAdded());
        });
    }

    public static void readPacket(byte[] bytes, String type){
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
