package unity.libraries;

import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.production.GenericCrafter;
import unity.libraries.Recipe;

public class MultiCrafter extends GenericCrafter{
	public final Recipe[] recs;

	public MultiCrafter(String name, Recipe[] recs){
		super(name);
		this.recs = recs;
	}
}
