package unity.content;

import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.ctype.*;
import mindustry.content.*;
import mindustry.content.TechTree.*;
import mindustry.game.Objectives.Objective;

import static mindustry.type.ItemStack.*;
import static unity.content.UnityBlocks.*;

public class UnityTechTree implements ContentList{
	private static TechNode context = null;

	@Override
	public void load(){
		attachNode(Blocks.surgeSmelter, darkAlloyForge);
		attachNode(Blocks.powerNode, lightLamp, () -> {
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
		attachNode(Blocks.scorch, inferno);
		attachNode(Blocks.lancer, laserTurret);
		attachNode(Blocks.copperWall, metaglassWall, () -> {
			unityNode(metaglassWallLarge);
		});
		attachNode(Items.surgealloy, UnityItems.umbrium, with(Items.surgealloy, 7000, Items.silicon, 8500, Items.graphite, 6000), () -> {

		});
	}

	private static void attachNode(UnlockableContent parent, UnlockableContent joint, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
		TechNode parnode = TechTree.all.find(t -> t.content == parent);
		TechNode node = new TechNode(parnode, joint, requirements);
		if(objectives != null) node.objectives = objectives;
		context = parnode;
		children.run();
	}

	private static void attachNode(UnlockableContent parent, UnlockableContent joint, Seq<Objective> objectives, Runnable children){
		attachNode(parent, joint, joint.researchRequirements(), objectives, children);
	}

	private static void attachNode(UnlockableContent parent, UnlockableContent joint, ItemStack[] requirements, Runnable children){
		attachNode(parent, joint, requirements, null, children);
	}

	private static void attachNode(UnlockableContent parent, UnlockableContent joint, Runnable children){
		attachNode(parent, joint, joint.researchRequirements(), children);
	}

	private static void attachNode(UnlockableContent parent, UnlockableContent block){
		attachNode(parent, block, () -> {});
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
