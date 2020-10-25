package unity.entities.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.*;
import mindustry.entities.*;
import mindustry.entities.bullet.ContinuousLaserBulletType;

//lmao
public class SparkingContinuousLaserBulletType extends ContinuousLaserBulletType{
    protected float fromBlockChance = 0.4f, fromLaserChance = 0.9f, fromBlockDamage = 23f, fromLaserDamage = 23f, incendStart = 2.9f;
    protected int fromLaserLen = 4, fromLaserLenRand = 5, fromBlockLen = 2, fromBlockLenRand = 5, fromLaserAmount = 1;

    public SparkingContinuousLaserBulletType(float damage){
        super(damage);
        lightningColor = Color.valueOf("ff9c5a");
    }

    public SparkingContinuousLaserBulletType(){
        this(0);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        float realLength = Damage.findLaserLength(b, length);
        if(Mathf.chanceDelta(fromBlockChance)) Lightning.create(b.team, lightningColor, fromBlockDamage, b.x, b.y, b.rotation(), Mathf.round(length / (float) 8) + fromBlockLen + Mathf.random(fromBlockLenRand));
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
    }
}
