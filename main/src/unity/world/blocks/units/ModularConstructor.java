package unity.world.blocks.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import unity.graphics.*;
import unity.world.blocks.units.ModularConstructorPart.*;
import unity.world.modules.*;
import unity.world.modules.ModularConstructorModule.*;

import java.util.*;

public class ModularConstructor extends Block{
    public TextureRegion[] topRegions;
    public float minSize = 24.5f - 7f;
    public Seq<ModularConstructorPlan> plans = new Seq<>(4);
    public float efficiencyPerTier = 80f, maxEfficiency = 5f * 60f;
    public Color buildColor = UnityPal.advance;
    public Vec2[] moduleNodes = {new Vec2(3.5f, 9.5f)};
    public boolean mirrorNodes = true;
    public int moduleSize = 6, moduleConnections = 8;
    public Block moduleBlock;
    protected int maxTier = 0;
    protected int[] capacities;
    protected Seq<ModularConstructorPlan> sortedPlans;

    public ModularConstructor(String name){
        super(name);
        update = true;
        sync = true;
        solid = false;
        hasPower = true;
        hasItems = true;
        configurable = true;

        config(Integer.class, (ModularConstructorBuild tile, Integer i) -> {
            tile.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            tile.progress = 0;
        });

        config(UnitType.class, (UnitFactoryBuild tile, UnitType val) -> {
            tile.currentPlan = plans.indexOf(p -> p.unit == val);
            tile.progress = 0;
        });

        consumes.add(new ConsumeItemDynamic((ModularConstructorBuild e) -> e.currentPlan != -1 ? plans.get(e.currentPlan).requirements : ItemStack.empty));
    }

    @Override
    public void init(){
        if(mirrorNodes && moduleNodes != null && moduleNodes.length > 0){
            Vec2 point = moduleNodes[0];
            int amount = Math.abs(point.x) > 0 ? 8 : 4;
            moduleNodes = Arrays.copyOf(moduleNodes, amount);
            int i = 0;
            for(int j = 0; j < 4; j++){
                moduleNodes[i++] = point.cpy().rotate(j * 90f);
                if(Math.abs(point.x) > 0){
                    Vec2 p = point.cpy();
                    moduleNodes[i++] = p.set(-p.x, p.y).rotate(j * 90f);
                }
            }
        }

        capacities = new int[Vars.content.items().size];
        sortedPlans = new Seq<>(plans);

        int i = 0;
        for(ModularConstructorPlan plan : plans){
            plan.index = i++;

            maxTier = Math.max(maxTier, plan.tier);
            for(ItemStack stack : plan.requirements){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }
        sortedPlans.sort(p -> p.tier);
        super.init();
    }

    @Override
    public void load(){
        super.load();
        topRegions = new TextureRegion[2];
        for(int i = 0; i < 2; i++) topRegions[i] = Core.atlas.find(name + "-top-" + i);
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{region, Core.atlas.find(name + "-top")};
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    public static class ModularConstructorPlan{
        public UnitType unit;
        public ItemStack[] requirements;
        public int tier;
        public float time;
        int index;

        public ModularConstructorPlan(UnitType unit, float time, int tier, ItemStack[] requirements){
            this.unit = unit;
            this.time = time;
            this.tier = tier;
            this.requirements = requirements;
        }
    }

    public class ModularConstructorBuild extends Building implements ModularConstructorModuleInterface{
        public int currentPlan = -1, tier = 0;
        public float progress, topOffset;
        public Seq<ModularConstructorPartBuild> parts = new Seq<>();
        ModularConstructorModule module = new ModularConstructorModule(this);
        Building[] occupied = new Building[moduleNodes.length];


        public int moduleConnections(){
            return moduleConnections;
        }

        @Override
        public ModularConstructorModule consModule(){
            return module;
        }

        @Override
        public boolean consConnected(Building other){
            int ang = Mathf.mod(Mathf.round(other.angleTo(this) / 90f), 4);
            if(moduleBlock == null || moduleBlock == other.block){
                int i = 0;
                for(Vec2 node : moduleNodes){
                    Tmp.r1.setCentered((node.x * Vars.tilesize) + x, (node.y * Vars.tilesize) + y, moduleSize * Vars.tilesize);
                    Tmp.r2.setCentered(other.x, other.y, (other.block.size * Vars.tilesize) - 1f);
                    if(ang == other.rotation() && other.block.size == moduleSize && Tmp.r1.contains(Tmp.r2)){
                        occupied[i] = other;
                        return true;
                    }
                    i++;
                }
            }
            return false;
        }

        @Override
        public void buildConfiguration(Table table){
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            int lastTier = -1;
            table.setBackground(Styles.black3);
            Table cont = null;
            table.add(Core.bundle.format("stat.unity.currentTier", tier + 1));
            table.row();
            for(ModularConstructorPlan plan : sortedPlans){
                if(!plan.unit.unlockedNow() || plan.tier > tier) continue;
                if(plan.tier != lastTier){
                    if(lastTier != -1) table.row();
                    lastTier = plan.tier;
                    table.add("[lightgray]T" + (plan.tier + 1) + ":");
                    table.row();
                    cont = new Table();
                    cont.defaults().size(40);
                    table.add(cont);
                }
                if(cont != null){
                    ImageButton button = cont.button(Tex.whiteui, Styles.clearToggleTransi, 24, () ->
                    Vars.control.input.frag.config.hideConfig()).group(group).get();
                    button.changed(() -> currentPlan = button.isChecked() ? plan.index : -1);
                    button.getStyle().imageUp = new TextureRegionDrawable(plan.unit.uiIcon);
                    button.update(() -> button.setChecked(currentPlan == plan.index));
                }
            }
        }

        @Override
        public void draw(){
            super.draw();
            for(int i = 0; i < occupied.length; i++){
                if(occupied[i] == null){
                    Vec2 node = moduleNodes[i];
                    Tmp.r1.setCentered((node.x * Vars.tilesize) + x, (node.y * Vars.tilesize) + y, moduleSize * Vars.tilesize);
                    Draw.color(Tmp.c1.set(buildColor).a(0.3f));
                    Fill.crect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height);
                    Draw.reset();
                }
            }
            ModularConstructorPlan plan = currentPlan != -1 ? plans.get(currentPlan) : null;
            if(plan != null && plan.tier <= tier){
                float time = progressTime(plan);
                float prog = Mathf.clamp(progress / time);
                Draw.mixcol(buildColor, 1f);
                Draw.alpha(0.35f);
                Draw.rect(plan.unit.fullIcon, this, 0);
                Draw.alpha(1f);
                Draw.mixcol();
                if(progress < time || Units.canCreate(team, plan.unit)){
                    if(progress > 0.001f){
                        Draw.draw(Draw.z(), () -> {
                            Draw.shader(Shaders.blockbuild);
                            Draw.color(buildColor);
                            Shaders.blockbuild.region = plan.unit.fullIcon;
                            Shaders.blockbuild.progress = prog;
                            Draw.rect(plan.unit.fullIcon, this, 0f);
                            Draw.flush();
                            Draw.color();
                            Draw.shader();
                        });
                    }
                }else{
                    Draw.color(0.7f, 0.7f, 0.7f);
                    Draw.rect(plan.unit.fullIcon, this, 0);
                }
                Draw.reset();
            }
            for(int i = 0; i < 4; i++){
                TextureRegion tex = topRegions[Mathf.clamp(i / 2, 0, 1)];
                float ang = (i * 90f);
                Tmp.v1.trns(ang, topOffset).add(this);
                Draw.rect(tex, Tmp.v1, ang);
            }
        }

        @Override
        public void placed(){
            super.placed();
            module.graph.added(this);
        }

        float progressTime(ModularConstructorPlan plan){
            return Math.max(plan.time - Math.max(module.graph.tier - plan.tier, 0f) * efficiencyPerTier, maxEfficiency);
        }

        @Override
        public void updateTile(){
            module.update();

            for(int i = 0; i < occupied.length; i++){
                if(occupied[i] != null && !occupied[i].added) occupied[i] = null;
            }

            ModularConstructorPlan plan = currentPlan != -1 ? plans.get(currentPlan) : null;
            if(plan != null && plan.tier <= tier){
                float time = progressTime(plan);
                if(progress >= time){
                    if(Units.canCreate(team, plan.unit) && consValid()){
                        Unit unit = plan.unit.spawn(team, x, y);
                        unit.rotation = 90f;
                        cons.trigger();
                        progress = 0f;
                    }
                }else if(consValid()){
                    progress += Time.delta;
                }
                topOffset = Mathf.lerpDelta(topOffset, Math.max(0f, (plan.unit.hitSize / 2f) - minSize), 0.1f);
            }else{
                topOffset = Mathf.lerpDelta(topOffset, 0f, 0.1f);
            }
        }

        @Override
        public boolean consValid(){
            boolean valid = true;
            for(ModularConstructorPartBuild build : module.graph.all){
                valid &= build.cons.canConsume();
            }
            return super.consValid() && valid;
        }

        @Override
        public boolean shouldConsume(){
            if(currentPlan == -1) return false;
            return enabled && (!consumes.has(ConsumeType.item) || consumes.get(ConsumeType.item).valid(this));
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return currentPlan != -1 && items.get(item) < getMaximumAccepted(item) &&
            Structs.contains(plans.get(currentPlan).requirements, stack -> stack.item == item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return capacities[item.id];
        }

        @Override
        public void write(Writes write){
            super.write(write);
            write.s(currentPlan);
            write.s(tier);
            write.f(progress);

            module.write(write);
        }

        @Override
        public void read(Reads read){
            super.read(read);
            currentPlan = read.s();
            tier = read.s();
            progress = read.f();

            module.read(read);
        }
    }
}
