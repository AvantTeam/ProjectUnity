package unity.entities.bullet.monolith;

import arc.math.geom.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

/** @author GlennFolker */
public class RicochetBulletType extends BasicBulletType{
    public int trailLength = 6;

    public RicochetBulletType(float speed, float damage){
        this(speed, damage, "bullet");
    }

    public RicochetBulletType(float speed, float damage, String spriteName){
        super(speed, damage, spriteName);
        pierce = true;
        pierceBuilding = true;
        pierceCap = 3;
        trailChance = 1f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new RicochetBulletData();
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc other, float initialHealth){
        ricochet(b, other);
    }

    @Override
    public void hitTile(Bullet b, Building build, float initialHealth, boolean direct){
        super.hitTile(b, build, initialHealth, direct);
        if(direct){
            ricochet(b, build);
        }
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        RicochetBulletData data = (RicochetBulletData)b.data;
        if(data.trail != null){
            data.trail.update(b.x, b.y);
        }
    }

    @Override
    public void draw(Bullet b){
        RicochetBulletData data = (RicochetBulletData)b.data;
        if(data.trail != null){
            data.trail.draw(backColor, width * 0.18f);
        }

        super.draw(b);
    }

    public void ricochet(Bullet b, Posc entity){
        RicochetBulletData data = (RicochetBulletData)b.data;
        if(data == null) return;

        if(data.hit == entity.id()) return;

        data.hit = entity.id();
        b.collided.clear();

        if(data.ricochet < pierceCap){
            data.findEnemy(b);
            if(data.target != null){
                if(data.target instanceof Hitboxc v){
                    Vec2 out = Predict.intercept(b.x, b.y, v.x(), v.y(), v.deltaX(), v.deltaY(), b.vel.len());
                    float rot = out.sub(b.x, b.y).angle();
                    b.vel.setAngle(rot);
                }else{
                    b.vel.setAngle(b.angleTo(data.target));
                }
            }else{
                despawned(b);
            }
        }
    }

    public class RicochetBulletData{
        protected int ricochet;

        protected Teamc target;
        protected int hit;

        protected Trail trail = new Trail(trailLength);

        protected RicochetBulletData(){}

        protected void findEnemy(Bullet b){
            target = Units.closestTarget(b.team, b.x, b.y, range() * b.fout(),
                u -> u.isValid() && u.id != hit && ((u.isFlying() && collidesAir) || (u.isGrounded() && collidesGround)),
                t -> t.isValid() && t.id != hit && collidesGround
            );
        }
    }
}
