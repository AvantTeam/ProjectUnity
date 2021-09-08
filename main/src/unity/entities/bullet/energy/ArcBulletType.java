package unity.entities.bullet.energy;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

public class ArcBulletType extends BulletType{
    public Color fromColor = Color.valueOf("6c8fc7"), toColor = Color.valueOf("606571"), lightningC1 = Pal.lancerLaser, lightningC2 = Color.valueOf("8494b3"), smokeColor = Pal.bulletYellowBack;
    public int length1, length2 = 8, lengthRand1, lengthRand2 = 4;
    public float lightningDamage1, lightningDamage2;
    public float lightningInaccuracy1, lightningInaccuracy2 = 180f, radius = 12f;
    public float smokeChance, lightningChance1, lightningChance2;
    public Effect arcSmokeEffect = UnityFx.arcSmoke, arcSmokeEffect2 = UnityFx.arcSmoke2;

    public ArcBulletType(float speed, float damage){
        super(speed, damage);
        
        despawnEffect = shootEffect = Fx.none;
        collidesTiles = hittable = false;
        pierce = true;
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        
        if(Mathf.chanceDelta(smokeChance)){
            arcSmokeEffect.at(b.x + Mathf.range(2f), b.y + Mathf.range(2f), b.rotation(), smokeColor);
        }

        if(Mathf.chanceDelta(lightningChance1)){
            Tmp.v1.trns(b.rotation() + Mathf.range(2f), radius);
            Lightning.create(b, lightningC1, lightningDamage1, b.x + Tmp.v1.x + Mathf.range(radius), b.y + Tmp.v1.y + Mathf.range(radius), b.rotation() + Mathf.range(lightningInaccuracy1), length1 + Mathf.range(lengthRand1));
        }

        if(Mathf.chanceDelta(lightningChance2)){
            Tmp.v1.trns(b.rotation() + Mathf.range(2f), radius);
            Lightning.create(b, lightningC1, lightningDamage2, b.x + Tmp.v1.x + Mathf.range(radius), b.y + Tmp.v1.y + Mathf.range(radius), b.rotation() + Mathf.range(lightningInaccuracy2), length2 + Mathf.range(lengthRand2));
        }

        if(Mathf.chanceDelta(1f)){
            arcSmokeEffect2.at(b.x + Mathf.range(radius), b.y + Mathf.range(radius), b.rotation() + Mathf.range(2f), smokeColor);
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.color(fromColor, toColor, b.fin());
        Fill.poly(b.x, b.y, 6, 6f + b.fout() * 6.1f, b.rotation());
		Draw.reset();
    }
}
