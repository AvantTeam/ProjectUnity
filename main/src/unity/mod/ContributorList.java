package unity.mod;

import arc.struct.*;

public final class ContributorList{
    private static IntMap<Seq<String>> contributors = new IntMap<>(ContributionType.all.length);

    public static void init(){
        for(ContributionType type : ContributionType.all){
            contributors.put(type.ordinal(), new Seq<>());
        }

        /** DO NOT INSERT COLORS! */
        contributors.get(ContributionType.collaborator.ordinal()).addAll(
        "sk7725",
        "younggam",
        "GlennFolker",
        "Gdeft",
        "Xusk",
        "ThePythonGuy");
        contributors.get(ContributionType.contributor.ordinal()).addAll(
        /** will always be empty.. */
        );
        contributors.get(ContributionType.translator.ordinal()).addAll(
        "sk7725 (Korean)",
        "Xusk (Russian)");
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
