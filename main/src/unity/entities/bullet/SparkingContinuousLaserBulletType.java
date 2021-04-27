package unity.entities.bullet;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.gen.*;
import unity.content.*;
import unity.entities.*;
import unity.graphics.UnityPal;
import unity.util.Utils;
import mindustry.content.StatusEffects;
import mindustry.entities.*;
import mindustry.entities.bullet.ContinuousLaserBulletType;

//lmao
public class SparkingContinuousLaserBulletType extends ContinuousLaserBulletType{
    protected float fromBlockChance = 0.4f, fromBlockDamage = 23f,
        fromLaserChance = 0.9f, fromLaserDamage = 23f,
        incendStart = 2.9f,
        coneRange = 1.1f;
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
            Damage.createIncend(b.x + Tmp.v1.x, b.y + Tmp.v1.y, incendSpread, incendAmount);
        }

        if(extinction){
            if(b.timer(2, 15f)){
                b.data = Utils.castConeTile(b.x, b.y, length * coneRange, b.rotation(), 70f, 45, (build, tile) -> {
                    float angD = Mathf.clamp(1f - (Utils.angleDist(Angles.angle(tile.worldx() - b.x, tile.worldy() - b.y), b.rotation()) / 70f));
                    float dst = Mathf.clamp(1f - (Mathf.dst(tile.worldx() - b.x, tile.worldy() - b.y) / (length * coneRange)));
                    if(Mathf.chance(Interp.smooth.apply(angD) * 0.32f * Mathf.clamp(dst * 1.7f))) Fires.create(tile);
                    //UnityFx.tilePosIndicatorTest.at(tile.worldx(), tile.worldy());
                    if(build != null && build.team != b.team){
                        build.damage(Interp.smooth.apply(angD) * 23.3f * Mathf.clamp(dst * 1.7f));
                        ExtraEffect.addMoltenBlock(build);
                    }
                }, tile -> tile.block().absorbLasers || tile.block().insulated);
            }

            if(b.data instanceof float[] data){
                Utils.castCone(b.x, b.y, length * coneRange, b.rotation(), 70f, (e, dst, angD) -> {
                    float clamped = Mathf.clamp(dst * 1.7f);
                    int idx = Mathf.clamp(Mathf.round(((Utils.angleDistSigned(b.angleTo(e), b.rotation()) + 70f) / 140f) * (data.length - 1)), 0, data.length - 1);
                    boolean h = (b.dst2(e) + (e.hitSize / 2f)) < data[idx] || e.isFlying();
                    if(h){
                        if(!e.dead){
                            float damageMulti = e.team != b.team ? 0.25f : 1f;
                            e.damage(28f * angD * dst * damageMulti * Time.delta);
                            Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 160f * (e.hitSize / 20f + 0.95f));
                            e.impulse(Tmp.v1);
                            if(Mathf.chanceDelta(Mathf.clamp(angD * dst * 12f) * 0.9f)) ExtraEffect.createEvaporation(e.x, e.y, (angD * dst) / 70f, e, b.owner);
                            e.apply(UnityStatusEffects.radiation, angD * damageMulti * 3800.3f * clamped);
                            e.apply(StatusEffects.melting, angD * damageMulti * 240.3f * clamped);
                        }else{
                            tempSeq.add(e);
                            Tmp.v1.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * clamped * 130f / Math.max(e.mass() / 120f + 119f / 120f, 1f));
                            Tmp.v1.scl(12f);
                            UnityFx.evaporateDeath.at(e.x, e.y, 0f, new UnitVecData(e, Tmp.v1.cpy()));
                            //for(int i = 0; i < 12; i++) ExtraEffect.createEvaporation(e.x, e.y, e, b.owner);
                            for(int i = 0; i < 12; i++){
                                Tmp.v1.trns(Angles.angle(b.x, b.y, e.x(), e.y()), 65f + Mathf.range(0.3f)).add(e);
                                Tmp.v2.trns(Mathf.random(360f), Mathf.random(e.hitSize / 1.25f));
                                UnityFx.vaporation.at(e.x, e.y, 0f, new Position[]{e, Tmp.v1.cpy(), Tmp.v2.cpy()});
                            }
                        }
                    }
                });
                tempSeq.each(Unitc::remove);
                tempSeq.clear();
            }
        }
    }
}
