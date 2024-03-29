package unity.content;

import arc.graphics.*;
import mindustry.content.*;
import mindustry.type.*;
import unity.graphics.*;
import unity.type.*;
import unity.world.meta.*;
import unity.world.meta.CrucibleRecipe.*;

public class UnityItems{
    public static Item
    //faction-alloys
    advanceAlloy, cupronickel, darkAlloy, dirium, lightAlloy, monolithAlloy, archDebris, plagueAlloy, sparkAlloy, superAlloy, terminaAlloy,
    terminationFragment, terminum,
    //faction items
    contagium, denseAlloy, imberium, irradiantSurge, luminum, monolite, nickel, steel, stone, umbrium, xenium, uranium;

    public static void load(){
        //region faction-alloys

        advanceAlloy = new Item("advance-alloy", Color.valueOf("748096")){{
            cost = 1.4f;
            radioactivity = 0.1f;
        }};

        cupronickel = new Item("cupronickel", Color.valueOf("a19975")){{
            cost = 2f;
        }};

        darkAlloy = new Item("dark-alloy", Color.valueOf("716264")){{
            cost = 1.4f;
            radioactivity = 0.11f;
        }};

        dirium = new Item("dirium", Color.valueOf("96f7c3")){{
            cost = 0.3f;
            hardness = 9;
        }};

        lightAlloy = new Item("light-alloy", Color.valueOf("e0ecee")){{
            cost = 1.4f;
            radioactivity = 0.08f;
        }};

        monolithAlloy = new AnimatedItem("monolith-alloy", UnityPal.monolithLight){{
            cost = 1.4f;
            flammability = 0.1f;
            radioactivity = 0.12f;
            frames = 14;
            frameTime = 1f;
            transitionFrames = 3;
        }};

        archDebris = new AnimatedItem("archaic-debris", UnityPal.monolith){{
            cost = 1.3f;
            radioactivity = 0.1f;
            frames = 7;
            frameTime = 3f;
            transitionFrames = 1;
        }};

        plagueAlloy = new Item("plague-alloy", Color.valueOf("6a766a")){{
            cost = 1.4f;
            radioactivity = 0.16f;
        }};

        sparkAlloy = new Item("spark-alloy", Color.valueOf("f4ff61")){{
            cost = 1.3f;
            radioactivity = 0.01f;
            explosiveness = 0.1f;
        }};

        superAlloy = new Item("super-alloy", Color.valueOf("67a8a0")){{
            cost = 2.5f;
        }};

        terminaAlloy = new Item("termina-alloy", Color.valueOf("9e6d74")){{
            cost = 4.2f;
            radioactivity = 1.74f;
        }};

        terminationFragment = new Item("termination-fragment", Color.valueOf("f9504f")){{
            cost = 1.2f;
            radioactivity = 3.64f;
        }};

        terminum = new Item("terminum", Color.valueOf("f53036")){{
            cost = 3.2f;
            radioactivity = 1.32f;
        }};

        //endregion
        //region faction items;

        contagium = new Item("contagium", Color.valueOf("68985e")){{
            cost = 1.5f;
            hardness = 3;
            radioactivity = 0.7f;
        }};

        denseAlloy = new Item("dense-alloy", Color.valueOf("a68a84")){{
            hardness = 2;
            cost = 2f;
        }};

        imberium = new Item("imberium", Color.valueOf("f6ff7d")){{
            cost = 1.4f;
            hardness = 3;
            radioactivity = 0.6f;
        }};

        irradiantSurge = new AnimatedItem("irradiant-surge", Color.valueOf("3d423e")){{
            cost = 2f;
            frames = 2;
            frameTime = 3f;
            transitionFrames = 30;
        }};

        luminum = new Item("luminum", Color.valueOf("e9eaf1")){{
            cost = 1.2f;
            hardness = 3;
            radioactivity = 0.1f;
        }};

        monolite = new Item("monolite", UnityPal.monolithDark){{
            cost = 1.5f;
            hardness = 3;
            radioactivity = 0.2f;
            flammability = 0.2f;
        }};

        nickel = new Item("nickel", Color.valueOf("6e9675")){{
            hardness = 3;
            cost = 2.5f;
        }};

        steel = new Item("steel", Color.valueOf("e1e3ed")){{
            hardness = 4;
            cost = 2.5f;
        }};

        stone = new Item("stone", Color.valueOf("8a8a8a")){{
            hardness = 1;
            cost = 0.4f;
            lowPriority = true;
        }};

        umbrium = new Item("umbrium", Color.valueOf("8c3d3b")){{
            cost = 1.2f;
            hardness = 3;
            radioactivity = 0.2f;
        }};

        xenium = new Item("xenium", Color.valueOf("9dddff")){{
            cost = 1.2f;
            hardness = 3;
            radioactivity = 0.6f;
        }};

        uranium = new Item("uranium", Color.valueOf("ace284")){{
            cost = 2f;
            hardness = 3;
            radioactivity = 1f;
        }};

        //endregion
        //region meta

        MeltInfo meltCopper = new MeltInfo(Items.copper, 750f, 0.1f, 0.02f, 2100f, 1);
        MeltInfo meltLead = new MeltInfo(Items.lead, 570f, 0.2f, 0.02f, 1900f, 1);
        MeltInfo meltTitanium = new MeltInfo(Items.titanium, 1600f, 0.07f, 1);
        MeltInfo meltSand = new MeltInfo(Items.sand, 1000f, 0.25f, 1);
        MeltInfo carbon = new MeltInfo("carbon", 4000f, 0.01f, 0.01f, 600f, 0);
        new MeltInfo(Items.coal, carbon, 0.5f, 0, true);
        new MeltInfo(Items.graphite, carbon, 1f, 0, true);
        MeltInfo meltNickel = new MeltInfo(nickel, 1100f, 0.15f, 1);
        MeltInfo meltCuproNickel = new MeltInfo(cupronickel, 850f, 0.05f, 2);
        MeltInfo meltMetaglass = new MeltInfo(Items.metaglass, 950f, 0.05f, 2);
        MeltInfo meltSilicon = new MeltInfo(Items.silicon, 900f, 0.2f, 2);
        MeltInfo meltSurgeAlloy = new MeltInfo(Items.surgeAlloy, 1500f, 0.05f, 3);
        MeltInfo meltThorium = new MeltInfo(Items.thorium, 1650f, 0.03f, 1);
        MeltInfo meltSuperAlloy = new MeltInfo(superAlloy, 1800f, 0.02f, 4);

        new CrucibleRecipe(meltCuproNickel, 0.6f, new InputRecipe(meltNickel, 0.8f, false), new InputRecipe(meltCopper, 2f));
        new CrucibleRecipe(meltSilicon, 0.25f, new InputRecipe(meltSand, 1.25f), new InputRecipe(carbon, 0.25f, false));
        new CrucibleRecipe(meltMetaglass, 0.5f, new InputRecipe(meltSand, 1f / 3f), new InputRecipe(meltLead, 1f / 3f));
        new CrucibleRecipe(meltSurgeAlloy, 0.25f, new InputRecipe(meltSilicon, 1f), new InputRecipe(meltLead, 2f), new InputRecipe(meltCopper, 1f), new InputRecipe(meltTitanium, 1.5f));
        new CrucibleRecipe(meltSuperAlloy, 0.2f, new InputRecipe(meltCuproNickel, 1f), new InputRecipe(meltSilicon, 1f), new InputRecipe(meltThorium, 1f), new InputRecipe(meltTitanium, 1f));

        //endregion
    }
}
