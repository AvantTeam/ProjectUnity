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
            "MEEP of Faith"
        ),

        ContributionType.contributor, Seq.with(
            "Drullkus",
            "Anuke",
            "ThePythonGuy3",
            "ThirstyBoi",
            "Xusk",
            "Eldoofus",
            "Gdeft",
            "Goobrr"
        ),

        ContributionType.translator, Seq.with(
            "sk7725 (Korean)",
            "Xusk (Russian)"
        )
    );

    public static Seq<String> getBy(ContributionType type){
        return contributors.get(type);
    }

    public enum ContributionType{
        collaborator,
        contributor,
        translator;

        public static ContributionType[] all = values();
    }
}
