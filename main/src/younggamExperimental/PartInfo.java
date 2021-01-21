package younggamExperimental;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.type.*;
import unity.util.*;

public class PartInfo{
    public final String name, desc;
    public final PartType category;
    public final int tx, ty, tw, th;
    public final boolean cannotPlace, isRoot;
    public final Point2 prePlace;
    public final ItemStack[] cost;
    public final byte[] connectOut, connectIn;
    public final OrderedMap<PartStatType, PartStat> stats = new OrderedMap<>(12);
    public TextureRegion sprite, sprite2, texRegion;
    final Seq<ConnectData> connInList = new Seq<>(), connOutList = new Seq<>();
    int id;

    public PartInfo(String name, String desc, PartType category, int tx, int ty, int tw, int th, boolean cannotPlace, boolean isRoot, Point2 prePlace, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats){
        this.name = name;
        this.desc = desc;
        this.category = category;
        this.tx = tx;
        this.ty = ty;
        this.tw = tw;
        this.th = th;
        this.cannotPlace = cannotPlace;
        this.isRoot = isRoot;
        this.prePlace = prePlace;
        this.cost = cost;
        this.connectOut = connectOut;
        this.connectIn = connectIn;
        for(var i : stats) this.stats.put(i.category, i);
    }

    public PartInfo(String name, String desc, PartType category, int tx, int ty, int tw, int th, ItemStack[] cost, byte[] connectOut, byte[] connectIn, PartStat... stats){
        this(name, desc, category, tx, ty, tw, th, false, false, null, cost, connectOut, connectIn, stats);
    }

    public static void preCalcConnection(PartInfo[] partsConfig){
        for(int i = 0, len = partsConfig.length; i < len; i++){
            int id = i;
            var pInfo = partsConfig[i];
            if(pInfo.connInList.isEmpty()){
                for(int j = 0, iLen = pInfo.connectIn.length; j < iLen; j++){
                    if(pInfo.connectIn[j] != 0) pInfo.connInList.add(ConnectData.getConnectSidePos(j, pInfo.tw, pInfo.th).id(pInfo.connectIn[j]));
                }
            }
            if(pInfo.connOutList.isEmpty()){
                for(int j = 0, iLen = pInfo.connectOut.length; j < iLen; j++){
                    if(pInfo.connectOut[j] != 0) pInfo.connOutList.add(ConnectData.getConnectSidePos(j, pInfo.tw, pInfo.th).id(pInfo.connectOut[j]));
                }
            }
        }
    }

    public static void assignPartSprites(PartInfo[] partsConfig, TextureRegion partsSprite, int spriteW, int spriteH){
        for(int i = 0, len = partsConfig.length; i < len; i++){
            var pinfo = partsConfig[i];
            pinfo.id = i;
            pinfo.texRegion = Funcs.getRegionRect(partsSprite, pinfo.tx, pinfo.ty, pinfo.tw, pinfo.th, spriteW, spriteH);
        }
    }
}
