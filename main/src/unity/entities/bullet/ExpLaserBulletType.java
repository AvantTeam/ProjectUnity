package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.comp.*;
import unity.graphics.*;

public class ExpLaserBulletType extends BulletType {
    /** Color of laser. Shifts to second color as the turret levels up. */
    public Color fromColor = Pal.lancerLaser, toColor = UnityPal.expLaser;
    /** Dimentions of laser */
    public float width = 1, length;
    /** Damage increase per owner level, if the owner can level up. */
    public float damageInc;
    /** Length increase per owner level, if the owner can level up. */
    public float lengthInc;
    /** Widths of each color */
    public float[] strokes = {2.9f, 1.8f, 1};
    /** Exp gained on hit */
    public float hitUnitExpGain, hitBuildingExpGain;

    public ExpLaserBulletType(float length, float damage){
        super(0.01f, damage);
        this.length = length;
        hitEffect = Fx.hitLiquid;
        shootEffect = Fx.hitLiquid;
        lifetime = 16f;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    public ExpLaserBulletType(){
        this(120f, 1f);
    }

    public void setDamage(Bullet b){
        if(b.owner instanceof ExpBuildc exp){
            int lvl = exp.level();

            b.damage = b.damage + lvl * damageInc * b.damageMultiplier();
        }
    }

    public Color getColor(Bullet b){
        if(b.owner instanceof ExpBuildc exp){
            int lvl = exp.level();

            return Tmp.c1.set(fromColor).lerp(toColor, lvl / 10f);
        }else{
            return fromColor;
        }
    }

    public float getLength(Bullet b){
        if(b.owner instanceof ExpBuildc exp){
            int lvl = exp.level();

            return length + lengthInc * lvl;
        }else{
            return length;
        }
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        setDamage(b);

        Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), getLength(b));
        b.data = target;

        if(target instanceof Hitboxc hit){
            hit.collision(b, hit.x(), hit.y());
            b.collision(hit, hit.x(), hit.y());
            if(b.owner instanceof ExpBuildc exp) exp.incExp(hitUnitExpGain);
        }else if(target instanceof Building tile && tile.collide(b)){
            tile.collision(b);
            this.hit(b, tile.x, tile.y);
            if(b.owner instanceof ExpBuildc exp) exp.incExp(hitBuildingExpGain);
        }else{
            b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
        }
    }

    @Override
    public float range(){
        return this.length;
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof Position point){
            Tmp.v1.set(point);

            Draw.color(getColor(b));

            Draw.alpha(0.4f);
            Lines.stroke(b.fout() * width * strokes[0]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.alpha(1);
            Lines.stroke(b.fout() * width * strokes[1]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

            Draw.color(Color.white);
            Lines.stroke(b.fout() * width * strokes[2]);
            Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
            Draw.reset();

            Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 10 * b.fout(), Color.white, 0.6f);
        }
    }
}
