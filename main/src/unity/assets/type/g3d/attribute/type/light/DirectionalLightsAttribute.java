package unity.assets.type.g3d.attribute.type.light;

import arc.struct.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;

public class DirectionalLightsAttribute extends Attribute{
    public static final String lightAlias = "directionalLights";
    public static final long light = register(lightAlias);

    public static boolean is(long mask){
        return (mask & light) == mask;
    }

    public final Seq<DirectionalLight> lights;

    public DirectionalLightsAttribute(){
        super(light);
        lights = new Seq<>(1);
    }

    public DirectionalLightsAttribute(DirectionalLightsAttribute copyFrom){
        this();
        lights.addAll(copyFrom.lights);
    }

    @Override
    public DirectionalLightsAttribute copy(){
        return new DirectionalLightsAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        for(DirectionalLight light : lights) result = 1229 * result + (light == null ? 0 : light.hashCode());
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type) return type < o.type ? -1 : 1;
        return 0;
    }
}
