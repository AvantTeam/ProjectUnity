package unity.sync;

import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.io.*;
import mindustry.net.*;
import unity.*;
import unity.ai.kami.*;
import unity.gen.*;

import java.io.*;
import java.nio.charset.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class UnityCall{
    private static final ReusableByteOutStream out = new ReusableByteOutStream();
    private static final Writes write = new Writes(new DataOutputStream(out));

    private static final ReusableByteOutStream outSend = new ReusableByteOutStream();
    private static final Writes writeSend = new Writes(new DataOutputStream(outSend));

    public static void init(){

    }

    public static void createKamiBullet(
        Teamc owner, BulletType type,
        float x, float y, float angle,
        float velocityScl, float lifetimeScl, float hitSize,
        int data, float fdata, float time
    ){

    }

    public static void tap(Player player, float x, float y){
        if(net.server() || !net.active()){
            Unity.tap.tap(player, x, y);
        }
    }

    public static void effect(Effect effect, float x, float y, float rotation, Object data){
        if(net.server() || !net.active()){
            effect.at(x, y, rotation, data);
        }
    }

    public static void soulJoin(MonolithSoul soul, Entityc ent){
        if(net.server() || !net.active()){
            MonolithSoul.soulJoin(soul, ent);
        }
    }

    protected static void client(NetConnection except, boolean reliable, int type){
        if(net.server()){
            /*outSend.reset();

            InvokePacket packet = Pools.obtain(InvokePacket.class, InvokePacket::new);
            packet.priority = 0;
            packet.type = 74;

            byte[] bytes = out.getBytes();
            TypeIO.writeString(writeSend, "unity.call");
            TypeIO.writeString(writeSend, type + ":" + new String(bytes, 0, bytes.length, StandardCharsets.UTF_16));

            packet.bytes = outSend.getBytes();
            packet.length = outSend.size();

            if(except != null){
                net.sendExcept(except, packet, reliable ? SendMode.tcp : SendMode.udp);
            }else{
                net.send(packet, reliable ? SendMode.tcp : SendMode.udp);
            }*/
        }
    }

    protected static void server(boolean reliable, int type){
        if(net.client()){
            /*outSend.reset();

            InvokePacket packet = Pools.obtain(InvokePacket.class, InvokePacket::new);
            packet.priority = 0;
            packet.type = 32;

            byte[] bytes = out.getBytes();
            TypeIO.writeString(writeSend, "unity.call");
            TypeIO.writeString(writeSend, type + ":" + new String(bytes, 0, bytes.length, StandardCharsets.UTF_16));

            packet.bytes = outSend.getBytes();
            packet.length = outSend.size();

            net.send(packet, reliable ? SendMode.tcp : SendMode.udp);*/
        }
    }
}
