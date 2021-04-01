package unity.world.blocks.environment;

import mindustry.type.*;
import mindustry.world.blocks.environment.*;

public class UnityOreBlock extends OreBlock{
    public UnityOreBlock(Item ore){
        super(ore.name.replaceFirst("unity-", ""));
        useColor = true;

        setup(ore);
    }
}
