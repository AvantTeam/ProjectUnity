package unity.content;

import arc.graphics.*;
import mindustry.ctype.*;
import mindustry.type.*;

public class UnityItems implements ContentList{
	public static Item
	
	//faction items
	advanceAlloy, lightAlloy, darkAlloy, monolithAlloy, plagueAlloy, sparkAlloy, terminum, terminaAlloy, terminationFragment,
	xenium, luminum, umbrium, monolite, contagium, imberium;
	
	@Override
	public void load(){
		advanceAlloy = new Item("advance-alloy", Color.valueOf("748096")){{
			cost = 1.4f;
			radioactivity = 0.1f;
		}};
		
		lightAlloy = new Item("light-alloy", Color.valueOf("e0ecee")){{
			cost = 1.4f;
			radioactivity = 0.08f;
		}};
		
		darkAlloy = new Item("dark-alloy", Color.valueOf("716264")){{
			cost = 1.4f;
			radioactivity = 0.11f;
		}};
		
		monolithAlloy = new Item("monolith-alloy", Color.valueOf("6586b0")){{
			cost = 1.4f;
			flammability = 0.1f;
			radioactivity = 0.12f;
		}};
		
		plagueAlloy = new Item("plague-alloy", Color.valueOf("6a766a")){{
			cost = 1.4f;
			radioactivity = 0.16f;
		}};
		
		sparkAlloy = new Item("spark-alloy", Color.valueOf("f4ff61")){{
			cost = 1.4f;
			radioactivity = 0.01f;
			explosiveness = 0.1f;
		}};
		
		terminum = new Item("terminum", Color.valueOf("f53036")){{
			cost = 3.2f;
			radioactivity = 1.32f;
		}};
		
		terminaAlloy = new Item("termina-alloy", Color.valueOf("9e6d74")){{
			cost = 4.2f;
			radioactivity = 1.74f;
		}};
		
		terminationFragment = new Item("termination-fragment", Color.valueOf("f9504f")){{
			cost = 1.2f;
			radioactivity = 3.64f;
		}};
		
		xenium = new Item("xenium", Color.valueOf("9dddff")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.6f;
		}};
		
		luminum = new Item("luminum", Color.valueOf("e9eaf1")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.1f;
		}};
		
		umbrium = new Item("umbrium", Color.valueOf("8c3d3b")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.2f;
		}};
		
		monolite = new Item("monolite", Color.valueOf("87ceeb")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.2f;
			flammability = 0.2f;
		}};
		
		contagium = new Item("contagium", Color.valueOf("68985e")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.7f;
		}};
		
		imberium = new Item("imberium", Color.valueOf("f6ff7d")){{
			cost = 1.2f;
			hardness = 3;
			radioactivity = 0.6f;
		}};
	}
}
