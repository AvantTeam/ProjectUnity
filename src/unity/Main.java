package unity;

import arc.graphics.*;
import arc.util.*;
import arc.util.Log.*;
import mindustry.mod.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import unity.content.*;

import static mindustry.Vars.*;

public class Main extends Mod{
	private ContentList[] content = {
		new UnityItems(),
		new UnityBlocks()
	};
	
	@Override
	public void init(){
		enableConsole = true;
		// Log.log(LogLevel.info,"[@]: @",,);
	}

	@Override
	public void loadContent(){
		for (ContentList list : content){
			list.load();
		}
		//Log.log(LogLevel.info, "[@]: @", UnityBlocks.multiTest1.name, String.valueOf(UnityBlocks.multiTest1.getRecipe().output.items.length));
	}
}