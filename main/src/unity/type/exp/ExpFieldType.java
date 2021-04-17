package unity.type.exp;

/**
 * @author GlennFolker
 * @author sk7725
 */
public enum ExpFieldType{
    linear,
    exp,
    root,
    bool,
    list;

    public static final ExpFieldType[] all = values();
}
