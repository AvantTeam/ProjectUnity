package unity.net;

import arc.util.*;
import arc.util.io.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.io.*;
import unity.*;
import unity.ai.KamiAI.*;

import java.io.*;
import java.util.*;

import static mindustry.Vars.*;

public class UnityCall{
    private static final ReusableByteOutStream out = new ReusableByteOutStream();
    private static final Writes write = new Writes(new DataOutputStream(out));

    public static void init(){
        if(netClient != null){
            UnityRemoteReadClient.registerHandlers();
            netClient.addPacketHandler("unity.call", handler -> {
                String[] split = handler.split(":");
                UnityRemoteReadClient.readPacket(unpack(split[1]), Byte.parseByte(split[0]));
            });
        }else{
            Log.warn("'netClient' is null");
        }

        if(netServer != null){
            UnityRemoteReadServer.registerHandlers();
            netServer.addPacketHandler("unity.call", (player, handler) -> {
                String[] split = handler.split(":");
                UnityRemoteReadServer.readPacket(unpack(split[1]), Byte.parseByte(split[0]), player);
            });
        }else{
            Log.warn("'netServer' is null");
        }
    }

    protected static String pack(byte[] bytes){
        String arr = Arrays.toString(bytes).replace(" ", "");
        return arr.substring(1, arr.length() - 1);
    }

    protected static byte[] unpack(String str){
        String[] split = str.split(",");

        byte[] bytes = new byte[split.length];
        for(int i = 0; i < split.length; i++){
            bytes[i] = Byte.parseByte(split[i]);
        }

        return bytes;
    }

    public static void createKamiBullet(
        Teamc owner, BulletType type,
        float x, float y, float angle,
        float velocityScl, float lifetimeScl, float hitSize,
        int data, float fdata, float time
    ){
        if(net.server() || !net.active()){
            Bullet b = type.create(owner, owner.team(), x, y, angle);
            b.vel.scl(velocityScl);
            b.lifetime = type.lifetime * lifetimeScl;
            b.hitSize = hitSize < 0f ? type.hitSize : hitSize;
            b.data = KamiBulletDatas.get(data);
            b.fdata = fdata;
            b.time = time;
        }

        if(net.server()){
            out.reset();

            TypeIO.writeBulletType(write, type);
            TypeIO.writeEntity(write, owner);
            TypeIO.writeTeam(write, owner.team());
            write.f(x);
            write.f(y);
            write.f(angle);
            write.f(velocityScl);
            write.f(lifetimeScl);
            write.f(hitSize);
            write.i(data);
            write.f(fdata);
            write.f(time);

            Call.clientPacketReliable("unity.call", "0:" + pack(out.getBytes()));
        }
    }

    public static void bossMusic(String name, boolean play){
        if(net.server() || !net.active()){
            if(play){
                Unity.musicHandler.play(name);
            }else{
                Unity.musicHandler.stop(name);
            }
        }

        if(net.server()){
            out.reset();

            write.str(name);
            write.bool(play);

            Call.clientPacketReliable("unity.call", "1:" + pack(out.getBytes()));
        }
    }
}
