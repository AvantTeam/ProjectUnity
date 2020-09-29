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
import unity.content.*;
import java.awt.event.ItemEvent;

import static mindustry.type.ItemStack.*;

public class Main extends Mod{
	private ContentList[] content = {
		new UnityItems()
	};
	
	@Override
	public void init(){
		// Log.log(LogLevel.info,"[@]: @",,);
	}

	@Override
	public void loadContent(){
		for(ContentList list : content){
			list.load();
		}
		
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