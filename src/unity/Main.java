package unity;

import arc.func.*;
import arc.graphics.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.mod.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.gen.*;
import unity.content.*;

import static mindustry.Vars.*;

public class Main extends Mod{
	private final ContentList[] unityContent = {
		new UnityItems(),
		new UnityBlocks(),
		new UnityUnitTypes()
	};

	@Override
	public void init(){
		enableConsole = true;
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