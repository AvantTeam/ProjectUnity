package unity.content;

import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.ctype.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.game.Objectives.*;

import static mindustry.type.ItemStack.*;
import static unity.content.UnityBlocks.*;
import static unity.content.UnitySectorPresets.*;

@SuppressWarnings("unused")
public class UnityTechTree implements ContentList{
    private static TechNode context = null;

    @Override
    public void load(){
        //region blocks
        attachNode(Blocks.surgeSmelter, () -> {
            node(darkAlloyForge);
            node(monolithAlloyForge);
            node(sparkAlloyForge);
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
        /*attachNode(Blocks.scorch, () -> {
            node(inferno);
        });TODO*/
        attachNode(Blocks.arc, () -> {
            node(mage, () -> {
                node(oracle);
            });
        });
        /*attachNode(Blocks.lancer, () -> {
            node(laserTurret);
        });TODO*/
        attachNode(Blocks.ripple, () -> {
            node(shielder);
        });
        attachNode(Blocks.cyclone, () -> {
            node(orb);
        });
        attachNode(Blocks.meltdown, () -> {
            node(shockwire, () -> {
                node(current, () -> {
                    node(plasma);
                });
            });
        });
        attachNode(Blocks.copperWall, () -> {
            node(metaglassWall, () -> {
                node(metaglassWall);
            });
        });
        //endregion
        //region items
        attachNode(Items.lead, () -> {
            nodeProduce(UnityItems.nickel, () -> {});
        });
        attachNode(Items.graphite, ()->{
            nodeProduce(UnityItems.stone, ()->{
                nodeProduce(UnityItems.denseAlloy, ()->{
                    nodeProduce(UnityItems.steel, ()->{
                        nodeProduce(UnityItems.dirium, ()->{});
                    });
                });
            });
        });
        //endregion
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
        nodeProduce(content, new Seq<>(), children);
    }
}
