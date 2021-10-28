package unity.content.blocks;

import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.content.*;
import unity.world.blocks.effect.*;
import unity.world.blocks.production.*;
import unity.world.blocks.units.*;

import static mindustry.Vars.*;
import static mindustry.type.ItemStack.*;

public class UnityBlocks{
    //---------- global ----------
    public static Block
    //production
    distributionDrill,

    //unit
    recursiveReconstructor,

    //crafting
    irradiator,

    //defense
    superCharger;

    private static final ContentList[] factionBlocks = {
        new DarkBlocks(),
        new LightBlocks(),
        new ImberBlocks(),
        new KoruhBlocks(),
        new MonolithBlocks(),
        new YoungchaBlocks(),
        new AdvanceBlocks(),
        new EndBlocks()
    };

    public static void load(){
        //region global

        distributionDrill = new DistributionDrill("distribution-drill"){{
            requirements(Category.production, with(Items.copper, 20, Items.silicon, 15, Items.titanium, 20));
            tier = 3;
            drillTime = 450;
            size = 2;

            consumes.liquid(Liquids.water, 0.06f).boost();
        }};

        recursiveReconstructor = new SelectableReconstructor("recursive-reconstructor"){{
            requirements(Category.units, with(Items.graphite, 1600, Items.silicon, 2000, Items.metaglass, 900, Items.thorium, 600, Items.lead, 1200, Items.plastanium, 3600));
            size = 11;
            liquidCapacity = 360f;
            configurable = true;
            constructTime = 20000f;
            minTier = 6;
            upgrades.addAll(
                new UnitType[]{UnitTypes.reign, UnityUnitTypes.citadel},

                new UnitType[]{UnitTypes.toxopid, UnityUnitTypes.araneidae},

                new UnitType[]{UnitTypes.corvus, UnityUnitTypes.cygnus},

                new UnitType[]{UnityUnitTypes.rex, UnityUnitTypes.excelsus},

                new UnitType[]{UnityUnitTypes.monument, UnityUnitTypes.colossus}
            );
            otherUpgrades.add(
                new UnitType[]{UnityUnitTypes.citadel, UnityUnitTypes.empire},

                new UnitType[]{UnityUnitTypes.araneidae, UnityUnitTypes.theraphosidae},

                new UnitType[]{UnityUnitTypes.colossus, UnityUnitTypes.bastion}
            );
            consumes.power(5f);
            consumes.items(with(Items.silicon, 1200, Items.metaglass, 800, Items.thorium, 700, Items.surgeAlloy, 400, Items.plastanium, 600, Items.phaseFabric, 350));
            consumes.liquid(Liquids.cryofluid, 7f);
        }};

        irradiator = new Press("irradiator"){{
            requirements(Category.crafting, with(Items.lead, 120, Items.silicon, 80, Items.titanium, 30));
            outputItem = new ItemStack(UnityItems.irradiantSurge, 3);
            size = 3;
            movementSize = 29f;
            fxYVariation = 25f / tilesize;
            craftTime = 50f;
            consumes.power(1.2f);
            consumes.items(with(Items.thorium, 5, Items.titanium, 5, Items.surgeAlloy, 1));
        }};

        superCharger = new Reinforcer("supercharger"){{
            requirements(Category.effect, with(Items.titanium, 60, Items.lead, 20, Items.silicon, 30));
            size = 2;
            itemCapacity = 15;
            laserColor = Items.surgeAlloy.color;
            consumes.power(0.4f);
            consumes.items(with(UnityItems.irradiantSurge, 10));
        }};

        //endregion
        //region Faction Blocks

        for(ContentList faction : factionBlocks){
            faction.load();
        }

        //endregion
    }
}