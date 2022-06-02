package unity.entities.type;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.graphics.*;

/**
 * Base implementation of {@link PUUnitTypeCommon}.
 * @author GlennFolker
 */
@SuppressWarnings("unchecked")
public class PUUnitType extends PUUnitTypeCommon{
    /** The common properties of this unit type, mapped by its class. */
    public final OrderedMap<Class<? extends Props>, Props> properties = new OrderedMap<>();

    /** The trail constructor, as a support for custom trails. */
    public Func<Unit, Trail> trailType = unit -> new Trail(trailLength);

    public PUUnitType(String name){
        super(name);
        properties.orderedKeys().ordered = false;
    }

    public <T extends Unit> void trail(int trailLength, Func<T, Trail> trailType){
        this.trailLength = trailLength;
        this.trailType = (Func<Unit, Trail>)trailType;
    }

    @Override
    public void drawTrail(Unit unit){
        if(unit.trail == null) unit.trail = trailType.get(unit);
        super.drawTrail(unit);
    }

    @Override
    public OrderedMap<Class<? extends Props>, Props> properties(){
        return properties;
    }

    @Override
    public void prop(Props prop){
        Class<? extends Props> type = prop.getClass();
        if(type.isAnonymousClass()) type = (Class<? extends Props>)type.getSuperclass();

        properties.put(type, prop);
    }

    @Override
    public <T extends Props> T prop(Class<T> type){
        return (T)properties.get(type);
    }

    @Override
    public <T extends Props> boolean hasProp(Class<T> type){
        return properties.containsKey(type);
    }

    @Override
    public void init(){
        for(Props prop : properties.values()) prop.preInit();
        super.init();
        for(Props prop : properties.values()) prop.init();
    }

    @Override
    public void load(){
        for(Props prop : properties.values()) prop.preLoad();
        super.load();
        for(Props prop : properties.values()) prop.load();
    }
}
