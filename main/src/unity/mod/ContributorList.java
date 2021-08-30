package unity.mod;

import arc.struct.*;

public final class ContributorList{
    private static final ObjectMap<ContributionType, Seq<String>> contributors = ObjectMap.of(
        // DO NOT INSERT COLORS!
        ContributionType.collaborator, Seq.with(
            "GlennFolker",
            "JerichoFletcher",
            "sk7725",
            "Xelo",
            "younggam",
            "ThePythonGuy",
            "MEEP of Faith"
        ),

        ContributionType.contributor, Seq.with(
            "Drullkus",
            "Anuke",
            "ThirstyBoi",
            "Xusk",
            "Eldoofus",
            "Evl",
            "Goober",
            "BasedUser",
            "Sharlotte"
        ),

        ContributionType.translator, Seq.with(
            "sk7725 (Korean)",
            "Xusk (Russian)"
        ),

        ContributionType.tester, Seq.with(
            "BlueWolf"
        )
    );

    public static final ObjectMap<String, String> githubAliases = ObjectMap.of(
            "Xelo", "XeloBoyo",
            "MEEP of Faith", "MEEPofFaith",
            "Anuke", "Anuken",
            "ThePythonGuy", "ThePythonGuy3",
            "Xusk", "Xusk947",
            "Goober", "Goobrr",
            "Sharlotte", "Sharlottes"
    );

    public static Seq<String> getBy(ContributionType type){
        return contributors.get(type);
    }

    public enum ContributionType{
        collaborator,
        contributor,
        translator,
        tester;

        public static ContributionType[] all = values();
    }
}
