package unity.mod;

import arc.*;
import arc.graphics.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;

@FactionBase
public enum Faction{
    scar("scar", Pal.remove),
    dark("dark", Color.valueOf("fc6203")),
    advance("advance", Color.sky),
    imber("imber", Pal.surge),
    plague("plague", Color.valueOf("a3f080")),
    koruh("koruh", Color.valueOf("61caff")),
    light("light", Color.valueOf("fffde8")),
    monolith("monolith", Color.teal),
    youngcha("youngcha", Color.valueOf("a69f95")),
    end("end", Pal.removeBack);

    public static final Faction[] all = values();

    public final String name;
    public final Color color;

    Faction(String name, Color color){
        this.name = Core.bundle.get("faction." + name, name);
        this.color = color.cpy();
    }
}
