package unity;

import arc.struct.*;

public final class ContributorList{
    private static IntMap<Seq<String>> contributors = new IntMap<>(ContributionType.all.length);
    private static IntMap<Seq<String>> translators = new IntMap<>(Language.all.length);

    static void init(){
        for(ContributionType type : ContributionType.all){
            contributors.put(type.ordinal(), new Seq<>());
        }
        for(Language lang : Language.all){
            translators.put(lang.ordinal(), new Seq<>());
        }

        contributors.get(ContributionType.founder.ordinal()).addAll(
            "EyeOfDarkness"
        );
        contributors.get(ContributionType.collaborator.ordinal()).addAll(
            "[pink]sk7725",
            "younggam",
            "[cyan]GlennFolker",
            "evl",
            "Xusk",
            "[#77dd77]ThePythonGuy"
        );
        contributors.get(ContributionType.contributor.ordinal()).addAll(
            //noone here yet
        );
        contributors.get(ContributionType.translator.ordinal()).addAll(
            "sk7725",
            "Xusk"
        );

        translators.get(Language.korean.ordinal()).addAll(
            "sk7725"
        );
        translators.get(Language.russian.ordinal()).addAll(
            "Xusk"
        );
    }

    public static Seq<String> getBy(ContributionType type){
        return contributors.get(type.ordinal());
    }

    public static Seq<String> getBy(Language lang){
        return translators.get(lang.ordinal());
    }

    public enum ContributionType{
        founder,
        collaborator,
        contributor,
        translator;

        public static ContributionType[] all = values();
    }

    public enum Language{
        korean,
        russian;

        public static Language[] all = values();
    }
}
