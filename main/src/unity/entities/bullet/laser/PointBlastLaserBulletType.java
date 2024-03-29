package unity.entities.bullet.laser;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.content.effects.SpecialFx.*;
import unity.util.*;

public class PointBlastLaserBulletType extends BulletType implements PointBlastInterface{
    public float damageRadius = 20f;
    public float auraDamage = 10f;
    public float length = 100f;
    public float width = 12f;
    public float widthReduction = 2f;
    public float auraWidthReduction = 3f;
    public boolean overrideDamage = false;
    public Color[] laserColors = {Color.white};

    private static boolean available = false;

    public PointBlastLaserBulletType(float damage){
        speed = 0f;
        this.damage = damage;

        hitEffect = Fx.hitLancer;
        despawnEffect = Fx.none;
        shootEffect = Fx.none;
        smokeEffect = Fx.none;
        hitSize = 4f;
        lifetime = 16f;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public Color[] colors(){
        return laserColors;
    }

    @Override
    public float widthReduction(){
        return auraWidthReduction;
    }

    @Override
    public float estimateDPS(){
        return (super.estimateDPS() * 3f) + auraDamage;
    }

    @Override
    public float range(){
        return length;
    }

    @Override
    public void init(){
        super.init();

        drawSize = Math.max(drawSize, length + damageRadius * 2f);
    }

    public void handleUnit(Bullet b, Unit unit, float initialHealth){

    }

    //new function instead of hitTile, to avoid hit() function
    public void handleBuilding(Bullet b, Building build, float initialHealth){
        build.damage(auraDamage);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        
        b.fdata = length;
        Tmp.v1.trns(b.rotation(), length);
        Tmp.v1.add(b);
        available = false;
        Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, (building, direct) -> {
            if(direct){
                building.damage(b.damage);
                available = true;
                b.fdata = b.dst(building);
                Tmp.v2.trns(b.rotation(), b.dst(building));
                Tmp.v2.add(b.x, b.y);
            }
            return building.block.absorbLasers;
        }, unit -> {
            available = true;
            Tmp.v2.trns(b.rotation(), b.dst(unit));
            Tmp.v2.add(b.x, b.y);
            unit.damage(b.damage);
            unit.apply(status, statusDuration);
        }, entity -> (b.dst(entity) / 2f) - entity.health(), (ex, ey) -> hitEffect.at(ex, ey, b.rotation()));

        if(available){
            b.fdata = b.dst(Tmp.v2);
            //Vars.indexer.eachBlock(null, Tmp.v2.x, Tmp.v2.y, damageRadius, building -> building.team != b.team, building -> handleBuilding(b, building, building.health));
            Utils.trueEachBlock(Tmp.v2.x, Tmp.v2.y, damageRadius, building -> {
                if(building.team != b.team){
                    handleBuilding(b, building, building.health);
                }
            });
            Units.nearby(Tmp.v2.x - damageRadius, Tmp.v2.y - damageRadius, damageRadius * 2f, damageRadius * 2f, unit -> {
                if(unit.team != b.team && unit.within(Tmp.v2.x, Tmp.v2.y, damageRadius)){
                    float ratio = b.damage / damage;
                    float health = unit.health;
                    if(!overrideDamage) unit.damage(auraDamage * ratio);
                    handleUnit(b, unit, health);
                    unit.apply(status, statusDuration);
                }
            });
            SpecialFx.pointBlastLaserEffect.at(Tmp.v2.x, Tmp.v2.y, damageRadius, this);
        }
    }

    @Override
    public void draw(Bullet b){
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0f, 0.2f);
        float baseLen = realLength * f;

        for(int i = 0; i < laserColors.length; i++){
            float wReduced = i * widthReduction;
            Draw.color(laserColors[i]);
            Fill.circle(b.x, b.y, ((width - wReduced) / 2f) * b.fout());
            Lines.stroke((width - wReduced) * b.fout());
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
            Tmp.v1.trns(b.rotation(), baseLen).add(b);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() * 1.22f, width * 2f, b.rotation());
            Draw.reset();
            Tmp.v1.trns(b.rotation(), baseLen + (width / 1.5f)).add(b);
        }
        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 1.4f * b.fout(), laserColors[0], 0.5f);
    }

    @Override
    public void drawLight(Bullet b){

    }
}
