package unity.entities.merge;

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

    public abstract class LightHoldBuildComp extends Building implements StemBuildc{
        public boolean acceptLight(Light light){
            return true;
        }
    }
}
