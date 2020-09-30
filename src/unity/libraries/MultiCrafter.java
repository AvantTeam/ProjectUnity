package unity.libraries;

import arc.func.*;
import arc.util.*;
import arc.util.Log.*;
import arc.util.io.*;
import arc.struct.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.scene.ui.Button.*;
import arc.math.*;
import arc.math.geom.*;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Pal;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.ctype.ContentType;
import mindustry.type.*;
import mindustry.world.meta.*;
import mindustry.world.meta.values.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.modules.ConsumeModule;
import mindustry.ui.*;
import mindustry.ui.fragments.BlockInventoryFragment;
import unity.libraries.Recipe.*;
import java.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class MultiCrafter extends GenericCrafter{
	public final Recipe[] recs;
	private ButtonStyle infoStyle = null;
	public final ObjectSet<Liquid> liquidSet = new ObjectSet();
	private boolean hasOutputItem = false;
	public final ObjectSet<Item> inputItemSet = new ObjectSet();
	public final ObjectSet<Liquid> inputLiquidSet = new ObjectSet();
	public final ObjectSet<Item> outputItemSet = new ObjectSet();
	public final ObjectSet<Liquid> outputLiquidSet = new ObjectSet();
	public final boolean dumpToggle;
	private boolean powerBarI = false;
	private boolean powerBarO = false;
	private final ExtraBlockInventoryFragment invFrag = new ExtraBlockInventoryFragment();

	public MultiCrafter(String name, Recipe[] recs, boolean dumpToggle){
		super(name);
		this.recs = recs;
		this.dumpToggle = dumpToggle;
		configurable = true;
		hasItems = true;
		hasLiquids = true;
		hasPower = false;
		saveConfig = true;
		config(Integer.class, (MultiCrafterBuild tile, Integer value) -> {
			
			if (tile.getToggle() >= 0) tile.getProgressArr()[tile.getToggle()] = tile.progress;
			if (value == -1){
				tile.setCondValid(false);
				tile.setCond(false);
			}
			if (dumpToggle){
				tile.toOutputItemSet.clear();
				tile.toOutputLiquidSet.clear();
				if (value > -1){
					ItemStack[] oItems = recs[value].output.items;
					LiquidStack[] oLiquids = recs[value].output.liquids;
					for (int i = 0, len = oItems.length; i < len; i++){
						Item item = oItems[i].item;
						if (tile.items.has(item)) tile.toOutputItemSet.add(item);
					}
					for (int i = 0, len = oLiquids.length; i < len; i++){
						Liquid liquid = oLiquids[i].liquid;
						if (tile.liquids.get(liquid) > 0.001f) tile.toOutputLiquidSet.add(liquid);
					}
				}
			}
			tile.progress = 0;
			tile.setToggle(value);
		});
	}

	public MultiCrafter(String name, Recipe[] recs){
		this(name, recs, false);
	}

	public Recipe[] getRecipe(){ return recs; }

	@Override
	public void init(){
		for (int i = 0; i < recs.length; i++){
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
		for (int i = 0; i < recLen; i++){
			Recipe rec = recs[i];
			ItemStack[] inputItems = rec.input.items;
			ItemStack[] outputItems = rec.output.items;
			LiquidStack[] inputLiquids = rec.input.liquids;
			LiquidStack[] outputLiquids = rec.output.liquids;
			float inputPower = rec.input.power;
			float outputPower = rec.output.power;
			// what the fuck that I need this
			int ii = i;
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
		protected int toggle = 0;
		protected float[] progressArr = new float[recs.length];
		protected boolean cond = false;
		protected boolean condValid = false;
		public float productionEfficiency = 0f;
		public final OrderedSet<Item> toOutputItemSet = new OrderedSet();
		public final OrderedSet<Liquid> toOutputLiquidSet = new OrderedSet();
		protected int dumpItemEntry = 0;
		protected int itemHas = 0;

		public int getToggle(){ return toggle; }

		public void setToggle(int toggle){ this.toggle = toggle; }

		public boolean getCondValid(){ return condValid; }

		public void setCondValid(boolean condValid){ this.condValid = condValid; }

		public boolean getCond(){ return cond; }

		public void setCond(boolean cond){ this.cond = cond; }

		public float[] getProgressArr(){ return progressArr; }

		@Override
		public boolean acceptItem(Building source, Item item){
			if (!(block instanceof MultiCrafter)) return false;
			if (items.get(item) >= getMaximumAccepted(item)) return false;
			return inputItemSet.contains(item);
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid, float amount){
			if (!(block instanceof MultiCrafter)) return false;
			if (liquids.get(liquid) + amount > block.liquidCapacity) return false;
			return inputLiquidSet.contains(liquid);
		}

		@Override
		public int removeStack(Item item, int amount){
			int ret = super.removeStack(item, amount);
			if (!items.has(item) && items != null) toOutputItemSet.remove(item);
			return ret;
		}

		@Override
		public void handleItem(Building source, Item item){
			int current = toggle;
			if ((dumpToggle ? current > -1 && Arrays.stream(recs[current].output.items).anyMatch(i -> i.item == item)
				: outputItemSet.contains(item)) && !items.has(item)) toOutputItemSet.add(item);
			items.add(item, 1);
		}

		@Override
		public void handleStack(Item item, int amount, Teamc source){
			int current = toggle;
			if ((dumpToggle ? current > -1 && Arrays.stream(recs[current].output.items).anyMatch(i -> i.item == item)
				: outputItemSet.contains(item)) && !items.has(item)) toOutputItemSet.add(item);
			items.add(item, amount);
		}

		@Override
		public void displayConsumption(Table table){
			int recLen = recs.length;
			if (recLen <= 0) return;
			int z = 0, y = 0, x = 0;
			table.left();
			for (int i = 0; i < recLen; i++){
				ItemStack[] itemStacks = recs[i].input.items;
				LiquidStack[] liquidStacks = recs[i].input.liquids;
				if (Arrays.stream(recs[i].output.items).allMatch(stack -> stack.item.unlockedNow())
					&& Arrays.stream(recs[i].output.liquids).allMatch(stack -> stack.liquid.unlockedNow())){
					for (int j = 0, len = itemStacks.length; j < len; j++){
						ItemStack stack = itemStacks[j];
						table.add(new ReqImage(new ItemImage(stack.item.icon(Cicon.medium), stack.amount),
							() -> items != null && items.has(stack.item, stack.amount))).size(8 * 4);//.padRight(8);
					}
					z += itemStacks.length;
					for (int j = 0, len = liquidStacks.length; j < len; j++){
						LiquidStack stack = liquidStacks[j];
						table.add(new ReqImage(stack.liquid.icon(Cicon.medium),
							() -> liquids != null && liquids.get(stack.liquid) > stack.amount)).size(8 * 4);
					}
					z += liquidStacks.length;
					if (z == 0){
						table.image(Icon.cancel).size(8 * 4);
						x += 1;
					}
					if (i < recLen - 1){
						InputContents next = recs[i + 1].input;
						y += next.items.length + next.liquids.length;
						x += z;
						if (x + y <= 8 && y != 0){
							table.image(Icon.pause).size(8 * 4);
							x += 1;
						}else if (x + y <= 7 && y == 0){
							table.image(Icon.pause).size(8 * 4);
							x += 1;
						}else{
							table.row();
							x = 0;
						}
					}
					y = 0;
					z = 0;
				}
			}
		}

		@Override
		public float getPowerProduction(){
			if (toggle < 0 || recs.length <= 0) return 0;
			float oPower = recs[toggle].output.power;
			if (oPower > 0 && cond){
				if (recs[toggle].input.power > 0){
					productionEfficiency = efficiency();
					return oPower * efficiency();
				}else{
					productionEfficiency = 1;
					return oPower;
				}
			}
			productionEfficiency = 0;
			return 0;
		}

		@Override
		public float getProgressIncrease(float baseTime){
			if (toggle < 0) return 0f;
			else if (recs[toggle].input.power > 0) return super.getProgressIncrease(baseTime);
			else return 1f / baseTime * delta();
		}

		protected boolean checkInput(){
			if (toggle < 0) return false;
			ItemStack[] itemStacks = recs[toggle].input.items;
			LiquidStack[] liquidStacks = recs[toggle].input.liquids;
			if (!items.has(itemStacks)) return true;
			for (int i = 0, len = liquidStacks.length; i < len; i++){
				if (liquids.get(liquidStacks[i].liquid) < liquidStacks[i].amount) return true;
			}
			return false;
		}

		protected boolean checkOutput(){
			if (toggle < 0) return false;
			ItemStack[] itemStacks = recs[toggle].input.items;
			LiquidStack[] liquidStacks = recs[toggle].input.liquids;
			for (int i = 0, len = itemStacks.length; i < len; i++){
				if (items.get(itemStacks[i].item) + itemStacks[i].amount > getMaximumAccepted(itemStacks[i].item))
					return true;
			}
			for (int i = 0, len = liquidStacks.length; i < len; i++){
				if (liquids.get(liquidStacks[i].liquid) + liquidStacks[i].amount > liquidCapacity) return true;
			}
			return false;
		}

		protected boolean checkCond(){
			if (toggle < 0) return false;
			if (power.status <= 0 && recs[toggle].input.power > 0){
				condValid = false;
				cond = false;
				return false;
			}else if (checkInput()){
				condValid = false;
				cond = false;
				return false;
			}else if (checkOutput()){
				condValid = true;
				cond = false;
				return false;
			}
			condValid = true;
			cond = true;
			return true;
		}

		protected void customCons(){
			if (toggle < 0) return;
			if (checkCond()){
				if (progressArr[toggle] != 0){
					progress = progressArr[toggle];
					progressArr[toggle] = 0;
				}
				progress += getProgressIncrease(recs[toggle].craftTime);
				totalProgress += delta();
				warmup = Mathf.lerpDelta(warmup, 1, 0.02f);
				if (Mathf.chance(Time.delta * updateEffectChance))
					updateEffect.at(getX() + Mathf.range(size * 4), getY() + Mathf.range(size * 4));
			}else warmup = Mathf.lerp(warmup, 0, 0.02f);
		}

		protected void customProd(){
			if (toggle < 0) return;
			ItemStack[] inputItems = recs[toggle].input.items;
			LiquidStack[] inputLiquids = recs[toggle].input.liquids;
			ItemStack[] outputItems = recs[toggle].output.items;
			LiquidStack[] outputLiquids = recs[toggle].output.liquids;
			for (int i = 0, len = inputItems.length; i < len; i++) items.remove(inputItems[i]);
			for (int i = 0, len = inputLiquids.length; i < len; i++)
				liquids.remove(inputLiquids[i].liquid, inputLiquids[i].amount);
			for (int i = 0, len = outputItems.length; i < len; i++){
				for (int j = 0, amount = outputItems[i].amount; j < amount; j++){
					Item oItem = outputItems[j].item;
					if (!put(oItem)){
						if (!items.has(oItem)) toOutputItemSet.add(oItem);
						items.add(oItem, 1);
					}
				}
			}
			for (int i = 0, len = outputLiquids.length; i < len; i++){
				Liquid oLiquid = outputLiquids[i].liquid;
				if (liquids.get(oLiquid) <= 0.001) toOutputLiquidSet.add(oLiquid);
				liquids.add(oLiquid, outputLiquids[i].amount);
			}
			craftEffect.at(x, y);
			progress = 0;
		}

		@Override
		public void updateTile(){
			if (recs.length < 0) return;
			if (timer.get(1, 6)){
				itemHas = 0;
				items.each((item, amount) -> itemHas++);
			}
			int recLen = recs.length;
			customUpdate();
			if (toggle >= 0){
				customCons();
				if (progress >= 1) customProd();
			}
			if (dumpToggle && toggle < 0) return;
			Seq<Item> que = toOutputItemSet.orderedItems();
			int len = que.size, i = 0;
			if (timer.get(dumpTime) && len > 0){
				for (; i < len; i++){
					Item candidate = que.get((i + dumpItemEntry) % len);
					if (dump(candidate)){
						if (items.has(candidate)) toOutputItemSet.remove(candidate);
						break;
					}
				}
				if (i != len) dumpItemEntry = (i + dumpItemEntry) % len;
			}
			Seq<Liquid> queL = toOutputLiquidSet.orderedItems();
			len = queL.size;
			if (len > 0){
				for (i = 0; i < len; i++){
					Liquid liquid = queL.get(i);
					dumpLiquid(liquid);
					if (liquids.get(liquid) <= 0.001f) toOutputLiquidSet.remove(liquid);
					break;
				}
			}
		}

		public void customUpdate(){
		}

		@Override
		public boolean shouldConsume(){
			return condValid && productionValid();
		}

		@Override
		public boolean productionValid(){
			return cond && enabled;
		}

		@Override
		public void updateTableAlign(Table table){
			float pos = input.mouseScreen(x, y - size * 4 - 1).y;
			Vec2 relative = input.mouseScreen(x, y + size * 4);
			table.setPosition(relative.x,
				Math.min(pos, (float) (relative.y - Math.ceil((float) itemHas / 3f) * 48f - 4f)), Align.top);
			if (!invFrag.isShown() && control.input.frag.config.getSelectedTile() == this && items.total() > 0)
				invFrag.showFor(this);
		}

		@Override
		public void buildConfiguration(Table table){
			if (recs.length <= 0) return;
			if (!invFrag.isBuilt()) invFrag.build(table.parent);
			if (invFrag.isShown()){
				invFrag.hide();
				control.input.frag.config.hideConfig();
				return;
			}
			ButtonGroup group = new ButtonGroup();
			group.setMinCheckCount(0);
			group.setMaxCheckCount(1);
			int recLen = recs.length;
			boolean[] exit = new boolean[recLen];
			for (int i = 0; i < recLen; i++){
				int ii = i; //what the fuck
				OutputContents output = recs[i].output;
				exit[i] = !(Arrays.stream(output.items).allMatch(stack -> stack.item.unlockedNow())
					&& Arrays.stream(output.liquids).allMatch(stack -> stack.liquid.unlockedNow()));
				if (exit[i]) continue;
				ImageButton button = (ImageButton) table.button(Tex.whiteui, Styles.clearToggleTransi, 40, () -> {})
					.group(group).get();
				button.clicked(() -> configure(button.isChecked() ? ii : -1));
				TextureRegion icon = output.items.length > 0 ? output.items[0].item.icon(Cicon.small)
					: output.liquids.length > 0 ? output.liquids[0].liquid.icon(Cicon.small) : region;
				button.getStyle().imageUp = region == icon ? output.power > 0 ? Icon.power : Icon.cancel
					: new TextureRegionDrawable(icon);
				button.update(() -> button.setChecked(toggle == ii));
			}
			table.row();
			int[][] lengths = new int[recLen][3];
			for (int i = 0; i < recLen; i++){
				OutputContents output = recs[i].output;
				int outputItemLen = output.items.length;
				int outputLiquidLen = output.liquids.length;
				if (outputItemLen > 0) lengths[i][0] = outputItemLen - 1;
				if (outputLiquidLen > 0){
					if (outputItemLen > 0) lengths[i][1] = outputLiquidLen;
					else lengths[i][1] = outputLiquidLen - 1;
				}
				if (output.power > 0) lengths[i][2] = 1;
			}
			int max = 0;
			for (int i = 0; i < recLen; i++){
				int temp = lengths[i][0] + lengths[i][1] + lengths[i][2];
				max = max < temp ? temp : max;
			}
			for (int i = 0; i < max; i++){
				for (int j = 0; j < recLen; j++){
					if (exit[j]) continue;
					OutputContents output = recs[j].output;
					int outputItemLen = output.items.length;
					int outputLiquidLen = output.liquids.length;
					if (lengths[j][0] > 0){
						table.image(output.items[outputItemLen - lengths[j][0]].item.icon(Cicon.small));
						lengths[j][0]--;
					}else if (lengths[j][1] > 0){
						table.image(output.liquids[outputLiquidLen - lengths[j][1]].liquid.icon(Cicon.small));
						lengths[j][1]--;
					}else if (lengths[j][2] > 0){
						if (output.items.length >= 1 || output.liquids.length >= 1) table.image(Icon.power);
						else table.image(Tex.clear);
						lengths[j][2]--;
					}else table.image(Tex.clear);
				}
				table.row();
			}
		}

		@Override
		public boolean onConfigureTileTapped(Building other){
			if (self() != other) invFrag.hide();
			return items.total() > 0 ? true : self() != other;
		}

		@Override
		public void created(){
			cons = new ExtraConsumeModule(self());
		}

		@Override
		public Object config(){
			return toggle;
		}

		@Override
		public void write(Writes write){
			super.write(write);
			write.s(toggle);
			Seq<Item> queItem = toOutputItemSet.orderedItems();
			short lenI = (short) queItem.size;
			write.s(lenI);
			for (short i = 0; i < lenI; i++) write.s(queItem.get(i).id);
			Seq<Liquid> queLiquid = toOutputLiquidSet.orderedItems();
			short lenL = (short) queLiquid.size;
			write.s(lenL);
			for (short i = 0; i < lenL; i++) write.s(queLiquid.get(i).id);
		}

		@Override
		public void read(Reads read, byte revision){
			super.read(read, revision);
			toggle = read.s();
			toOutputItemSet.clear();
			toOutputLiquidSet.clear();
			short lenI = read.s();
			for (short i = 0; i < lenI; i++) toOutputItemSet.add(content.getByID(ContentType.item, read.s()));
			short lenL = read.s();
			for (short i = 0; i < lenL; i++) toOutputLiquidSet.add(content.getByID(ContentType.liquid, read.s()));
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
		public float requestedPower(MultiCrafterBuild entity){
			if (entity.tile().build == null) return 0;
			int i = entity.getToggle();
			if (i < 0) return 0;
			float input = recs[i].input.power;
			if (input > 0 && entity.getCond()) return input;
			return 0;
		}
	}

	class ExtraConsumeModule extends ConsumeModule{
		private final MultiCrafterBuild _entity;

		public ExtraConsumeModule(Building entity){
			super(entity);
			_entity = (MultiCrafterBuild) entity;
		}

		@Override
		public BlockStatus status(){
			if (_entity.productionValid()) return BlockStatus.active;
			if (_entity.getCondValid()) return BlockStatus.noOutput;
			return BlockStatus.noInput;
		}
	}
}
