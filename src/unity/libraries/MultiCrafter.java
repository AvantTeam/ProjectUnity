package unity.libraries;

import arc.util.*;
import arc.util.Log.*;
import arc.struct.*;
import arc.scene.*;
import arc.scene.ui.layout.*;
import arc.scene.ui.Button.*;
import mindustry.graphics.Pal;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;
import mindustry.world.meta.values.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.ui.*;
import mindustry.ui.fragments.BlockInventoryFragment;
import unity.libraries.Recipe.*;

import static arc.Core.*;
import static mindustry.Vars.*;

import com.google.common.base.Objects;

public class MultiCrafter extends GenericCrafter{
	public final Recipe[] recs;
	private ButtonStyle infoStyle = null;
	public final ObjectSet<Liquid> liquidSet = new ObjectSet();
	private boolean hasOutputItem = false;
	public final ObjectSet<Item> inputItemSet = new ObjectSet();
	public final ObjectSet<Liquid> inputLiquidSet = new ObjectSet();
	public final ObjectSet<Item> outputItemSet = new ObjectSet();
	public final ObjectSet<Liquid> outputLiquidSet = new ObjectSet();
	private boolean dumpToggle = false;
	private boolean powerBarI = false;
	private boolean powerBarO = false;
	private final ExtraBlockInventoryFragment invFrag = new ExtraBlockInventoryFragment();

	public MultiCrafter(String name, Recipe[] recs){
		super(name);
		this.recs = recs;
		configurable = true;
		hasItems = true;
		hasLiquids = true;
		hasPower = false;
		saveConfig = true;
	}

	public Recipe[] getRecipe(){ return recs; }

	@Override
	public void init(){
		for (short i = 0; i < recs.length; i++){
			InputContents input = recs[i].input;
			OutputContents output = recs[i].output;
			if (input.power > 0f) powerBarI = true;
			if (output.power > 0f) powerBarO = true;
			if (input.items.length > 0){
				for (int j = 0, len = input.items.length; j < len; j++) inputItemSet.add(input.items[j].item);
			}
			if (input.liquids.length > 0){
				for (int j = 0, len = input.liquids.length; j < len; j++){
					liquidSet.add(input.liquids[j].liquid);
					inputLiquidSet.add(input.liquids[j].liquid);
				}
			}
			if (output.items.length > 0){
				for (int j = 0, len = output.items.length; j < len; j++) outputItemSet.add(output.items[j].item);
			}
			if (output.liquids.length > 0){
				for (int j = 0, len = output.liquids.length; j < len; j++){
					liquidSet.add(output.liquids[j].liquid);
					outputLiquidSet.add(output.liquids[j].liquid);
				}
			}
		}
		if (powerBarI){
			hasPower = true;
			consumes.add(new MultiConsumePower());
		}
		consumesPower = powerBarI;
		outputsPower = powerBarO;
		super.init();
		if (!outputLiquidSet.isEmpty()) outputsLiquid = true;
		timers++;
		if (!headless) infoStyle = scene.getStyle(ButtonStyle.class);
	}

	@Override
	public void displayInfo(Table table){
		super.displayInfo(table);
		int recLen = recs.length;
		for (short i = 0; i < recLen; i++){
			Recipe rec = recs[i];
			ItemStack[] inputItems = rec.input.items;
			ItemStack[] outputItems = rec.output.items;
			LiquidStack[] inputLiquids = rec.input.liquids;
			LiquidStack[] outputLiquids = rec.output.liquids;
			float inputPower = rec.input.power;
			float outputPower = rec.output.power;
			// what the fuck that I need this
			short ii = i;
			table.table(infoStyle.up, part -> {
				part.add("[accent]" + BlockStat.input.localized()).expandX().left().row();
				part.table(row -> {
					for (int l = 0, len = inputItems.length; l < len; l++)
						row.add(new ItemDisplay(inputItems[l].item, inputItems[l].amount, true)).padRight(5f);
				}).left().row();
				part.table(row -> {
					for (int l = 0, len = inputLiquids.length; l < len; l++)
						row.add(new LiquidDisplay(inputLiquids[l].liquid, inputLiquids[l].amount, false));
				}).left().row();
				if (inputPower > 0f){
					part.table(row -> {
						row.add("[lightgray]" + BlockStat.powerUse.localized() + ":[]").padRight(4f);
						(new NumberValue(inputPower * 60f, StatUnit.powerSecond)).display(row);
					}).left().row();
				}
				part.add("[accent]" + BlockStat.output.localized()).left().row();
				part.table(row -> {
					for (int jj = 0, len = outputItems.length; jj < len; jj++)
						row.add(new ItemDisplay(outputItems[jj].item, outputItems[jj].amount, true)).padRight(5f);
				}).left().row();
				part.table(row -> {
					for (int jj = 0, len = outputLiquids.length; jj < len; jj++)
						row.add(new LiquidDisplay(outputLiquids[jj].liquid, outputLiquids[jj].amount, false));
				}).left().row();
				if (outputPower > 0f){
					part.table(row -> {
						row.add("[lightgray]" + BlockStat.basePowerGeneration.localized() + ":[]").padRight(4f);
						(new NumberValue(outputPower * 60f, StatUnit.powerSecond)).display(row);
					}).left().row();
				}
				part.table(row -> {
					row.add("[lightgray]" + BlockStat.productionTime.localized() + ":[]").padRight(4f);
					(new NumberValue(rec.craftTime / 60f, StatUnit.seconds)).display(row);
				}).left().row();
				customDisplay(part, ii);
			}).color(Pal.accent).left().growX();
			table.add().size(18f).row();
		}
	}

	@Override
	public void setStats(){
		super.setStats();
		if (powerBarI) stats.remove(BlockStat.powerUse);
		stats.remove(BlockStat.productionTime);
	}

	@Override
	public void setBars(){
		super.setBars();
		bars.remove("liquid");
		bars.remove("items");
		if (!powerBarI) bars.remove("power");
		if (powerBarO) bars.add("poweroutput",
			(MultiCrafterBuild entity) -> new Bar(
				() -> bundle.format("bar.poweroutput",
					Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
				() -> Pal.powerBar, () -> entity.productionEfficiency));
		if (!liquidSet.isEmpty()){
			liquidSet.each(k -> {
				bars.add(k.localizedName, entity -> new Bar(() -> k.localizedName, () -> k.barColor(),
					() -> entity.liquids.get(k) / liquidCapacity));
			});
		}
	}

	@Override
	public boolean outputsItems(){
		return hasOutputItem;
	}

	public void customDisplay(Table part, int i){

	}

	public class MultiCrafterBuild extends GenericCrafterBuild{
		private short toggle = 0;
		private float[] progressArr = new float[recs.length];
		private boolean cond = false;
		private boolean condValid = false;
		public float productionEfficiency = 0f;
		private OrderedSet<item> toOutputItemSet=new OrderedSet();
		private OrderedSet<liquid> toOutputLiquidSet=new OrderedSet();
		private int dumpItemEntry=0;
		private int itemHas=0;
		@Override
		public boolean acceptItem(Building source,Item item) {
			if(!block instanceof MultiCrafter) return false;
			if(items.get(item)>=getMaximumAccepted(item)) return false;
			return outputItemSet.contains(item);
		}
		@Override
		public boolean acceptLiquid(Building source,Liquid liquid, float amount) {
			if(!block instanceof MultiCrafter) return false;
			if(liquids.get(liquid)+amount>block.liquidCapacity) return false;
			return inputLiquidSet.contains(liquid);
		}
		@Override
		public int removeStack(Item item,int amount) {
			int ret=super.removeStack(item,amount);
			if(!items.has(item)&&items!=null) toOutputItemSet.remove(item);
			return ret;
		}
		@Override
		public void handleItem(Building source,Item item) {
			short current=toggle;
			//if((dumpToggle?cu))
		}
	}

	class ExtraBlockInventoryFragment extends BlockInventoryFragment{
		private boolean built = false;
		private boolean visible = false;

		public boolean isBuilt(){ return built; }

		public boolean isShown(){ return visible; }

		@Override
		public void showFor(Building t){
			visible = true;
			super.showFor(t);
		}

		@Override
		public void hide(){
			visible = false;
			super.hide();
		}

		@Override
		public void build(Group parent){
			built = true;
			super.build(parent);
		}
	}

	class MultiConsumePower extends ConsumePower{
		public float requestedPower(Building entity){
			/*
			 * if(entity.tile().build==null) return 0; 
			 * int i=entity.getToggle(); 
			 * if(i<0) return 0; 
			 * float input=recs[i].input.power; //if(input>0&&entity.cond) return input;
			 */
			return 0;
		}
	}
}
