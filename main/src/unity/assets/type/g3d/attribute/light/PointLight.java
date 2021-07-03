package unity.assets.type.g3d.attribute.light;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;

public class PointLight extends BaseLight<PointLight>{
    public final Vec3 position = new Vec3();
    public float intensity;

    public PointLight setPosition(float positionX, float positionY, float positionZ){
        position.set(positionX, positionY, positionZ);
        return this;
    }

    public PointLight setPosition(Vec3 position){
        this.position.set(position);
        return this;
    }

    public PointLight setIntensity(float intensity){
        this.intensity = intensity;
        return this;
    }

    public PointLight set(PointLight copyFrom){
        return set(copyFrom.color, copyFrom.position, copyFrom.intensity);
    }

    public PointLight set(Color color, Vec3 position, float intensity){
        if(color != null) this.color.set(color);
        if(position != null) this.position.set(position);
        this.intensity = intensity;
        return this;
    }

    public PointLight set(float r, float g, float b, Vec3 position, float intensity){
        this.color.set(r, g, b, 1f);
        if(position != null) this.position.set(position);
        this.intensity = intensity;
        return this;
    }

    public PointLight set(Color color, float x, float y, float z, float intensity){
        if(color != null) this.color.set(color);
        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }

    public PointLight set(float r, float g, float b, float x, float y, float z, float intensity){
        this.color.set(r, g, b, 1f);
        this.position.set(x, y, z);
        this.intensity = intensity;
        return this;
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof PointLight light) && equals(light);
    }

    public boolean equals(PointLight other){
        return other != null && (other == this || (color.equals(other.color) && position.equals(other.position) && Mathf.equal(intensity, other.intensity)));
    }
}
