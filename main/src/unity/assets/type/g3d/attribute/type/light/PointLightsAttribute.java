package unity.assets.type.g3d.attribute.type.light;

import arc.struct.*;
import unity.assets.type.g3d.attribute.*;
import unity.assets.type.g3d.attribute.light.*;

public class PointLightsAttribute extends Attribute{
    public final static String lightAlias = "pointLights";
    public final static long light = register(lightAlias);

    public static boolean is(long mask){
        return (mask & light) == mask;
    }

    public final Seq<PointLight> lights;

    public PointLightsAttribute(){
        super(light);
        lights = new Seq<>(1);
    }

    public PointLightsAttribute(PointLightsAttribute copyFrom){
        this();
        lights.addAll(copyFrom.lights);
    }

    @Override
    public PointLightsAttribute copy(){
        return new PointLightsAttribute(this);
    }

    @Override
    public int hashCode(){
        int result = super.hashCode();
        for(PointLight light : lights) result = 1231 * result + (light == null ? 0 : light.hashCode());
        return result;
    }

    @Override
    public int compareTo(Attribute o){
        if(type != o.type) return type < o.type ? -1 : 1;
        return 0;
    }
}
