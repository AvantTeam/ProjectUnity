package unity.assets.type.g3d.attribute.light;

import arc.graphics.*;
import arc.math.geom.*;

public class DirectionalLight extends BaseLight<DirectionalLight>{
    public final Vec3 direction = new Vec3();

    public DirectionalLight setDirection(float directionX, float directionY, float directionZ){
        direction.set(directionX, directionY, directionZ);
        return this;
    }

    public DirectionalLight setDirection(Vec3 direction){
        this.direction.set(direction);
        return this;
    }

    public DirectionalLight set(DirectionalLight copyFrom){
        return set(copyFrom.color, copyFrom.direction);
    }

    public DirectionalLight set(Color color, Vec3 direction){
        if(color != null) this.color.set(color);
        if(direction != null) this.direction.set(direction).nor();
        return this;
    }

    public DirectionalLight set(float r, float g, float b, Vec3 direction){
        this.color.set(r, g, b, 1f);
        if(direction != null) this.direction.set(direction).nor();
        return this;
    }

    public DirectionalLight set(Color color, float dirX, float dirY, float dirZ){
        if(color != null) this.color.set(color);
        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }

    public DirectionalLight set(float r, float g, float b, float dirX, float dirY, float dirZ){
        this.color.set(r, g, b, 1f);
        this.direction.set(dirX, dirY, dirZ).nor();
        return this;
    }

    @Override
    public boolean equals(Object other){
        return (other instanceof DirectionalLight light) && equals(light);
    }

    public boolean equals(DirectionalLight other){
        return other != null && (other == this || (color.equals(other.color) && direction.equals(other.direction)));
    }
}
