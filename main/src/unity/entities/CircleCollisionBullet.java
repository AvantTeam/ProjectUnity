package unity.entities;

import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.gen.*;

public class CircleCollisionBullet extends Bullet{
    float resetTime = 0f;

    @Override
    public void update(){
        super.update();
        if(!collided.isEmpty()){
            resetTime += Time.delta;
            if(resetTime >= 60f){
                collided.clear();
                resetTime = 0f;
            }
        }
    }

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

    @Override
    public void reset(){
        super.reset();
        resetTime = 0f;
    }
}
