package unity.entities.merge;

import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

import java.util.*;

/**
 * A component that defines a type of block that holds "light lasers". These lasers acts like some type of consumer,
 * affects {@link Building#efficiency()} and {@link Building#consValid()}.
 * @author GlennFolker
 */
@SuppressWarnings("unused")
@MergeComponent
abstract class LightHoldComp extends Block implements Stemc{
    boolean requiresLight = true;
    boolean acceptsLight = true;
    float requiredLight = 1f;

    public LightHoldComp(String name){
        super(name);
        update = true;
        sync = true;
        destructible = true;
    }

    /**
     * There are no direct {@link Light} definition here, as light holders are not necessarily meant to hold only
     * one light laser. Define these on inheriting classes.
     * @author GlennFolker
     */
    public class LightHoldBuildComp extends Building{
        /** {@link Light}s that point to this building. */
        @ReadOnly Seq<Light> sources = new Seq<>();

        public void addSource(Light light){
            sources.add(light);
            added(light);
        }

        public void removeSource(Light light){
            sources.remove(light);
            removed(light);
        }

        public void added(Light light){}

        public void removed(Light light){}

        public boolean acceptLight(Light light){
            return acceptsLight || requiresLight;
        }

        public float lightsum(){
            return sources.sumf(Light::endStrength);
        }

        public float lightf(){
            return lightsum() / requiredLight;
        }

        /** Note that this does not apply {@link Light#strength}! */
        public Light apply(Light from, Light to){
            to.relX = from.endX() - x;
            to.relY = from.endY() - y;
            to.set(this);
            to.source = self();

            return to;
        }

        @Override
        public void updateTile(){
            Iterator<Light> it = sources.iterator();
            while(it.hasNext()){
                Light next = it.next();
                if(next == null || !next.isAdded() || next.calcStrength(x, y) <= 0f){
                    it.remove();
                    removed(next);
                }
            }
        }

        @Override
        @Replace
        public float efficiency(){
            return super.efficiency() * (requiresLight ? lightf() : 1f);
        }

        @Override
        @Replace
        public boolean consValid(){
            return super.consValid() && (!requiresLight || !Mathf.zero(lightf()));
        }
    }
}
