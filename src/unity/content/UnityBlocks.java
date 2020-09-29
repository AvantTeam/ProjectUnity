package unity.content;

import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.environment.*;
import mindustry.ctype.*;

import static unity.content.UnityItems.*;

public class UnityBlocks implements ContentList{
	public static Block
	
	//faction ores
	/*oreXenium, */oreUmbrium, oreLuminum, oreMonolite, oreImberium;
	
	@Override
	public void load(){
		oreUmbrium = new OreBlock(xenium){{
			oreScale = 23.77f;
			oreThreshold = 0.813f;
			oreDefault = true;
		}};
		
		oreLuminum = new OreBlock(luminum){{
			oreScale = 23.77f;
			oreThreshold = 0.81f;
			oreDefault = true;
		}};
		
		oreMonolite = new OreBlock(monolite){{
			oreScale = 23.77f;
			oreThreshold = 0.807f;
			oreDefault = true;
		}};
		
		oreImberium = new OreBlock(imberium){{
			oreScale = 23.77f;
			oreThreshold = 0.807f;
			oreDefault = true;
		}};
	}
}
