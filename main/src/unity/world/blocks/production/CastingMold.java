package unity.world.blocks.production;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.meta.*;

public class CastingMold extends GraphBlock{
    final TextureRegion[] baseRegions = new TextureRegion[4], topRegions = new TextureRegion[4];

    public CastingMold(String name){
        super(name);
        
        rotate = solid = hasItems = true;
        itemCapacity = 1;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++){
            baseRegions[i] = Core.atlas.find(name + "-base" + (i + 1));
            topRegions[i] = Core.atlas.find(name + "-top" + (i + 1));
        }
    }

    public class CastingMoldBuild extends GraphBuild{
        final static String tooHot = "Too hot to cast!";
        final OrderedSet<Building> outputBuildings = new OrderedSet<>(8);
        MeltInfo castingMelt;
        
        float pourProgress, castProgress, castSpeed;

        @Override
        public void proxUpdate(){
            updateOutput();
        }

        @Override
        public void onRotationChanged(){
            updateOutput();
        }

        @Override
        public void displayExt(Table table){
            table.row();
            table.table(sub -> {
                sub.clearChildren();
                sub.left();
                if(castingMelt != null){
                    sub.image(castingMelt.item.icon(Cicon.medium));
                    sub.label(() -> {
                        if(pourProgress == 1f && castSpeed == 0f) return tooHot;
                        return Strings.fixed((pourProgress + castProgress) * 50f, 2) + "%";
                    }).color(Color.lightGray);
                }else{
                    sub.labelWrap("Nothing being casted").color(Color.lightGray);
                }
            }).left();
        }

        void updateOutput(){
            outputBuildings.clear();
            for(int i = 0; i < 8; i++){
                var pos = gms.getConnectSidePos(i);
                var b = nearby(pos.toPos.x, pos.toPos.y);
                
                if(b != null){
                    if(b instanceof GraphBuildBase g && g.crucible() != null) continue;
                    outputBuildings.add(b);
                }
            }
        }

        @Override
        public void updatePost(){
            if(items.total() > 0){
                pourProgress = 0f;
                castProgress = 0f;
                
                if(timer(timerDump, dumpTime)){
                    Item itemPass = items.first();
                    
                    for(var i : outputBuildings){
                        if(i.team == team && i.acceptItem(this, itemPass)){
                            i.handleItem(this, itemPass);
                            items.remove(itemPass, 1);
                            
                            return;
                        }
                    }
                }
                return;
            }
            var dex = crucible();
            
            if(castingMelt == null){
                pourProgress = 0f;
                castProgress = 0f;
                var cc = dex.getContained();
                var melts = MeltInfo.all;
                                
                if(cc.isEmpty()) return;
                CrucibleData hpMelt = null;
                MeltInfo hpMeltType = null;
                
                for(var i : cc){
                    var meltType = melts[i.id];
                    if(i.meltedRatio * i.volume > 1f && (hpMelt == null || meltType.priority > hpMeltType.priority) && meltType.item != null){
                        hpMelt = i;
                        hpMeltType = meltType;
                    }
                }
                if(hpMelt != null){
                    dex.getNetwork().addLiquidToSlot(hpMelt, -1f);
                    castingMelt = hpMeltType;
                }
            }else{
                if(pourProgress < 1f){
                    pourProgress += edelta() * 0.05f;
                    if(pourProgress > 1f) pourProgress = 1f;
                }else if(castProgress < 1f){
                    castSpeed = Math.max(0f, (1f - (heat().getTemp() - 75f) / castingMelt.meltPoint) * castingMelt.meltSpeed * 1.5f);
                    castProgress += castSpeed;
                    
                    if(castProgress > 1f) castProgress = 1f;
                }else{
                    items.add(castingMelt.item, 1);
                    castingMelt = null;
                }
            }
        }

        @Override
        public void draw(){
            Draw.rect(baseRegions[rotation], x, y);
            if(castingMelt != null){
                if(pourProgress > 0f){
                    Draw.color(castingMelt.item.color, 1f - Math.abs(pourProgress - 0.5f) * 2f);
                    Draw.rect(liquidRegion, x, y, rotdeg());
                    Draw.color();
                    Draw.rect(castingMelt.item.icon(Cicon.medium), x, y, pourProgress * 8f, pourProgress * 8f);
                }
                if(castProgress < 1f && pourProgress > 0f){
                    UnityDrawf.drawHeat(castingMelt.item.icon(Cicon.medium), x, y, 0f, Mathf.map(castProgress, 0f, 1f, castingMelt.meltPoint, 275f));
                }
            }
            Draw.rect(topRegions[rotation], x, y);
            drawTeamTop();
        }

        @Override
        public void writeExt(Writes write){
            write.i(castingMelt != null ? castingMelt.id : -1);
            write.f(pourProgress);
            write.f(castProgress);
        }

        @Override
        public void readExt(Reads read, byte revision){
            castingMelt = MeltInfo.all[read.i()];
            pourProgress = read.f();
            castProgress = read.f();
        }
    }
}
