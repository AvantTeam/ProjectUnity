package unity.entities.merge;

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
    boolean acceptsLight = true;

    float requiredLight = 1f;

    public LightHoldComp(String name){
        super(name);
        update = true;
        sync = true;
        destructible = true;
    }

    public float getRotation(Building build){
        return 0f;
    }

    public abstract class LightHoldBuildComp extends Building implements StemBuildc{
        /** Don't modify directly, it's just here to store references to lights pointing this building */
        transient Seq<Light> sources = new Seq<>();

        public boolean acceptLight(Light light){
            return acceptsLight;
        }

        /** Called synchronously when a light ray touches this building */
        public void interact(Light light){}

        public float lightStatus(){
            return sources.sumf(Light::endStrength);
        }

        public float lightf(){
            return lightStatus() / requiredLight;
        }

        @Override
        @Replace
        public float efficiency(){
            float eff = super.efficiency();
            return eff * (requiresLight ? Math.min(lightf(), 1f) : 1f);
        }
    }
}
