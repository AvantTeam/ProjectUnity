package younggamExperimental.blocks;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.util.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.modules.*;
import younggamExperimental.*;
import younggamExperimental.blocks.Chopper.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class ModularTurret extends Turret implements GraphBlockBase{
    static final int mask = 65535;
    protected final Graphs graphs = new Graphs();
    PartInfo[] partInfo;//TODO
    final TextureRegion[] regions = new TextureRegion[4];
    TextureRegion partsRegion, rootRegion, rootOutlineRegion;//partAtlas,baseSprite,baseOutline;
    protected float yShift, yScale = 1f, partCostAccum = 0.2f;
    float autoBuildDelay = 10;
    protected int spriteGridSize = 32, spriteGridPadding;
    int gridW = 1, gridH = 1,
        tx, ty;

    public ModularTurret(String name){
        super(name);
        rotate = configurable = hasItems = true;
        config(String.class, (ModularTurretBuild build, String value) -> build.changed = build.setBluePrintFromString(value));
        config(IntSeq.class, (ModularTurretBuild build, IntSeq value) -> build.changed = build.setBluePrint(Utils.unpackInts(value)));
        configClear((ModularTurretBuild build) -> build.setBluePrint(null));
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) regions[i] = atlas.find(name + (i + 1));
        partsRegion = atlas.find("unity-partsicons");
        rootRegion = atlas.find(name + "-root");
        rootOutlineRegion = atlas.find(name + "-root-outline");
        tx = spriteGridPadding * 2 + gridW * spriteGridSize;
        ty = spriteGridPadding * 2 + gridH * spriteGridSize;
    }

    @Override
    public void init(){
        super.init();
        partInfo = UnityParts.getPartList();
    }

    @Override
    public void setStats(){
        super.setStats();
        graphs.setStats(stats);
        setStatsExt(stats);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        graphs.drawPlace(x, y, size, rotation, valid);
        super.drawPlace(x, y, rotation, valid);
    }

    @Override
    public Graphs graphs(){
        return graphs;
    }

    protected void setGridW(int s){
        gridW = Math.min(16, s);
    }

    protected void setGridH(int s){
        gridH = Math.min(16, s);
    }

    PartType[] getPartsCategories(){
        //TODO
        return null;
    }

    @Override
    public void setStatsExt(Stats stats){
        stats.add(Stat.ammo, table -> {

        });
    }

    public class ModularTurretBuild extends TurretBuild implements GraphBuildBase{
        protected GraphModules gms;
        //
        final OrderedMap<Item, Integer> blueprintRemainingCost = new OrderedMap<>(12);
        final IntSeq bluePrint = new IntSeq();
        final FrameBuffer buffer = new FrameBuffer(tx, ty);
        final StatContainer currentStats = new StatContainer();
        float turretRange = 80f,
            originalMaxHp,
            aniProg, aniSpeed, aniTime;
        int totalItemCountCost, totalItemCountPaid,
            itemCap;
        boolean changed, validTurret;

        float getPaidRatio(){
            if(totalItemCountCost == 0) return 0f;
            return ((float)totalItemCountPaid) / totalItemCountCost;
        }

        boolean setBluePrintFromString(String s){
            return setBluePrint(Utils.unpackIntsFromString(s));
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
            table.row();
            table.table().left().update(sub -> {
                sub.clearChildren();
                if(totalItemCountPaid == totalItemCountCost) return;
                sub.left();
                if(blueprintRemainingCost.isEmpty()) sub.labelWrap("No blueprint").color(Color.lightGray);
                else{//idk
                    for(var i : blueprintRemainingCost){
                        sub.image(i.key.uiIcon).size(iconMed);
                        sub.add((i.value >>> 16) + "/" + (i.value & mask));
                        sub.row();
                    }
                }
            });
        }

        void updateAutoBuild(){
            if(totalItemCountPaid >= totalItemCountCost) return;
            if(Vars.state.rules.infiniteResources || team.rules().infiniteResources || team.rules().cheat){
                for(var i : blueprintRemainingCost){
                    int temp = i.value & mask;
                    i.value <<= 16;
                    i.value += temp;
                }
                totalItemCountPaid = totalItemCountCost;
                applyStats();
                return;
            }
            if(timer(timerDump, autoBuildDelay)){
                var core = team.core();
                if(core == null) return;
                var cItems = core.items;
                for(var i : blueprintRemainingCost){
                    if((i.value >>> 16) < (i.value & mask) && cItems.get(i.key) > 0){
                        cItems.remove(i.key, 1);
                        totalItemCountPaid++;
                        blueprintRemainingCost.put(i.key, i.value + (1 << 16));
                        if(totalItemCountPaid == totalItemCountCost) applyStats();
                        return;
                    }
                }
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            int value = blueprintRemainingCost.get(item, 0);
            boolean hasSpace = value >>> 16 < (value & mask);
            return super.acceptItem(source, item) || hasSpace && acceptItemExt(source, item);
        }

        @Override
        public void handleItem(Building source, Item item){
            if(totalItemCountPaid == totalItemCountCost){
                handleItemExt(source, item);
                return;
            }
            totalItemCountPaid++;
            int value = blueprintRemainingCost.get(item, 0);
            blueprintRemainingCost.put(item, value + (1 << 16));
            if(totalItemCountPaid == totalItemCountCost) applyStats();
        }

        boolean acceptItemExt(Building source, Item item){
            if(!validTurret) return false;
            //TODO
            return false;
        }

        void handleItemExt(Building source, Item item){
            super.handleItem(source, item);
        }

        PartInfo[] getPartsConfig(){
            return partInfo;
        }

        @Override
        public boolean hasAmmo(){
            if(!validTurret) return false;
            //TODO
            return false;
        }

        void attemptRefillingMag(){
            //TODO
        }

        boolean haveAmmoType(){
            //TODO
            return false;
        }

        @Override
        public BulletType useAmmo(){
            return Bullets.standardCopper;
        }

        @Override
        public BulletType peekAmmo(){
            return Bullets.standardCopper;
        }

        void applyStats(){
            originalMaxHp = maxHealth;
            maxHealth = originalMaxHp + currentStats.hpinc;
            turretRange = 80f;//TODO
            heal(currentStats.hpinc * health / originalMaxHp);
            //TODO
        }

        @Override
        public void displayBarsExt(Table table){
            if(validTurret){
                //TODO
            }
        }

        void accumStats(PartInfo part, int x, int y, int[][] grid){
            var hp = part.stats.get(PartStatType.hp);
            if(hp != null) currentStats.hpinc += hp.asInt();
            //TODO
            var range = part.stats.get(PartStatType.rangeinc);
            if(range != null) currentStats.rangeInc += range.asInt();
            if(part.category == PartType.base){
                //TODO
            }
        }

        void resetStats(){
            if(originalMaxHp > 0f) maxHealth = originalMaxHp;
            validTurret = false;
            turretRange = 80f;
            itemCap = 10;
        }

        void drawPartBuffer(PartInfo part, int x, int y, int[][] grid){
            if(part.category != PartType.none && part.category != PartType.base){
                //TODO
            }
        }

        //TODO 굳이?
        void preDrawBuffer(){
            //TODO
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
            float cstMult = 1f;
            int len = bluePrint.size;
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
                        int increment = Mathf.floor(cstItem.amount * cstMult);
                        blueprintRemainingCost.put(cstItem.item, cur + increment);
                        totalItemCountCost += increment;
                    }
                }
                gridPrint[p / gridH][p % gridH] = temp;
            }
            currentStats.clear();
            for(int p = 0; p < len; p++){
                int temp = bluePrint.get(p);
                if(temp == 0) continue;
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
                        if(temp == 0) continue;
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

        @Override
        protected void updateShooting(){
            if(!validTurret) return;
            //TODO
        }

        void shootType(){
            //TODO
        }

        @Override
        protected void findTarget(){
            //?boolean targetAir = true, targetGround = true;
            target = Units.bestTarget(team, x, y, turretRange, e -> !e.dead, b -> true, unitSort);
        }

        //
        @Override
        public void updatePre(){
            updateAutoBuild();
        }

        void drawExt(){
            Draw.rect(regions[rotation()], x, y);
            drawTeamTop();
        }

        @Override
        public void draw(){
            aniTime += Time.delta;
            float prog = getPaidRatio();
            if(aniProg < prog){
                aniSpeed = (prog - aniProg) * 0.1f;
                aniProg += aniSpeed;
            }else{
                aniProg = prog;
                aniSpeed = 0f;
            }
            drawExt();
            var turretSprite = getBufferRegion();
            if(turretSprite != null){
                Draw.z(Layer.turret);
                if(getPaidRatio() < 1f){
                    float ou = turretSprite.u;
                    float ou2 = turretSprite.u2;
                    float ov = turretSprite.v;
                    float ov2 = turretSprite.v2;
                    turretSprite.setU2(Mathf.map(aniProg, 0f, 1f, ou + 0.5f * (ou2 - ou), ou2));
                    turretSprite.setU(Mathf.map(aniProg, 0f, 1f, ou + 0.5f * (ou2 - ou), ou));
                    turretSprite.setV2(Mathf.map(aniProg, 0f, 1f, ov + 0.5f * (ov2 - ov), ov2));
                    turretSprite.setV(Mathf.map(aniProg, 0f, 1f, ov + 0.5f * (ov2 - ov), ov));
                    UnityDrawf.drawConstruct(turretSprite, aniProg, Pal.accent, 1f, aniTime * 0.5f, Layer.turret, tex -> Draw.rect(tex, x, y, rotation + 90f));
                }else{
                    Draw.rect(turretSprite, x, y, rotation - 90f);
                    //TODO
                }
            }
        }

        //
        @Override
        public void created(){
            gms = new GraphModules(this);
            graphs.injectGraphConnector(gms);
            gms.created();
        }

        @Override
        public float efficiency(){
            return super.efficiency() * gms.efficiency();
        }

        @Override
        public void onRemoved(){
            gms.updateGraphRemovals();
            onDelete();
            super.onRemoved();
            onDeletePost();
        }

        @Override
        public void updateTile(){
            if(graphs.useOriginalUpdate()) super.updateTile();
            updatePre();
            gms.updateTile();
            updatePost();
            gms.prevTileRotation(rotation());
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            gms.onProximityUpdate();
            proxUpdate();
        }

        @Override
        public void display(Table table){
            super.display(table);
            gms.display(table);
            displayExt(table);
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            gms.displayBars(table);
            displayBarsExt(table);
        }

        @Override
        public void write(Writes write){
            super.write(write);
            gms.write(write);
            writeExt(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            gms.read(read, revision);
            readExt(read, revision);
        }

        @Override
        public GraphModules gms(){
            return gms;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, turretRange, team.color);
            gms.drawSelect();
        }
    }
}
