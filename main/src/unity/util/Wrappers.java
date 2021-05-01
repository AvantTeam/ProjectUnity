package unity.util;

/*
 * For passing into closures.
 * */
public class Wrappers{
    public static class NumberWrapper<T extends Number>{
        public T val;
        public NumberWrapper(){
            reset();
        }
        public NumberWrapper(T val){
            this.val = val;
        }
        public NumberWrapper(NumberWrapper<T> val){
            this.val = val.val;
        }
        public void reset(){
            val = (T)(Integer.valueOf(0));
        }
    }
    public static class ObjectWrapper<T>{
        public T val;
        public ObjectWrapper(){
            reset();
        }
        public ObjectWrapper(T val){
            this.val = val;
        }
        public ObjectWrapper(ObjectWrapper<T> val){
            this.val = val.val;
        }
        public void reset(){
            val = null;
        }
    }

}
