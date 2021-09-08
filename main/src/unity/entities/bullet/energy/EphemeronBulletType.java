package unity.entities.bullet.energy;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

public class EphemeronBulletType extends BasicBulletType{
    public Color midColor = Pal.lancerLaser;
    public float[] baseRadius = {11f, 8f, 6.5f}, extraRadius = {2.5f, 1.5f, 1f};
    public float maxRadius = 80f;
    public int pairs = 15;
    public BulletType positive, negative;

    public EphemeronBulletType(float speed, float damage){
        super(speed, damage);

        hittable = false;
        backColor = Color.valueOf("a9d8ff60");
        frontColor = Color.white;
    }

    @Override
    public void draw(Bullet b){
        Draw.color(backColor);
		Fill.circle(b.x, b.y, baseRadius[0] + (b.fout() * extraRadius[0]));
		Draw.color(midColor);
		Fill.circle(b.x, b.y, baseRadius[1] + (b.fout() * extraRadius[1]));
		Draw.color(frontColor);
		Fill.circle(b.x, b.y, baseRadius[2] + (b.fout() * extraRadius[2]));
    }

    @Override
    public void despawned(Bullet b){
        super.despawned(b);

        for(int i = 0; i < pairs; i++){
            Tmp.v1.rnd(Mathf.range(maxRadius)).add(b);

            float randomSign = Mathf.random(180f);
			float randomB = Mathf.random(0.2f, 1.4f);
			float angleRandom = Mathf.range(360f);
			float rangeRandom = Mathf.range(40f, 70f);

            Tmp.v2.trns(angleRandom, rangeRandom);
            Bullet pos = positive.create(b, Tmp.v1.x + Tmp.v2.x, Tmp.v1.y + Tmp.v2.y, angleRandom + randomSign);
            
            Tmp.v2.rotate(180f);
            Bullet neg = negative.create(b, Tmp.v1.x + Tmp.v2.x, Tmp.v1.y + Tmp.v2.y, angleRandom + randomSign + 180f);

            pos.data = neg;
            neg.data = pos;
            
            Tmp.v2.trns(angleRandom + randomSign, randomB);
            pos.vel.add(Tmp.v2);
            neg.vel.add(Tmp.v2.rotate(180f));

            UnityFx.ephemeronLaser.at(b.x, b.y, 0f, ((BasicBulletType)pos.type).frontColor, new EphemeronEffectData(pos, b.x, b.y));
            UnityFx.ephemeronLaser.at(b.x, b.y, 0f, ((BasicBulletType)neg.type).frontColor, new EphemeronEffectData(neg, b.x, b.y));
        }
    }

    public class EphemeronEffectData{
        public Bullet b;
        public float x, y;

        public EphemeronEffectData(Bullet b, float x, float y){
            this.b = b;
            this.x = x;
            this.y = y;
        }
    }
}