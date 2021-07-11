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
import unity.content.effects.*;
import unity.entities.effects.*;
import unity.entities.units.*;
import unity.graphics.*;
import unity.mod.*;
import unity.util.*;

public class EndCutterLaserBulletType extends BulletType{
    public float maxLength = 1000f;
    public float laserSpeed = 15f;
    public float accel = 25f;
    public float width = 12f;
    public float antiCheatScl = 1f;
    public float fadeTime = 60f;
    public float fadeInTime = 8f;
    public Color[] colors = {UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};

    public float minimumPower = 43000f;
    public float powerFade = 14000f;
    public float minimumUnitScore = 32000f;

    private boolean hit = false;

    public EndCutterLaserBulletType(float damage){
        super(0.005f, damage);
        despawnEffect = Fx.none;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public float estimateDPS(){
        return damage * (lifetime / 2f) / 5f * 3f;
    }

    @Override
    public float range(){
        return maxLength / 2f;
    }

    @Override
    public void draw(Bullet b){
        float fade = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime);
        float tipHeight = width / 2f;

        Lines.lineAngle(b.x, b.y, b.rotation(), b.fdata);
        for(int i = 0; i < colors.length; i++){
            float f = ((float)(colors.length - i) / colors.length);
            float w = f * (width + Mathf.absin(Time.time + (i * 1.4f), 1.1f, width / 4)) * fade;

            Tmp.v2.trns(b.rotation(), b.fdata - tipHeight).add(b);
            Tmp.v1.trns(b.rotation(), width * 2f).add(Tmp.v2);
            Draw.color(colors[i]);
            Fill.circle(b.x, b.y, w / 2f);
            Lines.stroke(w);
            Lines.line(b.x, b.y, Tmp.v2.x, Tmp.v2.y, false);
            for(int s : Mathf.signs){
                Tmp.v3.trns(b.rotation(), w * -0.7f, w * s);
                Fill.tri(Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x + Tmp.v3.x, Tmp.v2.y + Tmp.v3.y);
            }
        }
        Tmp.v2.trns(b.rotation(), b.fdata + tipHeight).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, width * 2f, colors[0], 0.5f);
        Draw.reset();
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new LaserData();
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof LaserData){
            LaserData vec = (LaserData)b.data;
            if(vec.restartTime >= 5f){
                vec.velocity = Mathf.clamp((vec.velocityTime / accel) + vec.velocity, 0f, laserSpeed);
                b.fdata = Mathf.clamp(b.fdata + (vec.velocity * Time.delta), 0f, maxLength);
                vec.velocityTime += Time.delta;
            }else{
                vec.restartTime += Time.delta;
            }
        }

        if(b.timer(0, 5f)){
            hit = false;
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, (building, direct) -> {
                if(hit) return true;
                if(direct){
                    if(building.health > damage * buildingDamageMultiplier * 0.5f){
                        Tmp.v2.trns(b.rotation(), maxLength * 1.5f).add(b);
                        float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, building.x, building.y);
                        b.fdata = ((b.dst(building) - (building.block.size * Vars.tilesize / 2f)) + dst) + 4f;
                        if(b.data instanceof LaserData){
                            LaserData data = (LaserData)b.data;
                            data.velocity = 0f;
                            data.restartTime = 0f;
                            data.velocityTime = 0f;
                        }
                        Tmp.v2.trns(b.rotation(), b.fdata).add(b);
                        //UnityFx.tenmeikiriTipHit.at(Tmp.v2.x, Tmp.v2.y, b.rotation() + 180f);
                        for(int i = 0; i < 2; i++){
                            HitFx.tenmeikiriTipHit.at(Tmp.v2.x + Mathf.range(4f), Tmp.v2.y + Mathf.range(4f), b.rotation() + 180f);
                        }
                        building.damage(damage * buildingDamageMultiplier);
                        hit = true;
                        return true;
                    }
                    building.damage(damage * buildingDamageMultiplier);
                }
                return false;
            }, unit -> {
                if(hit) return;
                if(unit.health > damage){
                    Tmp.v2.trns(b.rotation(), maxLength * 1.5f).add(b);
                    float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, unit.x, unit.y);
                    b.fdata = ((b.dst(unit) - (unit.hitSize / 2f)) + dst) + 4f;
                    if(b.data instanceof LaserData){
                        LaserData data = (LaserData)b.data;
                        data.velocity = 0f;
                        data.restartTime = 0f;
                        data.velocityTime = 0f;
                    }
                    Tmp.v2.trns(b.rotation(), b.fdata).add(b);
                    for(int i = 0; i < 2; i++){
                        HitFx.tenmeikiriTipHit.at(Tmp.v2.x + Mathf.range(4f), Tmp.v2.y + Mathf.range(4f), b.rotation() + 180f);
                    }
                    hit = true;
                }

                float lastHealth = unit.health;
                float extraDamage = (float)Math.pow(Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - minimumPower) / powerFade, 0f, 8f), 2f);
                float trueDamage = damage + Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - minimumUnitScore) / 2f, 0f, 90000000f);
                trueDamage += extraDamage * (damage / 3f);
                unit.apply(status, statusDuration);
                if(unit instanceof AntiCheatBase){
                    ((AntiCheatBase)unit).overrideAntiCheatDamage(damage * antiCheatScl);
                }else{
                    unit.damage(trueDamage);
                }

                if((unit.dead || unit.health >= Float.MAX_VALUE || (lastHealth - trueDamage < 0f && !(unit instanceof AntiCheatBase))) && (unit.hitSize >= 30f || unit.health >= Float.MAX_VALUE)){
                    AntiCheat.annihilateEntity(unit, true);
                    Tmp.v2.trns(b.rotation(), maxLength * 1.5f).add(b);
                    UnitCutEffect.createCut(unit, b.x, b.y, Tmp.v2.x, Tmp.v2.y);
                }
            }, (ex, ey) -> hitEffect.at(ex, ey, b.rotation()), true);
        }
        
        if(b.data instanceof LaserData){
            LaserData vec = (LaserData)b.data;
            if(vec.lightningTime >= 1f && b.fdata > vec.lastLength){
                int dst = Math.max(Mathf.round((b.fdata - vec.lastLength) / 5), 1);
                for(int i = 0; i < dst; i++){
                    float f = Mathf.lerp(vec.lastLength, b.fdata, (float)i / dst);
                    Tmp.v1.trns(b.rotation(), f).add(b);
                    Lightning.create(b.team, lightningColor, lightningDamage, Tmp.v1.x, Tmp.v1.y, b.rotation() + Mathf.range(20f), lightningLength);
                }
                vec.lightningTime -= 1f;
                vec.lastLength = b.fdata;
            }
            vec.lightningTime += Time.delta;
        }
    }

    @Override
    public void init(){
        super.init();
        drawSize = maxLength * 2f;
    }

    static class LaserData{
        int pierceCounter = 0;
        float lastLength, lightningTime, velocity, velocityTime, targetSize, restartTime = 5f;
        Position target;
    }
}
