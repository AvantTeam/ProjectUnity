package unity;

import arc.graphics.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.mod.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import unity.libraries.*;
import unity.libraries.Recipe.*;

import static mindustry.type.ItemStack.*;

import java.awt.event.ItemEvent;

public class Main extends Mod{
	public void init(){
		// Log.log(LogLevel.info,"[@]: @",,);
	}

	public void loadContent(){
		// faction-alloys
		Item advanceAlloy = new Item("advance-alloy", Color.valueOf("748096"));
		advanceAlloy.cost = 1.4f;
		advanceAlloy.radioactivity = 0.1f;
		Item darkAlloy = new Item("dark-alloy", Color.valueOf("716264"));
		darkAlloy.cost = 1.4f;
		darkAlloy.radioactivity = 0.11f;
		Item lightAlloy = new Item("light-alloy", Color.valueOf("e0ecee"));
		lightAlloy.cost = 1.4f;
		lightAlloy.radioactivity = 0.08f;
		Item monolithAlloy = new Item("monolith-alloy", Color.valueOf("6586b0"));
		monolithAlloy.cost = 1.4f;
		monolithAlloy.flammability = 0.1f;
		monolithAlloy.radioactivity = 0.12f;
		Item plagueAlloy = new Item("plague-alloy", Color.valueOf("6a766a"));
		plagueAlloy.cost = 1.4f;
		plagueAlloy.radioactivity = 0.16f;
		Item sparkAlloy = new Item("spark-alloy", Color.valueOf("f4ff61"));
		sparkAlloy.cost = 1.3f;
		sparkAlloy.radioactivity = 0.01f;
		sparkAlloy.explosiveness = 0.1f;
		Item terminum = new Item("terminum", Color.valueOf("f53036"));
		terminum.cost = 3.2f;
		terminum.radioactivity = 1.32f;
		// normal
		Item contagium = new Item("contagium", Color.valueOf("68985e"));
		terminum.radioactivity = 0.7f;
		terminum.hardness = 3;
		terminum.cost = 1.5f;
		Item imberium = new Item("imberium", Color.valueOf("f6ff7d"));
		imberium.radioactivity = 0.6f;
		imberium.hardness = 3;
		imberium.cost = 1.4f;
		Item luminum = new Item("luminum", Color.valueOf("e9eaf1"));
		luminum.radioactivity = 0.1f;
		luminum.hardness = 3;
		luminum.cost = 1.2f;
		Item monolite = new Item("monolite", Color.valueOf("87ceeb"));
		monolite.radioactivity = 0.2f;
		monolite.flammability = 0.2f;
		monolite.hardness = 3;
		monolite.cost = 1.5f;
		Item umbrium = new Item("umbrium", Color.valueOf("8c3d3b"));
		umbrium.radioactivity = 0.2f;
		umbrium.hardness = 3;
		umbrium.cost = 1.42f;
		Item xenium = new Item("xenium", Color.valueOf("9dddff"));
		xenium.radioactivity = 0.6f;
		xenium.hardness = 2;
		xenium.cost = 0.8f;
		MultiCrafter a = new MultiCrafter("multi-test-1",
			new Recipe[]{new Recipe(new InputContents(), new OutputContents(5.25f), 12),
				new Recipe(
					new InputContents(with(Items.coal, 1, Items.sand, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(new LiquidStack[]{new LiquidStack(Liquids.slag, 5)}), 60),
				new Recipe(
					new InputContents(with(Items.pyratite, 1, Items.blastCompound, 1),
						new LiquidStack[]{new LiquidStack(Liquids.water, 5)}, 1),
					new OutputContents(with(Items.scrap, 1, Items.plastanium, 2, Items.sporePod, 2),
						new LiquidStack[]{new LiquidStack(Liquids.oil, 5)}),
					72),}){
			{

			}
		};
		Log.log(LogLevel.info, "[@]: @", a.name, String.valueOf(a.recs[1].output.items.length));
	}
}