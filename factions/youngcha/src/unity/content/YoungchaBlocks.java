package unity.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.content.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;
import unity.mod.*;
import unity.graphics.*;
import unity.world.blocks.environment.*;

import static unity.mod.FactionRegistry.register;

/**
 * Defines all {@linkplain Faction#youngcha youngcha} block types.
 * @author younggam, xelo
 */
public final class YoungchaBlocks{
    public static Block
    oreNickel, concreteBlank, concreteFill, concreteNumber, concreteStripe, concrete, stoneFullTiles, stoneFull,
    stoneHalf, stoneTiles, pit, waterpit, greySand, nickelGeode, greysandWall, concreteWall;

    private YoungchaBlocks(){
        throw new AssertionError();
    }

    public static void load(){
        oreNickel = register(Faction.youngcha, new OreBlock(YoungchaItems.nickel){{
            oreScale = 24.77f;
            oreThreshold = 0.913f;
            oreDefault = true;
        }});
        greySand = register(Faction.youngcha, new Floor("grey-sand"){{
            variants = 3;
            itemDrop = Items.sand;
            playerUnmineable = true;
        }});
        concreteBlank = register(Faction.youngcha, new Floor("concrete-blank"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteFill = register(Faction.youngcha, new Floor("concrete-fill"){{
            variants = 0;
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteNumber = register(Faction.youngcha, new Floor("concrete-number"){{
            variants = 10;
            attributes.set(Attribute.water, -0.85f);
        }});
        concreteStripe = register(Faction.youngcha, new Floor("concrete-stripe"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        concrete = register(Faction.youngcha, new Floor("concrete"){{
            attributes.set(Attribute.water, -0.85f);
        }});
        stoneFullTiles = register(Faction.youngcha, new Floor("stone-full-tiles"){{
            attributes.set(Attribute.water, -0.75f);
        }});
        stoneFull = register(Faction.youngcha, new Floor("stone-full"){{
            attributes.set(Attribute.water, -0.75f);
        }});
        stoneHalf = register(Faction.youngcha, new Floor("stone-half"){{
            attributes.set(Attribute.water, -0.5f);
        }});
        stoneTiles = register(Faction.youngcha, new Floor("stone-tiles"){{
            attributes.set(Attribute.water, -0.5f);
        }});
        pit = new Floor("pit"){
            {
                buildVisibility = BuildVisibility.editorOnly;
                cacheLayer = PUCacheLayer.pitLayer;
                placeableOn = false;
                solid = true;
                variants = 0;
                canShadow = false;
                mapColor = Color.black;
            }

            @Override
            public TextureRegion[] icons(){
                return new TextureRegion[]{Core.atlas.find(name + "-icon", name)};
            }
        };
        waterpit = new Floor("waterpit"){
            {
                buildVisibility = BuildVisibility.editorOnly;
                cacheLayer = PUCacheLayer.waterpitLayer;
                placeableOn = true;
                isLiquid = true;
                drownTime = 20f;
                speedMultiplier = 0.1f;
                liquidMultiplier = 2f;
                status = StatusEffects.wet;
                statusDuration = 120f;
                variants = 0;
                liquidDrop = Liquids.water;
                canShadow = false;
                mapColor = Liquids.water.color.cpy().lerp(Color.black, 0.5f);
            }

            @Override
            public TextureRegion[] icons(){
                return new TextureRegion[]{Core.atlas.find(name + "-icon", name)};
            }
        };
        nickelGeode = new LargeStaticWall("nickel-geode"){{
            variants = 2;
            itemDrop = YoungchaItems.nickel;
            maxsize = 3;
        }};
        greysandWall = new LargeStaticWall("grey-sand-wall"){{
            variants = 3;
            itemDrop = Items.sand;
            maxsize = 3;
        }};
        concreteWall = new ConnectedWall("concrete-wall"){
            {
                variants = 0;
            }

            @Override
            public TextureRegion[] icons(){
                return new TextureRegion[]{Core.atlas.find(name, name)};
            }
        };
    }
}
