package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import unity.content.UnityFx;
import unity.entities.SaberData;
import unity.graphics.UnityPal;
import unity.util.Funcs;

public class SaberContinuousLaserBulletType extends ContinuousLaserBulletType{
    protected boolean swipe;

    public SaberContinuousLaserBulletType(float damage){
        super(damage);
    }

    public SaberContinuousLaserBulletType(){
        this(0f);
    }

    float chargedDamage(Bullet b, float val){
        return b.time < 40f ? val * (40f - b.time) : 0f;
    }

    @Override
    public void update(Bullet b){
        if(!(b.data instanceof SaberData)) b.data = new SaberData(0f, 3, b.rotation(), 10);
        SaberData temp = (SaberData)b.data;
        if(swipe){
            float angDst = Angles.angleDist(b.rotation(), temp.rot) / Time.delta;
            temp.mean.add(angDst);
            angDst = temp.mean.rawMean();
            if(b.owner instanceof Velc) temp.f = Mathf.clamp(temp.f + ((Velc)b.owner).vel().len() / 2f + angDst, 0f, length + angDst * 7f);

            float damageC = chargedDamage(b, angDst);
            float realLength = Damage.findLaserLength(b, temp.f);
            float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
            float baseLen = realLength * fout;

            if(b.timer(1, 5f)){
                Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), temp.f, largeHit);
                if(angDst > 0.0001f) Funcs.collideLineDamageOnly(b.team, (angDst + damageC) * 2f, b.x, b.y, b.rotation(), temp.f, b);
            }

            if(b.time < 25f){
                float c = (25f - b.time) * (angDst / 25f) / 25f;
                for(int i = 0; i < 3; i++){
                    float lenRangedB = baseLen + Mathf.range(16f);
                    if(Mathf.chanceDelta(c) && lenRangedB >= 8f) Lightning.create(b, UnityPal.scarColor, 3 + damageC / 2f, b.x, b.y, b.rotation(), Mathf.round(lenRangedB / 8f));
                }
            }
            float lenRanged = baseLen + Mathf.range(16f);
            if(Mathf.chanceDelta((0.1f + Mathf.clamp(angDst / 25f)) * b.fout()) && Mathf.round(lenRanged / 8f) >= 1) Lightning.create(b, UnityPal.scarColor, 6f + angDst * 1.7f + damageC * 2f, b.x, b.y, b.rotation(), Mathf.round(lenRanged / 8f));
            if(Mathf.chanceDelta(0.12f * b.fout())) UnityFx.falseLightning.at(b.x, b.y, b.rotation(), UnityPal.scarColor, baseLen);
            temp.rot = b.rotation();
            Tmp.v1.trns(b.rotation(), baseLen / 2f);
            temp.fT.update(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation() + 90f);
        }else{
            temp.f = length;
            if(b.owner instanceof Velc) temp.f = Mathf.clamp(((Velc)b.owner).vel().len() * 19f, 0f, length);
            if(b.timer(1, 5f)) Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), temp.f, largeHit);
        }
    }

    @Override
    public void draw(Bullet b){
        if(!(b.data instanceof SaberData)) return;
        SaberData temp = (SaberData)b.data;
        float realLength = Damage.findLaserLength(b, temp.f);
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float baseLen = realLength * fout;
        temp.fT.draw(UnityPal.scarColor, baseLen * 0.5f);
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
        for(int s = 0; s < colors.length; s++){
            Draw.color(Tmp.c1.set(colors[s]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * spaceMag);
                Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }
        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
    }
}
