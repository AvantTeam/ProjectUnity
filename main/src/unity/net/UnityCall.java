package unity.net;

import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.io.*;
import unity.*;
import unity.ai.KamiAI.*;

import java.io.*;
import java.nio.charset.*;
import java.util.regex.*;

import static mindustry.Vars.*;

public class UnityCall{
    private static final ReusableByteOutStream out = new ReusableByteOutStream(8192);
    private static final Writes write = new Writes(new DataOutputStream(out));

    private static final byte[] hexArray = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    private static final Pattern hexPattern = Pattern.compile("..");
    private static final ByteSeq hexSeq = new ByteSeq();

    public static void init(){
        if(netClient != null){
            UnityRemoteReadClient.registerHandlers();
            netClient.addPacketHandler("unity.call", handler -> {
                String[] split = handler.split("::");
                UnityRemoteReadClient.readPacket(unpackBytes(split[1]), split[0]);
            });
        }else{
            Log.warn("'netClient' is null");
        }

        if(netServer != null){
            UnityRemoteReadServer.registerHandlers();
            netServer.addPacketHandler("unity.call", (player, handler) -> {
                String[] split = handler.split("::");
                UnityRemoteReadServer.readPacket(unpackBytes(split[1]), split[0], player);
            });
        }else{
            Log.warn("'netServer' is null");
        }
    }

    protected static String packBytes(byte[] bytes){
        byte[] hexChars = new byte[bytes.length * 2];
        for(int j = 0; j < bytes.length; j++){
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars, StandardCharsets.UTF_8);
    }

    protected static byte[] unpackBytes(String str){
        Matcher matcher = hexPattern.matcher(str);
        hexSeq.clear();
        while(matcher.find()){
            hexSeq.add((byte)Integer.parseInt(matcher.group(), 16));
        }

        return hexSeq.toArray();
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

            Call.clientPacketReliable("unity.call", "createKamiBullet::" + packBytes(out.getBytes()));
        }
    }

    public static void bossMusic(Healthc boss, String name){
        if(net.server() || !net.active()){
            Unity.musicHandler.play(name, () -> !boss.dead() && boss.isAdded());
        }

        if(net.server()){
            out.reset();

            TypeIO.writeEntity(write, boss);
            write.str(name);

            Call.clientPacketReliable("unity.call", "bossMusic::" + packBytes(out.getBytes()));
        }
    }
}
