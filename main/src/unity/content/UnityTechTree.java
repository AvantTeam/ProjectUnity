package unity.content;

import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.ctype.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.game.Objectives.Objective;

import static mindustry.type.ItemStack.*;
import static unity.content.UnityBlocks.*;
import static unity.content.UnitySectorPresets.*;

public class UnityTechTree implements ContentList{
    private static TechNode context = null;

    @Override
    public void load(){
        attachNode(Blocks.surgeSmelter, () -> {
            unityNode(darkAlloyForge);
            unityNode(monolithAlloyFactory);
        });
        attachNode(Blocks.powerNode, () -> {
            unityNode(lightLamp, () -> {
                unityNode(lightFilter, () -> {
                    unityNode(lightInvertedFilter, () -> {
                        unityNode(lightItemFilter);
                    });
                });
                unityNode(lightPanel);
                unityNode(lightReflector, () -> {
                    unityNode(lightDivisor, () -> {
                        unityNode(lightDivisor1, () -> {
                            unityNode(lightInfluencer);
                        });
                    });
                    unityNode(lightReflector1, () -> {
                        unityNode(lightOmnimirror);
                    });
                });
                unityNode(oilLamp);
            });
        });
        attachNode(Blocks.scorch, () -> {
            unityNode(inferno);
        });
        attachNode(Blocks.arc, () -> {
            unityNode(mage, () -> {
                unityNode(oracle);
            });
        });
        attachNode(Blocks.lancer, () -> {
            unityNode(laserTurret);
        });
        attachNode(Blocks.ripple, () -> {
            unityNode(shielder);
        });
        attachNode(Blocks.cyclone, () -> {
            unityNode(orb);
        });
        attachNode(Blocks.meltdown, () -> {
            unityNode(shockwire, () -> {
                unityNode(current, () -> {
                    unityNode(plasma);
                });
            });
        });
        attachNode(Blocks.copperWall, () -> {
            unityNode(metaglassWall, () -> {
                unityNode(metaglassWall);
            });
        });
        attachNode(Items.surgeAlloy, () -> {
            unityNode(UnityItems.umbrium, with(Items.surgeAlloy, 7000, Items.silicon, 8500, Items.graphite, 6000), () -> {});
        });
        attachNode(Blocks.coreShard, () -> {
            unityNode(accretion, () -> {});
        });
    }

    private static void attachNode(UnlockableContent parent, Runnable children){
        TechNode parnode = TechTree.all.find(t -> t.content == parent);
        context = parnode;
        children.run();
    }

    private static void unityNode(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;
        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void unityNode(UnlockableContent content, ItemStack[] requirements, Runnable children){
        unityNode(content, requirements, null, children);
    }

    private static void unityNode(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        unityNode(content, content.researchRequirements(), objectives, children);
    }

    private static void unityNode(UnlockableContent content, Runnable children){
        unityNode(content, content.researchRequirements(), children);
    }

    private static void unityNode(UnlockableContent block){
        unityNode(block, () -> {});
    }
}
