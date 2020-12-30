package unity.world.blocks.distribution;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.meta.*;

import static arc.Core.atlas;
import static mindustry.Vars.content;

public class CruciblePump extends GraphBlock{
    static final float[] fillAm = new float[]{1f, 0.5f, 0.25f};
    final TextureRegion[] topRegions = new TextureRegion[4];//top
    TextureRegion bottomRegion;//bottom

    public CruciblePump(String name){
        super(name);
        rotate = solid = configurable = true;
        config(Item.class, (CruciblePumpBuild build, Item item) -> build.filterItem = item);
        config(Integer.class, (CruciblePumpBuild build, Integer value) -> {
            unity.Unity.print(value, value & 3, value >>> 2);
            build.pumpMode = value & 3;
            if(value > 2) build.filterItem = content.item(value >>> 2);
        });
        configClear((CruciblePumpBuild build) -> build.filterItem = null);
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) topRegions[i] = atlas.find(name + "-top" + (i + 1));
        bottomRegion = atlas.find(name + "-bottom");
    }

    public class CruciblePumpBuild extends GraphBuild{
        Item filterItem;
        float flowRate, flowAnimation;
        int pumpMode;

        @Override
        public void buildConfiguration(Table table){
            table.labelWrap("Fill until:").growX().pad(5f).center().row();
            table.table(bTable -> {
                bTable.button("Full", Styles.clearPartialt, () -> configure(0)).left().size(50f).disabled(b -> pumpMode == 0);
                bTable.button("50%", Styles.clearPartialt, () -> configure(1)).left().size(50f).disabled(b -> pumpMode == 1);
                bTable.button("25%", Styles.clearPartialt, () -> configure(2)).left().size(50f).disabled(b -> pumpMode == 2);
            }).row();
            table.labelWrap("Pump:").growX().pad(5f).center().row();
            ItemSelection.buildTable(table, content.items(), () -> filterItem, this::configure);
            table.setBackground(Styles.black5);
        }

        @Override
        public void displayExt(Table table){
            String ps = " " + StatUnit.perSecond.localized();
            table.row();
            table.table(sub -> {
                sub.clearChildren();
                sub.left();
                if(filterItem != null){
                    sub.image(filterItem.icon(Cicon.medium));
                    sub.label(() -> Strings.fixed(flowRate * 10f, 2) + "units" + ps).color(Color.lightGray);
                }else sub.labelWrap("No filter selected").color(Color.lightGray);
            }).left();
        }

        @Override
        public void updatePost(){
            float rate = 0.08f;
            var dex = crucible();
            flowRate /= 2f;
            if(filterItem != null){
                var fromNet = dex.getNetworkFromSet(1);
                var toNet = dex.getNetworkFromSet(0);
                if(fromNet != null && toNet != null){
                    for(var fnc : fromNet.contains()){
                        if(fnc.item != filterItem) continue;
                        float transfer = Math.min(toNet.getRemainingSpace(), Math.min(rate * edelta(), fnc.volume * fnc.meltedRatio));
                        var toG = toNet.getMeltFromID(fnc.id);
                        if(toG != null) transfer = Math.min(toNet.totalCapacity() * fillAm[pumpMode] - toG.volume, transfer);
                        if(transfer <= 0f) break;
                        fromNet.addLiquidToSlot(fnc, -transfer);
                        toNet.addMeltItem(MeltInfo.all[fnc.id], transfer, true);
                        flowRate = transfer;
                        break;
                    }
                }
            }
            flowAnimation += flowRate * 0.4f;
        }

        @Override
        public void draw(){
            Draw.rect(bottomRegion, x, y);
            if(filterItem != null){
                Draw.color(filterItem.color, Mathf.clamp(flowRate * 60f));
                UnityDrawf.drawSlideRect(liquidRegion, x, y, 16f, 16f, 32f, 16f, rotdeg() + 180f, 16, flowAnimation);
                Draw.color();
            }
            UnityDrawf.drawHeat(heatRegion, x, y, rotdeg(), heat().getTemp());
            Draw.rect(topRegions[rotation], x, y);
            drawTeamTop();
        }

        @Override
        public void writeExt(Writes write){
            write.s(filterItem == null ? -1 : filterItem.id);
            write.b(pumpMode);
        }

        @Override
        public void readExt(Reads read, byte revision){
            filterItem = content.item(read.s());
            pumpMode = read.b();
        }

        @Override
        public Integer config(){
            return pumpMode + (filterItem != null ? (filterItem.id + 1) << 2 : 0);
        }
    }
}
