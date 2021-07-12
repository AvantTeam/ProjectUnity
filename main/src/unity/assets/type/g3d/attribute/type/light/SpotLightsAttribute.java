package unity.assets.type.g3d.attribute.type.light;

import arc.struct.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;

public class SpotLightsAttribute extends Attribute{
    public final static String lightAlias = "spotLights";
    public final static long light = register(lightAlias);

    public static boolean is(long mask){
        return (mask & light) == mask;
    }

    public final Seq<SpotLight> lights;

    public SpotLightsAttribute(){
        super(light);
        lights = new Seq<>(1);
    }

    public SpotLightsAttribute(SpotLightsAttribute copyFrom){
        this();
        lights.addAll(copyFrom.lights);
    }

    @Override
    public SpotLightsAttribute copy(){
        return new SpotLightsAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        for(SpotLight light : lights) result = 1237 * result + (light == null ? 0 : light.hashCode());
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type) return type < o.type ? -1 : 1;
        return 0;
    }
}
