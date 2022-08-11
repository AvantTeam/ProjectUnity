package unity.parts;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;
import unity.gen.entities.*;
import unity.parts.stats.AdditiveStat.*;
import unity.parts.stats.*;
import unity.util.*;

//like Block, this is a singleton
public class ModularUnitPartType extends PartType{
    public static IntMap<ModularUnitPartType> partMap = new IntMap<>();
    private static int idAcc = 0;
    private final int id = idAcc++;
    public String category;

    public static TextureRegion[][] panelling;
    public static final Seq<PartDoodadPalette> unitDoodads = new Seq<>();
    /** texture will/may have three variants for the front middle and back **/
    public TextureRegion[] top;
    public TextureRegion[] outline;
    public boolean hasExtraDecal = false;
    public boolean hasCellDecal = false;
    public TextureRegion[] cell;
    public int costTotal = 0;
    //module cost..

    //places it can connect to
    public boolean root = false;
    public boolean visible = true;

    public ModularUnitPartType(String name){
        super(name);
        arc.util.Log.info(id());
        partMap.put(id(), this);
    }

    public ModularUnitPart create(int x, int y){
        return new ModularUnitPart(this, x, y);
    }

    @Override
    public int id(){
        return id;
    }

    public static void loadStatic(){
        panelling = Core.atlas.find("unity-panel").split(16, 16);
        unitDoodads.add(new PartDoodadPalette(true, true, 1, 1, "1x1", 12));
        unitDoodads.add(new PartDoodadPalette(false, true, 2, 2, "2x2", 5));
        unitDoodads.add(new PartDoodadPalette(false, true, 3, 3, "3x3", 4));
        unitDoodads.add(new PartDoodadPalette(true, true, 3, 2, "3x2", 3));

        for(int i = 0; i < unitDoodads.size; i++){
            unitDoodads.get(i).load();
        }
    }

    public void load(){
        hasCustomDraw = open || hasCellDecal || hasExtraDecal;
        ///
        String prefix = "unity-part-" + name;
        icon = Core.atlas.find(prefix + "-icon");
        top = new TextureRegion[]{
        getPartSprite(prefix + "-front"),
        getPartSprite(prefix + "-mid"),
        getPartSprite(prefix + "-back"),
        };
        outline = new TextureRegion[]{
        getPartSprite(prefix + "-front-outline"),
        getPartSprite(prefix + "-mid-outline"),
        getPartSprite(prefix + "-back-outline"),
        };
        cell = new TextureRegion[]{
        getPartSprite(prefix + "-cell-side"),
        getPartSprite(prefix + "-cell-center")
        };
    }

    public static TextureRegion getPartSprite(String e){
        var f = Core.atlas.find(e);
        if(f == Core.atlas.find("error")){
            f = Core.atlas.find("unity-part-empty");
        }
        return f;
    }

    public void requirements(String category, ItemStack[] itemcost){
        this.category = category;
        this.cost = itemcost;
        costTotal = 0;
        for(var i : itemcost){
            costTotal += i.amount;
        }
    }

    @Override
    public void drawCell(DrawTransform transform, Part part){
        if(hasCellDecal){
            TextureRegion cellSprite = cell[Math.abs(part.cx) < 0.01 ? 1 : 0];
            transform.drawRectScl(cellSprite, part.cx * partSize, part.cy * partSize, part.cx < 0 ? 1 : -1, 1);
        }
    }

    @Override
    public void drawTop(DrawTransform transform, Part part){
        if(hasExtraDecal)
            transform.drawRect(top[part.front], part.cx * partSize, part.cy * partSize);
    }

    @Override
    public void draw(DrawTransform transform, Part part, Modularc parent){
        var i = part.panelingIndexes[0];
        transform.drawRect(panelling[i % 12][i / 12], part.ax * partSize, part.ay * partSize);
    }

    @Override
    public void drawOutline(DrawTransform transform, Part part){
        if(hasExtraDecal)
            transform.drawRect(outline[part.front], part.cx * partSize, part.cy * partSize);
    }

    public static ModularUnitPartType getPartFromId(int id){
        if(partMap.containsKey(id)){
            return partMap.get(id);
        }else{
            Log.info("Part of id " + id + " not found");
            return partMap.get(0);
        }
    }

    //units
    public void armor(float amount){
        stats.add(new ArmourStat(amount));
    }

    public void health(float amount){
        stats.add(new HealthStat(amount));
    }

    public void mass(float amount){
        stats.add(new MassStat(amount));
    }

    public void producesPower(float amount){
        stats.add(new EngineStat(amount));
    }

    public void usesPower(float amount){
        stats.add(new PowerUsedStat(amount));
    }

    public void addsWeaponSlots(float amount){
        stats.add(new WeaponSlotStat(amount));
    }

    public void addsAbilitySlots(float amount){
        stats.add(new AbilitySlotStat(amount));
    }

    public void healthMul(float amount){
        stats.add(new HealthStat(amount));
    }

    public void itemCapacity(float amount){
        stats.add(new ItemCapacityStat(amount));
    }
    //turrets
    //???

    @Override
    public void display(Table table){
        table.table(header -> {
            //copied from blocks xd
            header.left();
            header.add(new Image(icon)).size(8 * 4);
            header.labelWrap(() -> Core.bundle.get("part." + name))
            .left().width(190f).padLeft(5);
            header.add().growX();
            header.button("?", Styles.flatBordert, () -> {
                //Unity.ui.partinfo.show(this);
            }).size(8 * 5).padTop(-5).padRight(-5).right().grow().name("blockinfo");
        });
        table.row();
        table.table(req -> {
            req.top().left();
            req.add("[lightgray]" + Stat.buildCost.localized() + ":[] ").left().top();
            for(ItemStack stack : cost){
                req.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
            }
        }).growX().left().margin(3);
    }

    public void displayTooltip(Table tip){
        tip.setBackground(Tex.button);
        tip.table(t -> {
            t.table(header -> {
                header.top().left();
                header.image(icon).size(8 * 4);

                header.label(() -> Core.bundle.get("part." + name))
                .left().padLeft(5);
            }).top().left();
            t.row();
            t.image(Tex.whiteui).color(Pal.darkishGray).center().growX().height(5).padTop(5); // separator
        }).growX().padBottom(5);
        tip.row();
        tip.table(desc -> {
            desc.labelWrap(Core.bundle.get("part." + name + ".description")).minWidth(200).grow();
            desc.row();
            desc.image(Tex.whiteui).color(Pal.darkishGray).center().growX().height(5).padTop(5);
        }).top().left().minWidth(300).padBottom(5);

        tip.row();
        tip.table(statTable -> stats.each(stat -> stat.display(statTable))).left();

        tip.row();
        tip.table(req -> {
            req.top().left();
            req.add("[lightgray]" + Stat.buildCost.localized() + ":[] ").left().top();
            req.row();
            req.table(reqlist -> {
                reqlist.top().left();
                for(ItemStack stack : cost){
                    reqlist.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
                }
            }).grow();
        }).growX();
    }
}


