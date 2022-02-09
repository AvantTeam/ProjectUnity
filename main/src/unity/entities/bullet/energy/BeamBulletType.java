package unity.entities.bullet.energy;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class BeamBulletType extends BulletType{
    public Color color = Pal.heal;
    public float beamWidth = 0.6f;
    public float lightWidth = 15f;
    public float length;

    /** Whether or not for this beam to cast lightning. */
    public boolean castsLightning;
    /** The interval between lightning casts and collision in ticks. */
    public float castInterval = 5f;
    public float minLightningDamage, maxLightningDamage;

    public String name;
    public TextureRegion region, endRegion;

    public BeamBulletType(float length, float damage){
        this(length, damage, "laser");
    }

    public BeamBulletType(float length, float damage, String name){
        super(0.01f, damage);

        this.length = length;
        this.name = name;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
        lifetime = 16f;
        shootEffect = Fx.none;
        despawnEffect = Fx.none;
        hitSize = 0f;
    }

    public BeamBulletType(){
        this(1f, 1f);
    }

    @Override
    public void load(){
        region = Core.atlas.find(name);
        endRegion = Core.atlas.find(name + "-end");
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        b.data = target;

        if(target instanceof Hitboxc hit){
            if(b.timer.get(1, castInterval)){
                hit.collision(b, target.x(), target.y());
                b.collision(hit, target.x(), target.y());
                if(castsLightning){
                    Lightning.create(b.team, color, Mathf.random(minLightningDamage, maxLightningDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / Vars.tilesize + 3));
                }
            }
        }else if(target instanceof Building build){
            if(b.timer.get(1, castInterval)){
                if(build.collide(b)){
                    build.collision(b);
                    hit(b, target.x(), target.y());
                }
                if(castsLightning){
                    Lightning.create(b.team, color, Mathf.random(minLightningDamage, maxLightningDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / Vars.tilesize + 3));
                }
            }
        }else{
            b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
            if(b.timer.get(1, castInterval) && castsLightning){
                Vec2 point = (Vec2)b.data;
                Lightning.create(b.team, color, Mathf.random(minLightningDamage, maxLightningDamage), b.x, b.y, b.angleTo(point.x, point.y), Mathf.floorPositive(b.dst(point.x, point.y) / Vars.tilesize + 3));
            }
        }
    }

    @Override
    public float range(){
        return length;
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof Position data){
            Tmp.v1.set(data);

            Draw.color(color);
            Drawf.laser(b.team, region, endRegion, b.x, b.y, Tmp.v1.x, Tmp.v1.y, beamWidth * b.fout());
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, lightWidth * b.fout(), color, 0.6f);
        }
    }
}
