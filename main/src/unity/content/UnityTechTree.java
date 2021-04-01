package unity.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static unity.content.UnityBlocks.*;

public class UnityTechTree implements ContentList{
    private static TechNode context = null;

    @Override
    public void load(){
        //region blocks

        attach(Blocks.surgeSmelter, () -> {
            node(darkAlloyForge);
            node(monolithAlloyForge);
            node(sparkAlloyForge, Seq.with(new Research(UnityItems.sparkAlloy)), () -> {
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
                node(lightFilter, () -> {
                    node(lightInvertedFilter, () -> {
                        node(lightItemFilter);
                    });
                });

                node(lightPanel);
                node(lightReflector, () -> {
                    node(lightDivisor, () -> {
                        node(lightDivisor1, () -> {
                            node(lightInfluencer);
                        });
                    });

                    node(lightReflector1, () -> {
                        node(lightOmnimirror);
                    });
                });

                node(oilLamp);
            });
        });

        attach(Blocks.arc, () -> {
            node(recluse, () -> {
                node(mage, () -> {
                    node(oracle);
                });
            });

            node(ricochet, () -> {
                node(shellshock, () -> {
                    node(purge);
                });
            });
        });

        attach(Blocks.titaniumWall, () -> {
            node(metaglassWall, () -> {
                node(metaglassWallLarge);
            });

            node(electrophobicWall, Seq.with(new Research(UnityItems.monolite)), () -> {
                node(electrophobicWallLarge);
            });
        });

        //end region
        //region items

        attach(Items.lead, () -> {
            nodeProduce(UnityItems.nickel);
        });

        attach(Items.graphite, () -> {
            nodeProduce(UnityItems.stone, () -> {
                nodeProduce(UnityItems.denseAlloy, () -> {
                    nodeProduce(UnityItems.steel, () -> {
                        //nodeProduce(UnityItems.uranium);
                        nodeProduce(UnityLiquids.lava, () -> {
                            nodeProduce(UnityItems.dirium);
                        });
                    });
                });
            });
        });

        attach(Items.surgeAlloy, () -> {
            nodeProduce(UnityItems.imberium, () -> {
                nodeProduce(UnityItems.sparkAlloy);
            });
        });

        attach(Blocks.siliconCrucible, () -> {
            node(energyMixer, Seq.with(new Research(Items.thorium), new Research(Items.titanium), new Research(Items.surgeAlloy)));
        });

        attach(Blocks.overdriveProjector, () -> {
            node(energyzer, Seq.with(new Research(energyMixer)));
        });
    }

    private static void attach(UnlockableContent parent, Runnable children){
        TechNode parnode = TechTree.all.find(t -> t.content == parent);
        context = parnode;
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

    private static void node(UnlockableContent content, ItemStack[] requirements){
        node(content, requirements, null, () -> {});
    }

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
