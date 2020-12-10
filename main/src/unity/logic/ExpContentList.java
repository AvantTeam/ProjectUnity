package unity.logic;

public enum ExpContentList{
    totalExp("@totalExp"),
    totalLevel("@totalLevel"),
    expCapacity("@expCapacity"),
    maxLevel("@maxLevel");

    public final String name;

    public static final ExpContentList[] all = values();

    ExpContentList(String name){
        this.name = name;
    }
}
