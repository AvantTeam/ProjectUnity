package unity.entities;

import arc.math.geom.*;
import arc.util.pooling.*;
import mindustry.gen.*;

public class CircleCollisionBullet extends Bullet{
    @Override
    public boolean collides(Hitboxc other){
        return super.collides(other) && within(other, (other.hitSize() / 2f) + hitSize());
    }

    @Override
    public void hitbox(Rect rect){
        rect.setCentered(x, y, hitSize * 2f);
    }

    @Override
    public float clipSize(){
        return hitSize() * 2f;
    }

    public static CircleCollisionBullet create(){
        return Pools.obtain(CircleCollisionBullet.class, CircleCollisionBullet::new);
    }
}
