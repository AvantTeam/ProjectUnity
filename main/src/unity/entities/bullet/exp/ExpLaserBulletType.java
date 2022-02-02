package unity.entities.bullet.exp;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.graphics.*;
import unity.util.*;
import unity.world.blocks.exp.*;

public class ExpLaserBulletType extends BulletType {
    /** Color of laser. Shifts to second color as the turret levels up. */
    public Color fromColor = Pal.lancerLaser, toColor = UnityPal.expLaser;
    /** Dimensions of laser */
    public float width = 1f, length;
    /** Damage increase per owner level, if the owner can level up. */
    public float damageInc;
    /** Length increase per owner level, if the owner can level up. */
    public float lengthInc;
    /** Widths of each color */
    public float[] strokes = {2.9f, 1.8f, 1};
    /** Exp gained on hit */
    public int hitUnitExpGain, hitBuildingExpGain;

    public ExpLaserBulletType(float length, float damage){
        super(0.01f, damage);
        this.length = length;
        ammoMultiplier = 1;
        drawSize = length * 2f;
        hitSize = 0f;
        hitEffect = Fx.hitLiquid;
        shootEffect = Fx.hitLiquid;
        lifetime = 18f;
        despawnEffect = Fx.none;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    public ExpLaserBulletType(){
        this(120f, 1f);
    }

    public int getLevel(Bullet b){
        if(b.owner instanceof ExpTurret.ExpTurretBuild exp){
            return exp.level();
        }else{
            return 0;
        }
    }

    public float getLevelf(Bullet b){
        if(b.owner instanceof ExpTurret.ExpTurretBuild exp){
            return exp.levelf();
        }else{
            return 0f;
        }
    }

    public void setDamage(Bullet b){
        b.damage += damageInc * getLevel(b) * b.damageMultiplier();
    }

    public Color getColor(Bullet b){
        return Tmp.c2.set(fromColor).lerp(toColor, getLevelf(b));
    }

    public float getLength(Bullet b){
        return length + lengthInc * getLevel(b);
    }

    @Override
    public float range(){
        return Math.max(length, maxRange);
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        setDamage(b);

        Healthc target = Utils.linecast(b, b.x, b.y, b.rotation(), getLength(b));
        b.data = target;

        if(target instanceof Hitboxc hit){
            hit.collision(b, hit.x(), hit.y());
            b.collision(hit, hit.x(), hit.y());
            if(b.owner instanceof ExpTurret.ExpTurretBuild exp){
                if(exp.level() < exp.maxLevel() && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(hitUnitExpGain); i++){
                        UnityFx.expGain.at(hit.x(), hit.y(), 0f, (Position)b.owner);
                    }
                }
                exp.handleExp(hitUnitExpGain);
            }
        }else if(target instanceof Building tile && tile.collide(b)){
            tile.collision(b);
            hit(b, tile.x, tile.y);
            if(b.owner instanceof ExpTurret.ExpTurretBuild exp){
                if(exp.level() < exp.maxLevel() && Core.settings.getBool("hitexpeffect")){
                    for(int i = 0; i < Math.ceil(hitBuildingExpGain); i++){
                        UnityFx.expGain.at(tile.x, tile.y, 0f, (Position)b.owner);
                    }
                }
                exp.handleExp(hitBuildingExpGain);
            }
        }else{
            b.data = new Vec2().trns(b.rotation(), getLength(b)).add(b.x, b.y);
        }
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
    

    @Override
    public void drawLight(Bullet b){
        //no light drawn here
    }
}
