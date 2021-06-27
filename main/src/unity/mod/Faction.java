package unity.mod;

import arc.*;
import arc.graphics.*;
import mindustry.game.EventType.*;
import mindustry.graphics.*;
import unity.annotations.Annotations.*;
import unity.graphics.*;

/** @author GlennFolker */
@FactionBase
public enum Faction{
    scar("scar", Pal.remove),
    dark("dark", Color.valueOf("fc6203")),
    advance("advance", Color.sky),
    imber("imber", Pal.surge),
    plague("plague", Color.valueOf("a3f080")),
    koruh("koruh", Color.valueOf("61caff")),
    light("light", Color.valueOf("fffde8")),
    monolith("monolith", UnityPal.monolith),
    youngcha("youngcha", Color.valueOf("a69f95")),
    end("end", Color.gray);

    public static final Faction[] all = values();

    public final String name;
    public String localizedName;

    public final Color color;

    static{
        Events.on(ClientLoadEvent.class, e -> {
            for(Faction faction : all){
                faction.localizedName = Core.bundle.format("faction." + faction.name, faction.color);
            }
        });
    }

    Faction(String name, Color color){
        this.name = name;
        this.color = color.cpy();
    }
}
