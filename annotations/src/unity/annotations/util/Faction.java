package unity.annotations.util;

import arc.*;
import arc.graphics.*;
import mindustry.graphics.*;

public enum Faction{
    scar("scar", Pal.remove),
    imber("imber", Pal.surge),
    monolith("monolith", Color.teal),
    end("end", Pal.removeBack);

    public static final Faction[] all = values();

    public final String name;
    public final Color color;

    Faction(String name, Color color){
        this.name = Core.bundle.get("faction." + name, name);
        this.color = color.cpy();
    }
}
