package unity.entities.bullet;

import arc.math.*;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.gen.*;
import unity.content.*;
import unity.entities.*;
import unity.graphics.UnityPal;
import unity.util.Funcs;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.bullet.ContinuousLaserBulletType;

//lmao
public class SparkingContinuousLaserBulletType extends ContinuousLaserBulletType{
    protected float fromBlockChance = 0.4f, fromBlockDamage = 23f,
    fromLaserChance = 0.9f, fromLaserDamage = 23f,
    incendStart = 2.9f;
    protected int fromLaserLen = 4, fromLaserLenRand = 5, fromLaserAmount = 1,
    fromBlockLen = 2, fromBlockLenRand = 5, fromBlockAmount = 1;
    protected boolean extinction;
    protected final Seq<Unit> tempSeq = new Seq<>();

    public SparkingContinuousLaserBulletType(float damage){
        super(damage);
        lightningColor = UnityPal.laserOrange;
    }

    public SparkingContinuousLaserBulletType(){
        this(0);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        float realLength = Damage.findLaserLength(b, length);
        for(int i = 0; i < fromBlockAmount; i++){
            if(Mathf.chanceDelta(fromBlockChance)) Lightning.create(b.team, lightningColor, fromBlockDamage, b.x, b.y, b.rotation(), Mathf.round(length / (float)8) + fromBlockLen + Mathf.random(fromBlockLenRand));
        }
        for(int i = 0; i < fromLaserAmount; i++){
            if(Mathf.chanceDelta(fromLaserChance)){
                int lLength = fromLaserLen + Mathf.random(fromLaserLenRand);
                Tmp.v1.trns(b.rotation(), Mathf.random(0, Math.max(realLength - lLength * 8f, 4f)));
                Lightning.create(b.team, lightColor, fromLaserDamage, b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), lLength);
            }
        }
        if(Mathf.chance(incendChance)){
            Tmp.v1.trns(b.rotation(), Mathf.random(incendStart, realLength));
            Damage.createIncend(b.x + Tmp.v1.x, b.y + Tmp.v2.y, incendSpread, incendAmount);
        }

        if(extinction){
            if(b.timer(2, 15f)){
                Funcs.castCone(b.x, b.y, length * 0.8f, b.rotation(), 70, (tile, build, dst, angD) -> {
                    if(Mathf.chance(angD * 0.2f * Mathf.clamp(dst * 1.7f))) Fires.create(tile);
                    if(build != null && b.team != build.team){
                        build.damage(angD * 23.3f * Mathf.clamp(dst * 1.7f));
                        ExtraEffect.addMoltenBlock(build);
                    }
                });
            }
            Funcs.castCone(b.x, b.y, length * 0.8f, b.rotation(), 70f, (e, dst, angD) -> {
                float clamped = Mathf.clamp(dst * 1.7f);
                if(!e.dead){
                    float damageMulti = e.team != b.team ? 0.25f : 1f;
                    e.damage(28f * angD * dst * damageMulti);
                    Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 160f * (e.hitSize / 20f + 0.95f));
                    e.impulse(Tmp.v1);
                    if(Mathf.chanceDelta(Mathf.clamp(angD * dst * 12f) * 0.9f)) ExtraEffect.createEvaporation(e.x, e.y, e, b.owner);
                    e.apply(UnityStatusEffects.radiation, angD * damageMulti * 3800.3f * clamped);
                    e.apply(StatusEffects.melting, angD * damageMulti * 240.3f * clamped);
                }else{
                    tempSeq.add(e);
                    Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 130f / Math.max(e.mass() / 120f + 119f / 120f, 1f));
                    Tmp.v1.scl(12f);
                    UnityFx.evaporateDeath.at(e.x, e.y, 0f, new UnitVecData(e, Tmp.v1.cpy()));
                    for(int i = 0; i < 12; i++) ExtraEffect.createEvaporation(e.x, e.y, e, b.owner);
                }
            });
            tempSeq.each(e -> {
                e.remove();
            });
            tempSeq.clear();
        }
    }

    @Override
    public void init(Bullet b){
        if(extinction){
            Funcs.castCone(b.x, b.y, length * 1.3f, b.rotation(), 80f, (tile, build, dst, angD) -> {
                if(Mathf.chance(angD * 0.9f * Mathf.clamp(dst * 1.7f))) Fires.create(tile);
                if(build != null && b.team != build.team) build.damage(angD * 258.3f * Mathf.clamp(dst * 1.7f));
            }, (e, dst, angD) -> {
                float damageMulti = e.team != b.team ? 0.25f : 1f;
                e.apply(StatusEffects.melting, angD * damageMulti * 1200.3f * Mathf.clamp(dst * 1.7f));
            });
        }
    }
}
