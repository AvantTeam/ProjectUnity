package unity;

import arc.func.*;
import arc.graphics.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.gen.*;
import unity.content.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Main extends Mod{
	private final ContentList[] unityContent = {
		new UnityItems(),
		new UnityStatusEffects(),
		new UnityBullets(),
		new UnityUnitTypes(),
		new UnityBlocks(),
	};

	@Override
	public void init(){
		enableConsole = true;
		if(!headless) {
			LoadedMod mod=mods.locateMod("unity");
			String change="mod."+mod.meta.name+".";
			mod.meta.displayName=bundle.get(change+"name");
			mod.meta.description=bundle.get(change+"description");
		}
		// Log.log(LogLevel.info,"[@]: @",,);
	}

	@Override
	public void loadContent(){
		for (ContentList list : unityContent){
			list.load();
		}
		addResearch("graphite-press", UnityBlocks.multiTest1);
		//Log.log(LogLevel.info, "[@]: @", UnityBlocks.multiTest1.name, String.valueOf(UnityBlocks.multiTest1.getRecipe().output.items.length));
	}

	private void addResearch(String parentName, Block target){
		//TODO find more neat way to add block in techTree | candidates:json,loop
		Block parent = content.getByName(ContentType.block, parentName);
		TechNode baseNode = TechTree.all.contains(t -> t.content == target)
			? TechTree.all.find(t -> t.content == target)
			: TechTree.create(parent, target);
		TechNode parnode = TechTree.all.find(t -> t.content == parent);
		if (!parnode.children.contains(baseNode)) parnode.children.add(baseNode);
		baseNode.parent = parnode;
	}
}