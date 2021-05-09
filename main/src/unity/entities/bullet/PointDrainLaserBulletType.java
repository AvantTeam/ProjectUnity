package unity.entities.bullet;

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
import unity.graphics.*;
import unity.util.*;

public class PointDrainLaserBulletType extends BulletType{
    public float drainPercent = 0.1f;
    public float maxLength = 140f;
    public float width = 6f;
    public float area = 9f;
    public float fadeTime = 16f;
    public Color backColor = UnityPal.plagueDark;
    public Color frontColor = UnityPal.plague;

    public PointDrainLaserBulletType(float damage){
        super(0.001f, damage);
        despawnEffect = Fx.none;
        hitSize = 4;
        drawSize = 420f;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public float range(){
        return maxRange;
    }

    @Override
    public void update(Bullet b){
        if(!(b.data instanceof DrainLaserData dld) || !(b.owner instanceof Healthc hOwner)) return;
        if(b.owner instanceof Unit e){
            dld.pos.trns(b.rotation(), e.dst(e.aimX, e.aimY)).limit(maxLength);
        }
        float length = Utils.findLaserLength(b.x, b.y, dld.pos.x + b.x, dld.pos.y + b.y, tile -> tile.team() != b.team && tile.block().absorbLasers);
        dld.pos.setLength(length).add(b);
        dld.trail.update(dld.pos.x, dld.pos.y);
        if(b.timer(1, 5f)){
            /*Utils.collideLineRaw(b.x, b.y, dld.pos.x, dld.pos.y, build -> build.team != b.team, unit -> unit.team != b.team, building -> {
                hit(b, building.x, building.y);
                building.damage(damage * buildingDamageMultiplier);
                return false;
            }, unit -> {
                hit(b, unit.x, unit.y);
                unit.damage(damage);
                Tmp.v1.trns(b.rotation(), knockback);
                if(knockback != 0) unit.impulse(Tmp.v1);
            });*/
            Utils.collideLineRawEnemy(b.team, b.x, b.y, dld.pos.x, dld.pos.y, (building, direct) -> {
                if(direct){
                    building.damage(damage * buildingDamageMultiplier);
                }
                return false;
            }, unit -> {
                unit.damage(damage);
                Tmp.v1.trns(b.rotation(), knockback);
                if(knockback != 0) unit.impulse(Tmp.v1);
            }, null, (ex, ey) -> hit(b, ex, ey));
            Damage.damageUnits(b.team, dld.pos.x, dld.pos.y, area, damage, unit -> unit.within(dld.pos, area), unit -> hOwner.heal(damage * drainPercent));
            Vars.indexer.eachBlock(null, dld.pos.x, dld.pos.y, area, build -> build.team != b.team, build -> {
                build.damage(damage * buildingDamageMultiplier);
                hOwner.heal(damage * drainPercent);
            });
        }
    }

    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof DrainLaserData dld)) return;
        float fade = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (b.lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeTime);
        Draw.color(backColor);
        dld.trail.draw(backColor, fade * area / 2f);
        for(int i = 0; i < 2; i++){
            float size = Math.max((width * fade) - (i * width / 2f), 0f);
            Draw.color(i == 0 ? backColor : frontColor);
            Fill.circle(b.x, b.y, size / 2f);
            Lines.stroke(size);
            Lines.line(b.x, b.y, dld.pos.x, dld.pos.y, false);
            Fill.circle(dld.pos.x, dld.pos.y, Math.max((area * fade) - (i * area / 2f), 0f));
        }
        Drawf.light(b.team, b.x, b.y, dld.pos.x, dld.pos.y, fade * width * 2f, backColor, 0.5f);
        Draw.reset();
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new DrainLaserData();
        ((DrainLaserData)b.data).pos.set(b);
    }

    @Override
    public void init(){
        super.init();
        drawSize = maxLength * 2f;
    }

    private static class DrainLaserData{
        Trail trail = new Trail(6);
        Vec2 pos = new Vec2();
    }
}
