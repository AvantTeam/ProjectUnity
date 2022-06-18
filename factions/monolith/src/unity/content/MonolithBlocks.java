package unity.content;

import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import unity.mod.*;
import unity.world.blocks.environment.*;

import static unity.content.MonolithAttributes.*;
import static unity.mod.FactionRegistry.*;

/**
 * Defines all {@linkplain Faction#monolith monolith} block types.
 * @author GlennFolker
 */
public final class MonolithBlocks{
    public static Block
    erodedSlate, infusedErodedSlate, archaicErodedSlate, sharpSlate, infusedSharpSlate, archaicSharpSlate,
    erodedEneraphyteVent, eneraphyteVent,

    erodedSlateWall, infusedErodedSlateWall, archaicErodedSlateWall, sharpSlateWall, infusedSharpSlateWall, archaicSharpSlateWall;

    private MonolithBlocks(){
        throw new AssertionError();
    }

    public static void load(){
        erodedSlate = register(Faction.monolith, new Floor("eroded-slate"){{
            attributes.set(eneraphyteInfusion, 0.08f);
        }});

        infusedErodedSlate = register(Faction.monolith, new Floor("infused-eroded-slate"){{
            attributes.set(eneraphyteInfusion, 0.24f);
        }});

        archaicErodedSlate = register(Faction.monolith, new Floor("archaic-eroded-slate"){{
            attributes.set(eneraphyteInfusion, 0.65f);
        }});

        sharpSlate = register(Faction.monolith, new Floor("sharp-slate"){{
            attributes.set(eneraphyteInfusion, 0.15f);
        }});

        infusedSharpSlate = register(Faction.monolith, new Floor("infused-sharp-slate"){{
            attributes.set(eneraphyteInfusion, 0.4f);
        }});

        archaicSharpSlate = register(Faction.monolith, new Floor("archaic-sharp-slate"){{
            attributes.set(eneraphyteInfusion, 1f);
        }});

        erodedEneraphyteVent = register(Faction.monolith, new SmallVent("eroded-eneraphyte-vent"){{
            attributes.set(eneraphyteEmission, 0.75f);
            attributes.set(eneraphyteInfusion, 0.24f);

            parent = blendGroup = infusedErodedSlate;
            effect = MonolithFx.erodedEneraphyteSteam;
        }});

        eneraphyteVent = register(Faction.monolith, new SmallVent("eneraphyte-vent"){{
            attributes.set(eneraphyteEmission, 1f);
            attributes.set(eneraphyteInfusion, 0.4f);

            parent = blendGroup = infusedSharpSlate;
            effect = MonolithFx.eneraphyteSteam;
            effectSpacing = 20f;
        }});

        erodedSlateWall = register(Faction.monolith, new StaticWall("eroded-slate-wall"){{
            attributes.set(eneraphyteInfusion, 0.08f);
            erodedSlate.asFloor().wall = this;
        }});

        infusedErodedSlateWall = register(Faction.monolith, new StaticWall("infused-eroded-slate-wall"){{
            attributes.set(eneraphyteInfusion, 0.24f);
            infusedErodedSlate.asFloor().wall = this;
        }});

        archaicErodedSlateWall = register(Faction.monolith, new StaticWall("archaic-eroded-slate-wall"){{
            attributes.set(eneraphyteInfusion, 0.65f);
            archaicErodedSlate.asFloor().wall = this;
        }});

        sharpSlateWall = register(Faction.monolith, new StaticWall("sharp-slate-wall"){{
            attributes.set(eneraphyteInfusion, 0.15f);
            sharpSlate.asFloor().wall = this;
        }});

        infusedSharpSlateWall = register(Faction.monolith, new StaticWall("infused-sharp-slate-wall"){{
            attributes.set(eneraphyteInfusion, 0.4f);
            infusedSharpSlate.asFloor().wall = this;
        }});

        archaicSharpSlateWall = register(Faction.monolith, new StaticWall("archaic-sharp-slate-wall"){{
            attributes.set(eneraphyteInfusion, 1f);
            archaicSharpSlate.asFloor().wall = this;
        }});
    }
}
