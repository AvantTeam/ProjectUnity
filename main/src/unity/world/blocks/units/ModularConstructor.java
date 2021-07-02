package unity.world.blocks.units;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.Block;
import mindustry.world.blocks.units.UnitFactory.*;
import mindustry.world.consumers.*;
import unity.graphics.*;

public class ModularConstructor extends Block{
    public TextureRegion[] topRegions;
    public float minSize = 24.5f;
    public Seq<ModularConstructorPlan> plans = new Seq<>(4);
    public float efficiencyPerTier = 80f;
    public Color buildColor = UnityPal.advance;
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

    public class ModularConstructorBuild extends Building{
        public int currentPlan = -1, tier = 4;
        public float progress, topOffset;

        @Override
        public void buildConfiguration(Table table){
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            int lastTier = -1;
            table.setBackground(Styles.black3);
            Table cont = null;
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
            ModularConstructorPlan plan = currentPlan != -1 ? plans.get(currentPlan) : null;
            if(plan != null && plan.tier <= tier){
                float prog = Mathf.clamp(progress / (plan.time - Math.max(tier - plan.tier, 0f) * efficiencyPerTier));
                Draw.mixcol(buildColor, 1f);
                Draw.alpha(0.35f);
                Draw.rect(plan.unit.fullIcon, this, 0);
                Draw.alpha(1f);
                Draw.mixcol();
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
            }
            for(int i = 0; i < 4; i++){
                TextureRegion tex = topRegions[Mathf.clamp(i / 2, 0, 1)];
                float ang = (i * 90f);
                Tmp.v1.trns(ang, topOffset).add(this);
                Draw.rect(tex, Tmp.v1, ang);
            }
        }

        @Override
        public void updateTile(){
            ModularConstructorPlan plan = currentPlan != -1 ? plans.get(currentPlan) : null;
            if(plan != null && plan.tier <= tier){
                if(consValid()){
                    progress += Time.delta;
                }
                if(progress >= plan.time - Math.max(tier - plan.tier, 0f) * efficiencyPerTier){
                    Unit unit = plan.unit.spawn(team, x, y);
                    unit.rotation = 90f;
                    progress = 0f;
                }
                topOffset = Mathf.lerpDelta(topOffset, Math.max(0f, (plan.unit.hitSize / 2f) - minSize), 0.1f);
            }else{
                topOffset = Mathf.lerpDelta(topOffset, 0f, 0.1f);
            }
        }

        @Override
        public boolean shouldConsume(){
            if(currentPlan == -1) return false;
            return enabled;
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
        }

        @Override
        public void read(Reads read){
            super.read(read);
            currentPlan = read.s();
            tier = read.s();
            progress = read.f();
        }
    }
}
