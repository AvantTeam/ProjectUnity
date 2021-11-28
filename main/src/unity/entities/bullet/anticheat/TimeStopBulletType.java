package unity.entities.bullet.anticheat;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.entities.bullet.anticheat.modules.*;
import unity.gen.*;
import unity.graphics.*;
import unity.mod.*;

public class TimeStopBulletType extends AntiCheatBulletTypeBase{
    public float duration = 45f;

    public TimeStopBulletType(float speed, float damage){
        super(speed, damage);
        despawnEffect = hitEffect = HitFx.endHitRedSmall;
        trailColor = UnityPal.scarColor;
        trailLength = 10;
        trailWidth = 4f;
        pierce = true;
        pierceCap = 3;
        lifetime = 110f;

        modules = new AntiCheatBulletModule[]{
            new ArmorDamageModule(1f / 100, 2f, 8f, 3f)
        };
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);
        Draw.color(trailColor);
        Drawf.tri(b.x, b.y, trailWidth * 2 * 1.22f, 14f, b.rotation());
        Drawf.tri(b.x, b.y, trailWidth * 2 * 1.22f, 7f, b.rotation() + 180f);
        Draw.color();
    }

    @Override
    public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        Bullet b = TimeStopBullet.create();
        b.type = this;
        b.owner = owner;
        b.team = team;
        b.time = 0f;
        b.initVel(angle, speed * velocityScl);
        if(backMove){
            b.set(x - b.vel.x * Time.delta, y - b.vel.y * Time.delta);
        }else{
            b.set(x, y);
        }
        b.lifetime = lifetime * lifetimeScl;
        b.data = data;
        b.drag = drag;
        b.hitSize = hitSize;
        b.damage = (damage < 0 ? this.damage : damage) * b.damageMultiplier();
        //reset trail
        if(b.trail != null){
            b.trail.clear();
        }
        b.add();

        if(keepVelocity && owner instanceof Velc) b.vel.add(((Velc)owner).vel());

        if(TimeStop.inTimeStop() && owner != null){
            float duration = Math.min(this.duration, TimeStop.getTime(owner));
            if(duration > 0f){
                TimeStop.addEntity(b, duration);
            }
        }
        return b;
    }
}
