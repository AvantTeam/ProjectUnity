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
import unity.entities.effects.*;
import unity.entities.units.*;
import unity.graphics.*;
import unity.util.*;

public class EndCutterLaserBulletType extends BulletType{
    public float maxlength = 1000f;
    public float laserSpeed = 15f;
    public float accel = 30f;
    public float width = 12f;
    public float antiCheatScl = 1f;
    public float fadeTime = 60f;
    public float fadeInTime = 8f;
    public Color[] colors = {UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.white};

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
        return maxlength / 2f;
    }

    @Override
    public void draw(Bullet b){
        float fade = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime);

        Lines.lineAngle(b.x, b.y, b.rotation(), b.fdata);
        for(int i = 0; i < colors.length; i++){
            float f = ((float)(colors.length - i) / colors.length);
            float w = f * (width + Mathf.absin(Time.time + (i * 1.4f), 1.1f, width / 4)) * fade;

            Tmp.v2.trns(b.rotation(), b.fdata).add(b);
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
        Tmp.v2.trns(b.rotation(), b.fdata + width).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, width * 2f, colors[0], 0.5f);
        Draw.reset();
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new Vec2();
    }

    @Override
    public void update(Bullet b){
        b.fdata = Mathf.clamp(b.fdata + (Mathf.clamp(b.time / accel) * laserSpeed * Time.delta), 0f, maxlength);

        if(b.timer(0, 5f)){
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, building -> {
                building.damage(damage);
                return false;
            }, unit -> {
                float lastHealth = unit.health;
                float extraDamage = (float)Math.pow(Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - 43000f) / 14000f, 0f, 8f), 2f);
                float trueDamage = damage + Mathf.clamp((unit.maxHealth + unit.type.dpsEstimate - 32000f) / 2f, 0f, 90000000f);
                trueDamage += extraDamage * (damage / 3f);
                unit.apply(status, statusDuration);
                if(unit instanceof AntiCheatBase){
                    ((AntiCheatBase)unit).overrideAntiCheatDamage(damage * antiCheatScl);
                }else{
                    unit.damage(trueDamage);
                }
                if((unit.dead || unit.health >= Float.MAX_VALUE || (lastHealth - trueDamage < 0f && !(unit instanceof AntiCheatBase))) && (unit.hitSize >= 30f || unit.health >= Float.MAX_VALUE)){
                    UnityAntiCheat.annihilateEntity(unit, true);
                    Tmp.v2.trns(b.rotation(), maxlength * 1.5f).add(b);
                    UnitCutEffect.createCut(unit, b.x, b.y, Tmp.v2.x, Tmp.v2.y);
                }
            }, hitEffect);
        }
        
        if(b.data instanceof Vec2){
            Vec2 vec = (Vec2)b.data;
            if(vec.y >= 1f && b.fdata > vec.x){
                int dst = Math.max(Mathf.round((b.fdata - vec.x) / 5), 1);
                for(int i = 0; i < dst; i++){
                    float f = Mathf.lerp(vec.x, b.fdata, i / (float)dst);
                    Tmp.v1.trns(b.rotation(), f).add(b);
                    Lightning.create(b.team, lightningColor, lightningDamage, Tmp.v1.x, Tmp.v1.y, b.rotation() + Mathf.range(20f), lightningLength);
                }
                vec.y -= 1f;
                vec.x = b.fdata;
            }
            vec.y += Time.delta;
        }
    }

    @Override
    public void init(){
        super.init();
        drawSize = maxlength * 2f;
    }
}
