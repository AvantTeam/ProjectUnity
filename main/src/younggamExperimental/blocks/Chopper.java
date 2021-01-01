package younggamExperimental.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.meta.*;
import unity.util.*;
import unity.world.blocks.*;
import younggamExperimental.*;

import static arc.Core.atlas;

public class Chopper extends GraphBlock{
    static final PartInfo[] partInfo = new PartInfo[5];
    final PartType[] categories = new PartType[]{PartType.blade, PartType.saw};//categorySprite
    final TextureRegion[] baseRegions = new TextureRegion[4];//base
    TextureRegion topRegion, partsRegion;//topsprite,partsAtlas
    float partCostAccum = 0.2f, autoBuildDelay = 10;
    int gridW = 1, gridH = 1,
        spriteGridSize = 32, spriteGridPadding,
        tx, ty;
    private int index;

    public Chopper(String name){
        super(name);
        rotate = solid = configurable = acceptsItems = true;
        config(String.class, (ChopperBuild build, String value) -> build.changed = build.setBluePrintFromString(value));
        config(IntSeq.class, (ChopperBuild build, IntSeq value) -> build.changed = build.setBluePrint(Funcs.unpackInts(value)));
        configClear((ChopperBuild build) -> build.setBluePrint(null));
    }

    protected void setGridW(int s){
        gridW = Math.min(16, s);
    }

    protected void setGridH(int s){
        gridH = Math.min(16, s);
    }

    PartType[] getPartsCategories(){
        return categories;
    }

    @Override
    public void load(){
        topRegion = atlas.find(name + "-top");
        for(int i = 0; i < 4; i++) baseRegions[i] = atlas.find(name + "-base" + (i + 1));
        partsRegion = atlas.find(name + "-parts");
        //I guess this class should be only one.
        partInfo[0].sprite = partInfo[3].sprite = atlas.find(name + "-rod");
        partInfo[1].sprite = atlas.find(name + "-blade1");
        partInfo[1].sprite2 = atlas.find(name + "-blade2");
        partInfo[2].sprite = atlas.find(name + "-sblade");
        for(int i = 0; i < 2; i++) categories[i].region = atlas.find(name + "-category" + (i + 1));
        //auto headless
        tx = spriteGridPadding * 2 + gridW * spriteGridSize;
        ty = spriteGridPadding * 2 + gridH * spriteGridSize;
    }

    protected void addPart(String name, String desc, PartType category, int tx, int ty, int tw, int th, boolean cannotPlace, boolean isRoot, Point2 prePlace, ItemStack[] cost, byte[] connectOut, byte[] connectIn){
        //TODO
        partInfo[index++] = new PartInfo(name, desc, category, tx, ty, tw, th, cannotPlace, isRoot, prePlace, cost, connectOut, connectIn);
    }

    protected void addPart(String name, String desc, PartType category, int tx, int ty, int tw, int th, ItemStack[] cost, byte[] connectOut, byte[] connectIn){
        addPart(name, desc, category, tx, ty, tw, th, false, false, null, cost, connectOut, connectIn);
    }

    public class ChopperBuild extends GraphBuild{
        final OrderedMap<Item, Integer> blueprintRemainingCost = new OrderedMap<>(12);
        final IntSeq bluePrint = new IntSeq();
        final Seq<StatContainer.Segment> hitSegments = new Seq<>();
        final FrameBuffer buffer = new FrameBuffer(tx, ty);
        final StatContainer currentStats = new StatContainer();
        final Rect detectRect = new Rect();
        float totalItemCountCost, totalItemCountPaid,
            originalMaxHp;
        int aniProg, aniTime, aniSpeed,
            speedDmgMul,
            knockbackTorque, inertia,
            bladeRadius;
        boolean changed;

        float getPaidRatio(){
            if(totalItemCountCost == 0f) return 0f;
            return totalItemCountPaid / totalItemCountCost;
        }

        boolean setBluePrintFromString(String s){
            return setBluePrint(Funcs.unpackIntsFromString(s));
        }

        boolean setBluePrint(IntSeq s){
            if(!bluePrint.equals(s)){
                bluePrint.clear();
                if(s != null) bluePrint.addAll(s);
                return true;
            }
            return false;
        }

        TextureRegion getBufferRegion(){
            var tex = Draw.wrap(buffer.getTexture());
            tex.v = tex.v2;
            tex.v2 = tex.u;
            return tex;
        }

        @Override
        public void displayExt(Table table){
            String ps = " " + StatUnit.perSecond.localized();
            table.row();
            table.table().left().update(sub -> {
                sub.clearChildren();
                if(totalItemCountPaid == totalItemCountCost) return;
                sub.left();
                if(blueprintRemainingCost.isEmpty()) sub.labelWrap("No blueprint").color(Color.lightGray);
                else{//idk
                    for(var i : blueprintRemainingCost){
                        sub.image(i.key.icon(Cicon.medium));
                        sub.add((i.value >>> 16) + "/" + (i.value & 65536));
                        sub.row();
                    }
                }
            });
        }

        void updateAutoBuild(){
            if(totalItemCountPaid >= totalItemCountCost) return;
            if(Vars.state.rules.infiniteResources || team.rules().infiniteResources || team.rules().cheat){
                for(var i : blueprintRemainingCost){
                    int temp = i.value & 65536;
                    i.value <<= 16;
                    i.value += temp;
                }
                totalItemCountPaid = totalItemCountCost;
                applyStats();
                return;
            }
            if(timer(dumpTime, autoBuildDelay)){
                var core = team.core();
                if(team.core() == null) return;
                var cItems = team.core().items;
                for(var i : blueprintRemainingCost){
                    if(i.value >>> 16 < (i.value & 65536) && cItems.get(i.key) > 0){
                        cItems.remove(i.key, 1);
                        totalItemCountPaid++;
                        i.value += 1 << 16;
                        if(totalItemCountPaid == totalItemCountCost) applyStats();
                        return;
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            int value = blueprintRemainingCost.get(item, 0);
            boolean hasSpace = value >>> 16 < (value & 65536);
            return super.acceptItem(source, item) || hasSpace;//acceptItemExt
        }

        @Override
        public void handleItem(Building source, Item item){
            if(totalItemCountPaid == totalItemCountCost) return;//handleItemExt
            totalItemCountPaid++;
            int value = blueprintRemainingCost.get(item, 0);
            blueprintRemainingCost.put(item, value + (1 << 16));
            if(totalItemCountPaid == totalItemCountCost) applyStats();
        }

        //TODO 굳이?
        void resetStats(){
            inertia = 5;
            //TODO
            if(originalMaxHp > 0f) maxHealth = originalMaxHp;
        }

        void applyStats(){
            inertia = 5 + currentStats.inertia();
            originalMaxHp = maxHealth;
            maxHealth = originalMaxHp + currentStats.hpinc();
            heal(currentStats.hpinc() * health / originalMaxHp);
            hitSegments.set(currentStats.segments);
            int r = 0;
            for(int i = 0, len = hitSegments.size; i < len; i++) r = Math.max(r, hitSegments.get(i).end * 8);
            detectRect.setCentered(x, y, r * 2f);
            bladeRadius = r;
        }

        void accumStats(PartInfo part, int x, int y, int[][] grid){
            //TODO
        }

        void drawPartBuffer(PartInfo part, int x, int y, int[][] grid){
            Draw.rect(part.sprite, (x + part.tw * 0.5f) * 32f, (y + part.th * 0.5f) * 32f, part.tw * 32f, part.th * 32f);
            if(part.sprite2 != null && grid[x + 1][y] == 0) Draw.rect(part.sprite2, (x + part.tw * 0.5f + 1f) * 32f, (y + part.th * 0.5f) * 32f, part.tw * 32f, part.th * 32f);
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Tex.whiteui, Styles.clearTransi, 50f, () -> {
                var dialog = new BaseDialog("Edit Blueprint");
                dialog.setFillParent(false);
                var mtd = ModularConstructorUI.applyModularConstructorUI(dialog.cont, partsRegion, Math.round(partsRegion.width / 32f), Math.round(partsRegion.height / 32f),
                    partInfo, gridW, gridH, bluePrint, getPartsCategories(), partCostAccum
                );
                dialog.buttons.button("@ok", () -> {
                    configure(mtd.getPackedSave());
                    dialog.hide();
                }).size(130f, 60f);
                dialog.update(() -> {
                    if(!(tile.build instanceof ChopperBuild)) dialog.hide();
                });
                dialog.show();
            }).size(50f).get().getStyle().imageUp = Icon.pencil;
            //normal invFrag will be closed less than millisecond
        }

        @Override
        public void configured(Unit builder, Object value){
            changed = false;
            super.configured(builder, value);
            if(!changed) return;
            resetStats();
            int cstMult = 1, len = bluePrint.size;
            for(int p = 0; p < len; p++){
                int temp = bluePrint.get(p);
                if(temp != 0){
                    var partL = partInfo[temp - 1];
                    cstMult += partCostAccum * partL.tw * partL.th;
                }
            }
            cstMult -= partCostAccum;
            totalItemCountCost = totalItemCountPaid = 0;
            int[][] gridPrint = new int[len / gridH][gridH];
            blueprintRemainingCost.clear();
            for(int p = 0; p < len; p++){
                int temp = bluePrint.get(p);
                if(temp != 0){
                    var partL = partInfo[temp - 1];
                    var prtTmp = partL.cost;
                    for(var cstItem : prtTmp){
                        int cur = blueprintRemainingCost.get(cstItem.item, 0);
                        int increment = cstItem.amount * cstMult;
                        blueprintRemainingCost.put(cstItem.item, cur + increment);
                        totalItemCountCost += increment;
                    }
                }
                gridPrint[p / gridH][p % gridH] = temp;
            }
            currentStats.clear();
            for(int p = 0; p < len; p++){
                int temp = bluePrint.get(p);
                if(p == 0) continue;
                accumStats(partInfo[temp - 1], p / gridH, p % gridH, gridPrint);
            }
            if(!Vars.headless){
                Draw.draw(Draw.z(), () -> {
                    //backBuffer(gridPrint)
                    Tmp.m1.set(Draw.proj());
                    Draw.proj(0f, 0f, tx, ty);
                    buffer.begin(Color.clear);
                    Draw.color(Color.white);
                    //preDrawBuffer(gridPrint)
                    for(int p = 0; p < len; p++){
                        int temp = bluePrint.get(p);
                        drawPartBuffer(partInfo[temp - 1], p / gridH, p % gridH, gridPrint);
                    }
                    //postDrawBuffer(gridPrint)
                    buffer.end();
                    Draw.proj(Tmp.m1);
                    Draw.reset();
                });
            }
        }

        @Override
        public String config(){
            if(bluePrint.isEmpty()) return "";
            return IntPacker.packArray(bluePrint).toStringPack();
        }

        @Override
        public void writeExt(Writes write){
            if(bluePrint.isEmpty()){
                write.i(0);
                return;
            }
            var tmp = IntPacker.packArray(bluePrint).packed;
            int len = tmp.size;
            write.s(len);
            for(int i = 0; i < len; i++) write.i(tmp.get(i));
            if(blueprintRemainingCost.isEmpty()) write.s(0);
            else{
                write.s(blueprintRemainingCost.size);
                for(var i : blueprintRemainingCost){
                    write.s(i.key.id);
                    write.s(i.value >>> 16);
                }
            }
        }

        @Override
        public void readExt(Reads read, byte revision){
            short packedSize = read.s();
            IntSeq pack = new IntSeq();
            for(int i = 0; i < packedSize; i++) pack.add(read.i());
            configureAny(pack);
            //how this possible
            short costSize = read.s();
            if(costSize > 0){
                for(int i = 0; i < costSize; i++){
                    Item item = Vars.content.item(read.s());
                    short paid = read.s();
                    int cur = blueprintRemainingCost.get(item, -1);
                    if(cur != -1){
                        blueprintRemainingCost.put(item, (paid << 16) + cur);
                        totalItemCountPaid += paid;
                    }
                }
            }
            if(totalItemCountPaid == totalItemCountCost) applyStats();
        }
    }
}

