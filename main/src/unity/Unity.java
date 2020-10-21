package unity;

import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.ctype.*;
import unity.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Unity extends Mod{
	private final ContentList[] unityContent = {
		new UnityItems(),
		new UnityStatusEffects(),
		new UnityBullets(),
		new UnityUnitTypes(),
		new UnityBlocks(),
		new UnityPlanets(),
		new UnityTechTree(),
	};

	@Override
	public void init(){
		enableConsole = true;
		if(!headless){
			LoadedMod mod = mods.locateMod("unity");
			String change= "mod." + mod.meta.name + ".";
			mod.meta.displayName = bundle.get(change + "name");
			mod.meta.description = bundle.get(change + "description");
		}
		// Log.log(LogLevel.info,"[@]: @",,);
	}

	@Override
	public void loadContent(){
		for(ContentList list : unityContent){
			list.load();
		}
		//Log.log(LogLevel.info, "[@]: @", UnityBlocks.multiTest1.name, String.valueOf(UnityBlocks.multiTest1.getRecipe().output.items.length));
	}
}