package unity.net;

import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.io.*;
import unity.*;
import unity.ai.KamiAI.*;

public class UnityRemoteReadClient{
    public static void readPacket(Reads read, byte id){
        if(id == 0){
            try{
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
            }catch(Throwable t){
                Log.err(t);
            }
        }else if(id == 1){
            try{
                Healthc boss = TypeIO.readEntity(read);
                String name = read.str();

                Unity.musicHandler.play(name, () -> !boss.dead() && boss.isAdded());
            }catch(Throwable t){
                Log.err(t);
            }
        }else{
            Log.err("Invalid client remote read method id: " + id);
        }
    }
}
