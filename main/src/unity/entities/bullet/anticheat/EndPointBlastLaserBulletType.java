package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.content.effects.SpecialFx.*;
import unity.util.*;

public class EndPointBlastLaserBulletType extends AntiCheatBulletTypeBase implements PointBlastInterface{
    public float damageRadius = 20f;
    public float auraDamage = 10f;
    public float length = 100f;
    public float width = 12f;
    public float widthReduction = 2f;
    public float auraWidthReduction = 3f;
    public Color[] laserColors = {Color.white};

    private static boolean available = false;

    public EndPointBlastLaserBulletType(float damage){
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

    @Override
    public void init(Bullet b){
        super.init(b);

        b.fdata = length;
        Tmp.v1.trns(b.rotation(), length).add(b);
        available = false;

        Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width / 3f, (bd, direct) -> {
            if(direct){
                available = true;
                b.fdata = b.dst(bd);
                Tmp.v2.trns(b.rotation(), b.fdata).add(b.x, b.y);
                hitBuildingAnticheat(b, bd);
            }
            return bd.block.absorbLasers;
        }, u -> {
            available = true;
            Tmp.v2.trns(b.rotation(), b.dst(u)).add(b.x, b.y);
            hitUnitAntiCheat(b, u);
            return false;
        }, entity -> (b.dst(entity) / 2f) - entity.health(), (ex, ey) -> hitEffect.at(ex, ey, b.rotation()), true);

        if(available){
            b.fdata = b.dst(Tmp.v2);
            Utils.trueEachBlock(Tmp.v2.x, Tmp.v2.y, damageRadius, building -> {
                if(building.team != b.team){
                    hitBuildingAnticheat(b, building, auraDamage * (b.damage / damage));
                }
            });
            Units.nearby(Tmp.v2.x - damageRadius, Tmp.v2.y - damageRadius, damageRadius * 2f, damageRadius * 2f, unit -> {
                if(unit.team != b.team && unit.within(Tmp.v2.x, Tmp.v2.y, damageRadius)){
                    hitUnitAntiCheat(b, unit, auraDamage * (b.damage / damage));
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
