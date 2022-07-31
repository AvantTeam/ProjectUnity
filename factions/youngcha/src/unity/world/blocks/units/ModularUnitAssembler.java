package unity.world.blocks.units;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.blocks.payloads.*;
import unity.content.*;
import unity.gen.entities.*;
import unity.parts.*;
import unity.ui.*;
import unity.util.*;

import static mindustry.Vars.content;
import static unity.parts.ModularUnitPartType.partSize;

public class ModularUnitAssembler extends PayloadBlock{
    public static ModularUnitEditorDialog modularUnitEditorDialog;
    public int unitModuleWidth = 255;
    public int unitModuleHeight = 255;
    public boolean sandbox = false;
    public NinePatch chassis;

    TextureRegion softShadowRegion;

    public ModularUnitAssembler(String name){
        super(name);
        solid = false;
        configurable = true;
        config(byte[].class, (ModularUnitAssemblerBuild build, byte[] data) -> {
            build.blueprint.decode(data);
            build.construct = build.blueprint.construct();
            for(var c : build.currentlyConstructing){
                c.complete();
            }
            build.currentlyConstructing.clear();
        });
        config(Boolean.class, (ModularUnitAssemblerBuild build, Boolean data) -> {if(data){build.spawnUnit();}});
        clipSize = Math.max(unitModuleWidth, unitModuleHeight) * partSize;
        outputsPayload = !sandbox;
    }

    @Override
    public void load(){
        super.load();
        chassis = new NinePatch(Core.atlas.find("unity-chassis-panel"), 6, 6, 6, 6);
        chassis.scale(0.25f, 0.25f);
        softShadowRegion = Core.atlas.find("circle-shadow");
    }

    public static class ModuleConstructing{
        public short x, y;
        public ItemStack[] remaining;
        public ModularUnitPartType type;
        public int takenBy = -1;

        //moduleblock?
        ModuleConstructing(ModularUnitPart mp){
            this.x = (short)mp.x();
            this.y = (short)mp.y();
            remaining = new ItemStack[mp.type.cost.length];
            for(int i = 0; i < remaining.length; i++){
                remaining[i] = new ItemStack(mp.type.cost[i].item, mp.type.cost[i].amount);
            }
            type = (ModularUnitPartType)mp.type;
        }

        public void write(Writes write){
            write.s(x);
            write.s(y);
            write.s(type.id);
            write.s(remaining.length); //amount of items

            for(ItemStack itemStack : remaining){
                write.s(itemStack.item.id); //item ID
                write.i(itemStack.amount); //item amount
            }
            write.i(takenBy);
        }

        public ModuleConstructing(Reads read, byte revision){
            x = read.s();
            y = read.s();
            type = ModularUnitPartType.getPartFromId(read.s());
            remaining = new ItemStack[read.s()];
            for(int i = 0; i < remaining.length; i++){
                remaining[i] = new ItemStack(content.item(read.s()), read.i());
            }
            takenBy = read.i();
        }

        public int total(){
            int t = 0;
            for(ItemStack itemStack : remaining){
                t += itemStack.amount;
            }
            return t;
        }

        public boolean submitItem(Item s){
            for(int i = 0; i < remaining.length; i++){
                if(s == remaining[i].item && remaining[i].amount > 0){
                    remaining[i].amount--;
                    if(remaining[i].amount == 0 && total() == 0){
                        remaining = new ItemStack[0];
                    }
                    return true;
                }
            }
            return false;
        }

        public Item any(){
            for(ItemStack itemStack : remaining){
                if(itemStack.amount > 0){
                    return itemStack.item;
                }
            }
            return null;
        }

        public boolean isComplete(){
            return remaining.length == 0;
        }

        public void complete(){
            remaining = new ItemStack[0];
        }

    }

    public class ModularUnitAssemblerBuild extends PayloadBlockBuild<UnitPayload>{
        public ModularUnitBlueprint blueprint;
        public Seq<ModuleConstructing> currentlyConstructing = new Seq<>();
        IntSet built = new IntSet();
        public ModularUnitConstruct construct;

        public ModularUnitAssemblerBuild(){
            blueprint = new ModularUnitBlueprint(unitModuleWidth, unitModuleHeight);
            construct = blueprint.construct();
        }

        @Override
        public void buildConfiguration(Table table){
            if(modularUnitEditorDialog == null) modularUnitEditorDialog = new ModularUnitEditorDialog();
            //ui lambda soup time
            var configureButtonCell = table.button(Tex.whiteui, Styles.cleari, 50,
            () -> modularUnitEditorDialog.show(
            blueprint, () -> {
                construct = blueprint.construct();
                for(var c : currentlyConstructing){
                    c.complete();
                }
                currentlyConstructing.clear();
            },
            ModularUnitEditorDialog.unitInfoViewer, part -> part.visible && part.w <= unitModuleWidth && part.h <= unitModuleHeight
            )
            );
            configureButtonCell.size(50);
            configureButtonCell.get().getStyle().imageUp = Icon.pencil;

            if(sandbox){
                //creative spawn unit.
                var spawnUnitButtonCell = table.button(Tex.whiteui, Styles.cleari, 50,
                (() -> configure(true))
                ).size(50);
                spawnUnitButtonCell.get().getStyle().imageUp = Icon.add;
            }

            if(this.block.hasItems){
                Vars.control.input.inv.showFor(this);
            }
        }

        void rebuildItemCost(Table req){
            req.clear();
            req.top().left();
            int i = 0;
            for(ItemStack stack : blueprint.itemRequirements()){
                req.add(new ItemDisplay(stack.item, stack.amount, false)).padRight(5);
                i++;
                if(i % 6 == 0){
                    req.row();
                }
            }
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            ModularUnitConstruct[] curCon = {construct};
            table.table(t -> {
                t.update(() -> {
                    if(curCon[0] != construct){
                        rebuildItemCost(t);
                        curCon[0] = construct;
                    }
                });
                rebuildItemCost(t);
            }).minHeight(35 * (1 + blueprint.itemRequirements().toArray().length / 6.0f)).growY().top();
        }

        //js
        public void spawnUnit(){
            if(Vars.net.client()){
                return;
            }
            var t = YoungchaUnitTypes.modularUnitSmall.create(team);
            ((Modularc)t).setConstruct(construct);
            t.set(x, y);
            t.add();
            t.rotation = rotdeg();
            Events.fire(new UnitCreateEvent(t, this));
        }

        public void createUnit(){
            var t = YoungchaUnitTypes.modularUnitSmall.create(team);
            ((Modularc)t).setConstruct(construct);
            payload = new UnitPayload(t);
            payVector.setZero();
            Events.fire(new UnitCreateEvent(payload.unit, this));
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(!construct.isEmpty() && !sandbox){
                if(built.size >= construct.partsList.size && payload == null){
                    createUnit();
                    built.clear();
                    currentlyConstructing.clear();
                }
            }
            moveOutPayload();
        }

        //arm needs to get an available job, then move, then build it
        public ModuleConstructing getJob(Building b, Item e){
            if(construct == null || payload != null){
                return null;
            }
            if(e == null){
                //gets any random job
                for(var c : currentlyConstructing){
                    if(c.takenBy == b.pos()){
                        return c;
                    }
                    if(c.takenBy == -1){
                        c.takenBy = b.pos();
                        return c;
                    }
                }
                for(var part : construct.partsList){
                    if(!built.contains(Point2.pack(part.x(), part.y()))){
                        var con = new ModuleConstructing(part);
                        con.takenBy = b.pos();
                        currentlyConstructing.add(con);
                        return con;
                    }
                }

            }else{
                //gets any job with a specified item
                for(var c : currentlyConstructing){
                    if(c.takenBy == b.pos()){
                        return c;
                    }
                    if(c.takenBy == -1){
                        for(var stack : c.remaining){
                            if(stack.item.equals(e) && stack.amount > 0){
                                c.takenBy = b.pos();
                                return c;
                            }
                        }
                    }
                }
                for(var part : construct.partsList){
                    if(!built.contains(Point2.pack(part.x(), part.y()))
                    && currentlyConstructing.find(bPart -> bPart.x == part.x() && bPart.y == part.y()) == null){
                        boolean hasItem = false;
                        for(var stack : part.type.cost){
                            if(stack.item.equals(e)){
                                hasItem = true;
                                break;
                            }
                        }
                        if(hasItem){
                            var con = new ModuleConstructing(part);
                            con.takenBy = b.pos();
                            currentlyConstructing.add(con);
                            return con;
                        }
                    }
                }
            }
            return null; //no job for you
        }

        //called by server on client only
        public void finishModule(Point2 module){
            if(!built.contains(Point2.pack(module.x, module.y))){
                built.add(Point2.pack(module.x, module.y));
            }
            for(var c : currentlyConstructing){
                if(c.x == module.x && c.y == module.y){
                    c.complete();
                }
            }
        }

        public boolean constructModule(ModuleConstructing module, Item item){
            boolean b = module.submitItem(item);
            if(module.total() == 0){
                currentlyConstructing.remove(module);
                built.add(Point2.pack(module.x, module.y));
            }
            return b;
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            if(!sandbox){
                Draw.rect(outRegion, x, y, rotdeg());
            }
            drawTeamTop();
            DrawTransform dt = new DrawTransform();
            dt.setTranslate(x, y);
            dt.setRotation(rotdeg());
            if(construct != null && !sandbox && payload == null){
                //9 slice?

                Draw.color(0, 0, 0, 0.4f);
                float rad = 1.6f;
                float size = (construct.parts.length + construct.parts[0].length) * 0.5f * partSize;
                Draw.rect(softShadowRegion, this, size * rad * Draw.xscl, size * rad * Draw.yscl, rotdeg() - 90);
                Draw.color();
                for(var part : construct.partsList){
                    if(built.contains(Point2.pack(part.x(), part.y()))){
                        part.type.draw(dt, part, null);
                    }else{
                        float dx = x + (part.ax() - 0.5f) * partSize;
                        float dy = y + (part.ay() - 0.5f) * partSize;
                        chassis.draw(dx, dy, x - dx, y - dy, part.type.w * partSize, part.type.h * partSize, 1, 1, rotdeg() - 90);
                    }
                }
            }
            Draw.z(Layer.blockOver);
            payRotation = rotdeg();
            drawPayload();
        }

        public Vec2 modulePos(float x, float y){
            if(construct == null){
                return new Vec2(x, y);
            }
            var v = new Vec2((x - construct.parts.length * 0.5f) * partSize, (y - construct.parts[0].length * 0.5f) * partSize);
            v.rotate(90 * (rotation - 1));
            return v.add(this.x, this.y);
        }

        @Override
        public byte[] config(){
            return blueprint.encode();
        }

        @Override
        public void configured(Unit builder, Object value){
            super.configured(builder, value);
        }


        @Override
        public void write(Writes write){
            super.write(write);
            var data = blueprint.encode();
            write.i(data.length);
            write.b(data);
            write.i(built.size);
            built.each(write::i);
            write.i(currentlyConstructing.size);
            for(int i = 0; i < currentlyConstructing.size; i++){
                currentlyConstructing.get(i).write(write);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            byte[] data = new byte[read.i()];
            read.b(data);
            blueprint.decode(data);
            construct = blueprint.construct();
            int builtLen = read.i();
            for(int i = 0; i < builtLen; i++){
                built.add(read.i());
            }
            int cLen = read.i();
            for(int i = 0; i < cLen; i++){
                currentlyConstructing.add(new ModuleConstructing(read, revision));
            }
        }

        public boolean isSandbox(){
            return sandbox;
        }
    }
}
