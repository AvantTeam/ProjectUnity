package unity.type;

import arc.Core;
import arc.graphics.g2d.*;
import mindustry.type.*;

public class UnderworldBlock {
    public String name, localizedName;
    public ItemStack[] cost;
    public int buildTime;
    public TextureRegion region;

    public UnderworldBlock(String name, ItemStack[] cost, int buildTime) {
        this.name = name;
        this.cost = cost;
        this.buildTime = buildTime;
        region = Core.atlas.find("unity-" + name);
        localizedName = Core.bundle.get("block." + name + ".name");
    }
}
