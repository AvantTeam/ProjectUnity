package unity.util;

import arc.func.*;
import arc.util.pooling.*;

public class BasicPool<T> extends Pool<T>{
    Prov<T> prov;

    public BasicPool(Prov<T> p){
        prov = p;
    }

    @Override
    protected T newObject(){
        return prov.get();
    }
}
