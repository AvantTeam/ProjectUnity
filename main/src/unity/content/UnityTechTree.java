package unity.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static unity.content.blocks.DarkBlocks.*;
import static unity.content.blocks.LightBlocks.*;
import static unity.content.blocks.ImberBlocks.*;
import static unity.content.blocks.MonolithBlocks.*;
import static unity.content.blocks.UnityBlocks.*;
import static unity.content.UnityItems.*;
import static unity.content.UnityUnitTypes.*;

@SuppressWarnings("all")
public class UnityTechTree{
    private static TechNode context = null;

    public static void load(){
        //region blocks

        attach(Blocks.surgeSmelter, () -> {
            node(darkAlloyForge);
            node(monolithAlloyForge);
            node(sparkAlloyForge, Seq.with(new Research(sparkAlloy)), () -> {
                node(orb, () -> {
                    node(shielder);
                    node(shockwire, () -> {
                        node(current, () -> {
                            node(plasma, () -> {
                                node(electrobomb);
                            });
                        });
                    });
                });
            });
        });

        attach(Blocks.powerNode, () -> {
            node(lightLamp, () -> {
                node(lightReflector, () -> {
                    node(lightDivisor);
                });

                node(oilLamp);
            });
        });

        attach(Blocks.arc, () -> {
            node(diviner, Seq.with(new Research(monolite)), () -> {
                node(mage, () -> {
                    node(heatRay, () -> {
                        node(incandescence);
                    });

                    node(oracle, Seq.with(new Research(monolithAlloy)));
                });

                node(recluse, () -> {
                    node(blackout);
                });
            });

            node(ricochet, Seq.with(new Research(monolite)), () -> {
                node(shellshock, Seq.with(new Research(monolithAlloy)), () -> {
                    node(purge);
                });

                node(lifeStealer, () -> {
                    node(absorberAura);
                });
            });
        });

        attach(Blocks.titaniumWall, () -> {
            node(metaglassWall, () -> {
                node(metaglassWallLarge);
            });

            node(electrophobicWall, Seq.with(new Research(monolite)), () -> {
                node(electrophobicWallLarge);
            });
        });

        attach(Blocks.siliconCrucible, () -> {
            node(irradiator, Seq.with(new Research(Items.thorium), new Research(Items.titanium), new Research(Items.surgeAlloy)));
        });

        attach(Blocks.overdriveProjector, () -> {
            node(superCharger, Seq.with(new Research(irradiator)));
        });

        attach(Blocks.surgeTower, () -> {
            node(absorber, Seq.with(new Research(sparkAlloyForge)));
        });

        //end region
        //region units

        attach(UnitTypes.fortress, () -> {
            node(stele, () -> {
                node(pedestal, () -> {
                    node(pilaster, () -> {
                        node(pylon, () -> {
                            node(monument, () -> {
                                node(colossus, () -> {
                                    node(bastion);
                                });
                            });
                        });
                    });
                });

                node(adsect, () -> {
                    node(comitate);
                });
            });
        });

        //end region
        //region items

        attach(Items.lead, () -> {
            nodeProduce(nickel);
        });

        attach(Items.graphite, () -> {
            nodeProduce(monolite);

            nodeProduce(stone, () -> {
                nodeProduce(denseAlloy, () -> {
                    nodeProduce(steel, () -> {
                        //nodeProduce(uranium);
                        nodeProduce(UnityLiquids.lava, () -> {
                            nodeProduce(dirium);
                        });
                    });
                });
            });
        });

        attach(Items.thorium, () -> {
            nodeProduce(archDebris, Seq.with(new Research(monolite)), () -> {
                nodeProduce(monolithAlloy);
            });
        });

        attach(Items.surgeAlloy, () -> {
            nodeProduce(imberium, () -> {
                nodeProduce(sparkAlloy);

                nodeProduce(irradiantSurge);
            });
        });
    }

    private static void attach(UnlockableContent parent, Runnable children){
        context = TechTree.get(parent);
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives){
        node(content, content.researchRequirements(), objectives, () -> {});

    }

    @SuppressWarnings("unused")
    private static void node(UnlockableContent content, ItemStack[] requirements){
        node(content, requirements, null, () -> {});
    }

    @SuppressWarnings("unused")
    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives){
        node(content, requirements, objectives, () -> {});
    }

    private static void node(UnlockableContent content){
        node(content, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.and(new Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }
}