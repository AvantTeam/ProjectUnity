package unity.assets.type.g3d.attribute.light;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;

public class SpotLight extends BaseLight<SpotLight>{
    public final Vec3 position = new Vec3();
    public final Vec3 direction = new Vec3();
    public float intensity;
    public float cutoffAngle;
    public float exponent;

    public SpotLight setPosition(float positionX, float positionY, float positionZ){
        position.set(positionX, positionY, positionZ);
        return this;
    }

    public SpotLight setPosition(Vec3 position){
        this.position.set(position);
        return this;
    }

    public SpotLight setDirection(float directionX, float directionY, float directionZ){
        this.direction.set(directionX, directionY, directionZ);
        return this;
    }

    public SpotLight setDirection(Vec3 direction){
        this.direction.set(direction);
        return this;
    }

    public SpotLight setIntensity(float intensity){
        this.intensity = intensity;
        return this;
    }

    public SpotLight setCutoffAngle(float cutoffAngle){
        this.cutoffAngle = cutoffAngle;
        return this;
    }

    public SpotLight setExponent(float exponent){
        this.exponent = exponent;
        return this;
    }

    public SpotLight set(SpotLight copyFrom){
        return set(copyFrom.color, copyFrom.position, copyFrom.direction, copyFrom.intensity, copyFrom.cutoffAngle, copyFrom.exponent);
    }

    public SpotLight set(Color color, Vec3 position, Vec3 direction, float intensity, float cutoffAngle, float exponent){
        if(color != null) this.color.set(color);
        if(position != null) this.position.set(position);
        if(direction != null) this.direction.set(direction).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }

    public SpotLight set(float r, float g, float b, Vec3 position, Vec3 direction, float intensity, float cutoffAngle, float exponent){
        this.color.set(r, g, b, 1f);
        if(position != null) this.position.set(position);
        if(direction != null) this.direction.set(direction).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }

    public SpotLight set(Color color, float posX, float posY, float posZ, float dirX, float dirY, float dirZ, float intensity, float cutoffAngle, float exponent){
        if(color != null) this.color.set(color);
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }

    public SpotLight set(float r, float g, float b, float posX, float posY, float posZ, float dirX, float dirY, float dirZ, float intensity, float cutoffAngle, float exponent){
        this.color.set(r, g, b, 1f);
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity;
        this.cutoffAngle = cutoffAngle;
        this.exponent = exponent;
        return this;
    }

    public SpotLight setTarget(final Vec3 target){
        direction.set(target).sub(position).nor();
        return this;
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof SpotLight light) && equals(light);
    }

    public boolean equals(SpotLight other){
        return (other != null && (other == this || (color.equals(other.color) && position.equals(other.position) && direction.equals(other.direction) && Mathf.equal(intensity, other.intensity) && Mathf.equal(cutoffAngle, other.cutoffAngle) && Mathf.equal(exponent, other.exponent))));
    }
}
