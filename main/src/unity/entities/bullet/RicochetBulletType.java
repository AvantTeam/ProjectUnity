package unity.entities.bullet;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class RicochetBulletType extends BasicBulletType{
    public int trailLength = 6;

    public RicochetBulletType(float speed, float damage){
        super(speed, damage);
        pierce = true;
        pierceBuilding = true;
        pierceCap = 3;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = Pools.obtain(RicochetBulletData.class, RicochetBulletData::new).set(0);
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
            data.trail.draw(backColor, width * 0.4f);
        }

        super.draw(b);
    }

    @Override
    public void despawned(Bullet b){
        super.despawned(b);
        Pools.free(b.data);
    }

    public void ricochet(Bullet b, Posc entity){
        RicochetBulletData data = (RicochetBulletData)b.data;
        if(data == null) return;

        if(data.ids.contains(entity.id())) return;
        data.ids.add(entity.id());

        if(data.ricochet < pierceCap){
            data.findEnemy(b);
            if(data.target != null){
                //TODO doesn't seem right
                Vec2 out = Predict.intercept(b, data.target, speed);
                b.vel().setAngle(b.angleTo(out));
            }else{
                despawned(b);
            }
        }
    }

    public class RicochetBulletData implements Poolable{
        protected int ricochet;
        protected Teamc target;

        protected Trail trail = new Trail(trailLength);
        protected IntSeq ids = new IntSeq();

        protected RicochetBulletData set(int ricochet){
            this.ricochet = ricochet;
            return this;
        }

        protected void findEnemy(Bullet b){
            target = Units.closestTarget(b.team, b.x, b.y, range() * b.fout(),
                u -> u.isValid() && !b.collided.contains(u.id) && ((u.isFlying() && collidesAir) || (u.isGrounded() && collidesGround)),
                t -> t.isValid() && !b.collided.contains(t.id) && collidesGround
            );
        }

        @Override
        public void reset(){
            ricochet = 0;
            target = null;
            trail.clear();
            ids.clear();
        }
    }
}
