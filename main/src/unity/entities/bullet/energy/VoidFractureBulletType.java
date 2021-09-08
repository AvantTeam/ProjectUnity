package unity.entities.bullet.energy;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class VoidFractureBulletType extends BulletType{
    public float delay = 40f;
    public float targetingRange = 320f;
    public float trueSpeed;
    public float nextLifetime = 10f;

    private static float s;

    public VoidFractureBulletType(float speed, float damage){
        super(0f, damage);
        trueSpeed = speed;
        collides = false;
        collidesTiles = false;
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.data instanceof FractureData data){
            if(b.timer(1, 5f)){
                s = Float.MAX_VALUE;
                Floatf<Healthc> score = h -> h.dst2(b) + Mathf.pow(Angles.angleDist(b.rotation(), b.angleTo(h)), 4f);

                Units.nearbyEnemies(b.team, b.x, b.y, targetingRange, u -> {
                    if(Angles.within(b.rotation(), b.angleTo(u), 45f) && score.get(u) < s){

                    }
                });
            }
        }
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new FractureData();
    }

    static class FractureData{
        Healthc target;
        IntSet collided = new IntSet();
    }
}
