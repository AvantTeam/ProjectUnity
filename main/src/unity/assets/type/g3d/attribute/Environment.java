package unity.assets.type.g3d.attribute;

import arc.struct.*;
import unity.assets.type.g3d.attribute.light.*;
import unity.assets.type.g3d.attribute.type.light.*;

/** Only add lights before rendering anything. */
public class Environment extends Attributes{
    public ShadowMap shadowMap;

    public Environment add(BaseLight<?>... lights){
        for(var light : lights) add(light);
        return this;
    }

    public Environment add(Seq<BaseLight<?>> lights){
        for(var light : lights) add(light);
        return this;
    }

    public Environment add(BaseLight<?> light){
        if(light instanceof DirectionalLight l){
            add(l);
        }else if(light instanceof PointLight l){
            add(l);
        }else if(light instanceof SpotLight l){
            add(l);
        }else{
            throw new IllegalArgumentException("Unknown light type");
        }

        return this;
    }

    public Environment add(DirectionalLight light){
        DirectionalLightsAttribute dirLights = get(DirectionalLightsAttribute.light);
        if(dirLights == null){
            set(dirLights = new DirectionalLightsAttribute());
        }

        dirLights.lights.add(light);
        return this;
    }

    public Environment add(PointLight light){
        PointLightsAttribute pointLights = get(PointLightsAttribute.light);
        if(pointLights == null){
            set(pointLights = new PointLightsAttribute());
        }

        pointLights.lights.add(light);
        return this;
    }

    public Environment add(SpotLight light){
        SpotLightsAttribute spotLights = get(SpotLightsAttribute.light);
        if(spotLights == null){
            set(spotLights = new SpotLightsAttribute());
        }

        spotLights.lights.add(light);
        return this;
    }

    public Environment remove(BaseLight<?>... lights){
        for(var light : lights) remove(light);
        return this;
    }

    public Environment remove(Seq<BaseLight<?>> lights){
        for(var light : lights) remove(light);
        return this;
    }

    public Environment remove(BaseLight<?> light){
        if(light instanceof DirectionalLight l){
            remove(l);
        }else if(light instanceof PointLight l){
            remove(l);
        }else if(light instanceof SpotLight l){
            remove(l);
        }else{
            throw new IllegalArgumentException("Unknown light type");
        }

        return this;
    }

    public Environment remove(DirectionalLight light){
        if(has(DirectionalLightsAttribute.light)){
            DirectionalLightsAttribute dirLights = get(DirectionalLightsAttribute.light);
            dirLights.lights.remove(light, false);
            if(dirLights.lights.size == 0) remove(DirectionalLightsAttribute.light);
        }
        return this;
    }

    public Environment remove(PointLight light){
        if(has(PointLightsAttribute.light)){
            PointLightsAttribute pointLights = get(PointLightsAttribute.light);
            pointLights.lights.remove(light, false);
            if(pointLights.lights.size == 0) remove(PointLightsAttribute.light);
        }
        return this;
    }

    public Environment remove(SpotLight light){
        if(has(SpotLightsAttribute.light)){
            SpotLightsAttribute spotLights = get(SpotLightsAttribute.light);
            spotLights.lights.remove(light, false);
            if(spotLights.lights.size == 0) remove(SpotLightsAttribute.light);
        }
        return this;
    }
}
