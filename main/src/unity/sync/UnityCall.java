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
        if(netClient != null){
            UnityRemoteReadClient.registerHandlers();
            netClient.addPacketHandler("unity.call", handler -> {
                int i = handler.indexOf(":");
                UnityRemoteReadClient.readPacket(handler.substring(i + 1).getBytes(StandardCharsets.UTF_16), Byte.parseByte(handler.substring(0, i)));
            });
        }else{
            Log.warn("'netClient' is null");
        }

        if(netServer != null){
            UnityRemoteReadServer.registerHandlers();
            netServer.addPacketHandler("unity.call", (player, handler) -> {
                int i = handler.indexOf(":");
                UnityRemoteReadServer.readPacket(handler.substring(i + 1).getBytes(StandardCharsets.UTF_16), Byte.parseByte(handler.substring(0, i)), player);
            });
        }else{
            Log.warn("'netServer' is null");
        }
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
            b.data = KamiBulletDatas.get(b, data);
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

            client(null, false, 0);
        }
    }

    public static void bossMusic(String name, boolean play){
        if(net.server() || !net.active()){
            if(play){
                Unity.music.play(name);
            }else{
                Unity.music.stop(name);
            }
        }

        if(net.server()){
            out.reset();

            write.str(name);
            write.bool(play);

            client(null, true, 1);
        }
    }

    public static void tap(Player player, float x, float y){
        if(net.server() || !net.active()){
            Unity.tap.tap(player, x, y);
        }

        if(net.server() || net.client()){
            out.reset();

            if(net.server()) TypeIO.writeEntity(write, player);
            write.f(x);
            write.f(y);

            if(net.client()){
                server(false, 2);
            }else if(net.server()){
                client(null, false, 2);
            }
        }
    }

    public static void effect(Effect effect, float x, float y, float rotation, Object data){
        if(net.server() || !net.active()){
            effect.at(x, y, rotation, data);
        }

        if(net.server()){
            out.reset();

            TypeIO.writeEffect(write, effect);
            write.f(x);
            write.f(y);
            write.f(rotation);

            try{
                TypeIO.writeObject(write, data);
            }catch(IllegalArgumentException no){
                if(data instanceof Position[] pos){
                    write.b(16);
                    write.b(pos.length);
                    for(Position p : pos){
                        write.f(p.getX());
                        write.f(p.getY());
                    }
                }else if(data instanceof Entityc e){
                    write.b(17);
                    TypeIO.writeEntity(write, e);
                }else{
                    throw new IllegalArgumentException("Unknown object type: " + data.getClass());
                }
            }

            client(null, true, 3);
        }
    }

    public static void soulJoin(MonolithSoul soul, Entityc ent){
        if(net.server() || !net.active()){
            MonolithSoul.soulJoin(soul, ent);
        }

        if(net.server()){
            out.reset();

            TypeIO.writeUnit(write, soul);
            if(ent instanceof Building b){
                write.b(0);
                TypeIO.writeBuilding(write, b);
            }else if(ent instanceof Syncc s){
                write.b(1);
                TypeIO.writeEntity(write, s);
            }else{
                throw new IllegalArgumentException("Invalid joined entity: " + ent.getClass());
            }

            client(null, true, 4);
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
