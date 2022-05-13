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

import static unity.util.Utils.*;

public class AnomalyLaserBulletType extends BulletType{
    float length = 250f;
    float lengthFalloff = 0.6f;
    float sideLength = 29f, sideWidth = 0.7f;
    float sideAngle = 90f;

    Color[] colors = {Pal.lancerLaser.cpy().mul(0.9f).a(0.3f), Pal.lancerLaser, Color.white};

    public AnomalyLaserBulletType(float damage){
        super(0f, damage);
        despawnEffect = Fx.none;
        hitEffect = Fx.lancerLaserShoot;
        keepVelocity = false;
        lifetime = 20f;
    }

    @Override
    public float calculateRange(){
        return length;
    }

    @Override
    public void init(){
        super.init();
        drawSize = (length + 250f * 1.5f) * 2f;
    }

    @Override
    public void init(Bullet b){
        float charge = Math.max(b.damage - damage, 0f);
        b.fdata = length + (charge * 1.5f);
        float d = damage * ((charge / 70f) + 1f);
        float size = Mathf.sqrt(charge / 3f);
        int lighLength = Math.min(Mathf.round((charge - 30f) / 9f), 18),
        lighAmount = Math.min(Mathf.ceil((charge - 30f) / 60), 3);

        Tmp.v1.trns(b.rotation(), b.fdata).add(b);
        collideLineRawEnemyRatio(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, Math.max(size, 8f), (building, ratio, direct) -> {
            if(direct){
                building.damage(d * ratio);
            }
            if(building.block.absorbLasers){
                b.fdata = b.dst(building);
            }
            return building.block.absorbLasers;
        }, (unit, ratio) -> {
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f * ratio);
            if(impact) Tmp.v3.setAngle(b.rotation() + (knockback < 0 ? 180f : 0f));
            unit.impulse(Tmp.v3);
            unit.apply(status, statusDuration);

            unit.damage(d * ratio);
            return false;
        }, (ex, ey) -> {
            hit(b, ex, ey);
            if(lighLength >= 5 && lighAmount > 0){
                for(int i = 0; i < lighAmount; i++){
                    int len = Mathf.random(lighLength / 2, lighLength);
                    Lightning.create(b.team, lightningColor, d / 5f, ex, ey, b.rotation() + Mathf.range(25f), len);
                }
            }
        });
    }

    @Override
    public void draw(Bullet b){
        float realLength = b.fdata;
        float f = Mathf.curve(b.fin(), 0f, 0.2f);
        float charge = Math.max(b.damage - damage, 0f);
        float width = (Mathf.sqrt(charge / 3f) + 3f) * 2.5f;
        float cw = width / lengthFalloff;
        float compound = 1f;
        float baseLen = realLength * f;
        float sLength = charge / 6f;
        sLength *= sLength / 2f;
        sLength /= 5f;

        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
        for(Color color : colors){
            Draw.color(color);
            Lines.stroke((cw *= lengthFalloff) * b.fout());
            Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);
            Tmp.v1.trns(b.rotation(), baseLen).add(b);
            Drawf.tri(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() * 1.22f, cw * 2f + width / 2f, b.rotation());
            Fill.circle(b.x, b.y, cw * b.fout());

            float offset = Math.min((charge - 40f) / 7f, 30f);

            for(int i : Mathf.signs){
                if(offset <= 0f){
                    Drawf.tri(b.x, b.y, sideWidth * b.fout() * cw, (sideLength + sLength) * compound, b.rotation() + (sideAngle * i));
                }else{
                    for(int s : Mathf.signs){
                        Drawf.tri(b.x, b.y, sideWidth * b.fout() * cw, (sideLength + sLength) * compound, b.rotation() + (sideAngle * i) + (offset * s));
                    }
                }
            }

            compound *= lengthFalloff;
        }
        Draw.reset();

        Tmp.v1.trns(b.rotation(), baseLen * 1.1f).add(b);
        Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 1.7f * b.fout(), colors[0], 0.6f);
    }

    @Override
    public void drawLight(Bullet b){

    }
}
