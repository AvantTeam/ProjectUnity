package unity.net;

import arc.util.io.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.net.Net.*;
import unity.*;
import unity.ai.KamiAI.*;

import java.io.*;

import static mindustry.Vars.*;

public class UnityCall{
    private static ReusableByteOutStream out = new ReusableByteOutStream(8192);
    private static Writes write = new Writes(new DataOutputStream(out));

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
            UnityInvokePacket packet = UnityInvokePacket.create();
            packet.priority = 0;
            packet.type = 0;

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

            packet.bytes = out.getBytes();
            packet.length = out.size();

            net.send(packet, SendMode.udp);
        }
    }

    public static void bossMusic(Healthc boss, String name){
        if(net.server() || !net.active()){
            Unity.musicHandler.play(name, () -> !boss.dead() && boss.isAdded());
        }

        if(net.server()){
            UnityInvokePacket packet = UnityInvokePacket.create();
            packet.priority = 0;
            packet.type = 1;

            out.reset();

            TypeIO.writeEntity(write, boss);
            write.str(name);

            packet.bytes = out.getBytes();
            packet.length = out.size();

            net.send(packet, SendMode.tcp);
        }
    }
}
