package unity.util;

/** @author GlennFolker */
public class AtomicPair<K, V>{
    public volatile K key = null;
    public volatile V value = null;

    public void reset(){
        key = null;
        value = null;
    }
}
