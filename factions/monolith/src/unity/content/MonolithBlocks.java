package unity.content;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import unity.mod.*;

import static unity.content.MonolithAttributes.*;
import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} block types.
 * @author GlennFolker
 */
public final class MonolithBlocks{
    public static Block
    erodedSlate, infusedErodedSlate,
    erodedSlateWall, infusedErodedSlateWall;

    private MonolithBlocks(){
        throw new AssertionError();
    }

    public static void load(){
        erodedSlate = register(Faction.monolith, new Floor("eroded-slate"){{
            attributes.set(intermediateInfusion, 0.1f);
        }});

        erodedSlateWall = register(Faction.monolith, new StaticWall("eroded-slate-wall"){{
            attributes.set(intermediateInfusion, 0.1f);
            erodedSlate.asFloor().wall = this;
        }});

        infusedErodedSlate = register(Faction.monolith, new Floor("infused-eroded-slate"){{
            attributes.set(intermediateInfusion, 0.4f);
        }});

        infusedErodedSlateWall = register(Faction.monolith, new StaticWall("infused-eroded-slate-wall"){{
            attributes.set(intermediateInfusion, 0.4f);
            infusedErodedSlate.asFloor().wall = this;
        }});
    }
}
