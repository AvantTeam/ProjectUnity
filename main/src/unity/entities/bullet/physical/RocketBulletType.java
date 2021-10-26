package unity.entities.bullet.physical;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class RocketBulletType extends BasicBulletType{
    public float backSpeed = 1f;
    public float accel = 1.01f;

    public RocketBulletType(float maxSpeed,float damage, String sprite){
        super(maxSpeed, damage, sprite); //Speed means nothing
        layer = Layer.effect + 1;
        keepVelocity = false;
    }

    @Override
    public void load(){
        super.load();
        backRegion = Core.atlas.find(sprite + "-outline");
    }

    @Override
    public float range(){ //TODO proper range calculation
        return super.range();
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.data instanceof RocketData r){
            r.scl *= accel * Time.delta;
            Tmp.v1.trns(r.angle, r.scl - 1f);
            b.vel.add(Tmp.v1);
            if(speed > 0 && Angles.within(b.vel.angle(), r.angle, 90)){
                b.vel.limit(speed);
            }
        }
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof RocketData r){
            Draw.z(layer - 0.01f);
            Draw.rect(backRegion, b.x, b.y, r.angle - 90f);
            Draw.z(layer);
            Draw.rect(frontRegion, b.x, b.y, r.angle - 90f);
            Draw.reset();
        }
    }

    @Override
    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        Bullet bullet = super.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
        bullet.initVel(angle, -backSpeed * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        if(keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
        bullet.data = new RocketData(-bullet.vel.len(), angle);
        return bullet;
    }

    public static class RocketData{
        float angle;
        float vel, scl = 1f;

        public RocketData(float vel, float angle){
            this.vel = vel;
            this.angle = angle;
        }
    }
}