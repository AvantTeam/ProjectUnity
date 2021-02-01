package unity.entities.bullet;

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
    /** Colors. Should only be 2 colors. */
    public Color color = Pal.heal;
    public boolean fromLightning;
    public float force, scaledForce, fromLightningMinDamage, fromLightningMaxDamage;
    public float laserWidth = 0.6f;
    public float lightWidth = 15f;
    public float length;

    public BeamBulletType(float length, float damage){
        super(0.01f, damage);
        
        this.length = length;
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
    public void update(Bullet b){
        super.update(b);
        
        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
        b.data = target;
        
        if(target instanceof Hitboxc hit){
            if(b.timer.get(1, 5)){
                hit.collision(b, target.x(), target.y());
                b.collision(hit, target.x(), target.y());
                if(fromLightning) Lightning.create(b.team, color, Mathf.random(fromLightningMinDamage, fromLightningMaxDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / Vars.tilesize + 3));
            }
        }else if(target instanceof Building build){
            if(b.timer.get(1, 5)){
                if(build.collide(b)){
                    build.collision(b);
                    hit(b, target.x(), target.y());
                }
                if(fromLightning) Lightning.create(b.team, color, Mathf.random(fromLightningMinDamage, fromLightningMaxDamage), b.x, b.y, b.angleTo(target), Mathf.floorPositive(b.dst(target) / Vars.tilesize + 3));
            }
        }else{
            b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
            if(b.timer.get(1, 5) && fromLightning){
                Vec2 point = (Vec2) b.data;
                Lightning.create(b.team, color, Mathf.random(fromLightningMinDamage, fromLightningMaxDamage), b.x, b.y, b.angleTo(point.x, point.y), Mathf.floorPositive(b.dst(point.x, point.y) / Vars.tilesize + 3));
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
            Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, laserWidth * b.fout());
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, lightWidth * b.fout(), color, 0.6f);

            //Unity.print("Pos based draw");
        }else if(b.data instanceof Vec2 data){
            Tmp.v1.set(data);

            Draw.color(color);
            Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, laserWidth * b.fout());
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, lightWidth * b.fout(), color, 0.6f);
            
            //Unity.print("Vec2 based draw");
        }
    }
}