package unity.annotations.util;

import arc.*;
import arc.graphics.*;

public enum Faction{
    monolith("monolith", Color.teal.cpy());

    public final String name;
    public final Color color;

    Faction(String name, Color color){
        this.name = Core.bundle.get("faction." + name, name);
        this.color = color.cpy();
    }
}
