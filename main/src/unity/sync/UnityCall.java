package unity.sync;

import mindustry.entities.*;
import mindustry.gen.*;
import unity.*;
import unity.gen.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class UnityCall{
    public static void init(){

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
}
