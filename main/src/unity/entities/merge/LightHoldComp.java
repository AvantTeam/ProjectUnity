package unity.entities.merge;

import arc.math.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

/**
 * A component that defines a type of block that holds "light lasers". These lasers acts like some type of consumer,
 * affects {@link Building#efficiency()} and {@link Building#consValid()}.
 * @author GlennFolker
 */
@SuppressWarnings("unused")
@MergeComponent
abstract class LightHoldComp extends Block implements Stemc{
    boolean requiresLight = true;
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

        /** Called in the asynchronous process. */
        public void addSource(Light light){
            sources.add(light);
        }

        /** Called in the asynchronous process. */
        public void removeSource(Light light){
            sources.remove(light);
        }

        public boolean acceptLight(Light light){
            return requiresLight;
        }

        public float lightf(){
            return sources.sumf(Light::strength) / requiredLight;
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
