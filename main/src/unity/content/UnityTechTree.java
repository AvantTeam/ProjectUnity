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

        attachNode(Blocks.surgeSmelter, () -> {
            node(darkAlloyForge);
            node(monolithAlloyForge);
            node(sparkAlloyForge, () -> {
               node(orb, () -> {
                    node(shielder);
                    node(shockwire, () -> {
                        node(current, () -> {
                            node(plasma);
                        });
                    });
                });
            });
        });

        attachNode(Blocks.powerNode, () -> {
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

        attachNode(Blocks.arc, () -> {
            node(mage, () -> {
                node(oracle);
            });
        });

        attachNode(Blocks.titaniumWall, () -> {
            node(metaglassWall, () -> {
                node(metaglassWallLarge);
            });

            node(electrophobicWall, Seq.with(new Research(UnityItems.monolite)), () -> {
                node(electrophobicWallLarge);
            });
        });

        //end region
        //region items

        attachNode(Items.lead, () -> {
            nodeProduce(UnityItems.nickel);
        });

        attachNode(Items.graphite, () -> {
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
    }

    private static void attachNode(UnlockableContent parent, Runnable children){
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

    private static void node(UnlockableContent block){
        node(block, () -> {});
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
