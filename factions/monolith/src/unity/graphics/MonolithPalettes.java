package unity.graphics;

import arc.graphics.*;
import unity.mod.*;

/**
 * {@linkplain Faction#monolith Monolith}-specific palettes.
 * @author GlennFolker
 */
public final class MonolithPalettes{
    public static final Color
        monolith = Palettes.monolith,
        monolithLight = new Color(0xc0ecffff),
        monolithDark = new Color(0x6586b0ff),

        monolithOutline = Palettes.darkOutline;

    private MonolithPalettes(){
        throw new AssertionError();
    }
}
