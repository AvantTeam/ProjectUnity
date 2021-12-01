package unity.util;

import arc.func.*;
import arc.util.pooling.*;

public class BasicPool<T> extends Pool<T>{
    Prov<T> prov;

    public BasicPool(int initialCapacity, Prov<T> prov){
        super(initialCapacity, 5000);
        this.prov = prov;
    }

    public BasicPool(int initialCapacity, int max, Prov<T> prov){
        super(initialCapacity, max);
        this.prov = prov;
    }

    @Override
    protected T newObject(){
        return prov.get();
    }
}
