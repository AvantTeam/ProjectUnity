package unity.mod;

import arc.struct.*;

public final class ContributorList{
    private static final IntMap<Seq<String>> contributors = new IntMap<>(ContributionType.all.length);

    public static void init(){
        for(ContributionType type : ContributionType.all){
            contributors.put(type.ordinal(), new Seq<>());
        }

        // DO NOT INSERT COLORS!
        contributors.get(ContributionType.collaborator.ordinal()).addAll(
            "Eldoofus",
            "Gdeft",
            "GlennFolker",
            "Goobrr",
            "JerichoFletcher",
            "sk7725",
            "ThePythonGuy3",
            "ThirstyBoi",
            "Txar",
            "Xelo",
            "Xusk",
            "younggam",
            "MEEP of Faith"
        );

        contributors.get(ContributionType.contributor.ordinal()).addAll(
            "Drullkus"
        );

        contributors.get(ContributionType.translator.ordinal()).addAll(
            "sk7725 (Korean)",
            "Xusk (Russian)"
        );
    }

    public static Seq<String> getBy(ContributionType type){
        return contributors.get(type.ordinal());
    }

    public enum ContributionType{
        collaborator,
        contributor,
        translator;

        public static ContributionType[] all = values();
    }
}
