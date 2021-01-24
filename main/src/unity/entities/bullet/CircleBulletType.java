package unity.entities.bullet;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.entities.*;

@SuppressWarnings("unchecked")
public class CircleBulletType extends BulletType{
    public Func<Bullet, Color> color = (Bullet b) -> Color.red;

    public CircleBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.data instanceof Cons c) c.get(b);
    }

    @Override
    public void draw(Bullet b){
        float z = Draw.z();
        Draw.z(b.hitSize > 50f ? z - 0.001f : z);
        Draw.color(color.get(b));
        Fill.circle(b.x, b.y, b.hitSize + 1.5f);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, b.hitSize);
        Draw.z(z);
    }

    @Override
    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        CircleCollisionBullet bullet = CircleCollisionBullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.vel.trns(angle, speed * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();

        if(keepVelocity && owner instanceof Velc v) bullet.vel.add(v.vel());
        return bullet;
    }
}
