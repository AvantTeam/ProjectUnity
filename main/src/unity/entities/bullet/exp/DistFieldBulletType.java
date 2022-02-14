package unity.entities.bullet.exp;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.content.*;
import unity.entities.bullet.exp.*;
import unity.gen.Expc.*;
import unity.gen.*;
import unity.world.blocks.exp.ExpTurret;
import unity.world.blocks.exp.turrets.ExpPowerTurret;

public class DistFieldBulletType extends ExpBulletType{
    public Color centerColor, edgeColor;
    public Effect distSplashFx, distStart;
    public StatusEffect distStatus;
    public float radius, radiusInc;
    public float damageLimit, distDamage;
    public float bulletSlowMultiplier;

    public DistFieldBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void draw(Bullet b){
        float radius = getRadius(b);

        Draw.color(Pal.lancerLaser);
        Lines.stroke(1);
        Lines.circle(b.x, b.y, Mathf.clamp((1 - b.fin()) * 20) * radius);

        float centerf = centerColor.toFloatBits();
        float edgef = edgeColor.cpy().a(0.3f + 0.25f * Mathf.sin(b.time() * 0.05f)).toFloatBits();
        float sides = Mathf.ceil(Lines.circleVertices(radius) / 2f) * 2;
        float space = 360f / sides;
        float dp = 5;
        for(int i = 0; i < sides; i += 2){
            float px = Angles.trnsx(space * i, Mathf.clamp((1 - b.fin()) * dp) * radius);
            float py = Angles.trnsy(space * i, Mathf.clamp((1 - b.fin()) * dp) * radius);
            float px2 = Angles.trnsx(space * (i + 1), Mathf.clamp((1 - b.fin()) * dp) * radius);
            float py2 = Angles.trnsy(space * (i + 1), Mathf.clamp((1 - b.fin()) * dp) * radius);
            float px3 = Angles.trnsx(space * (i + 2), Mathf.clamp((1 - b.fin()) * dp) * radius);
            float py3 = Angles.trnsy(space * (i + 2), Mathf.clamp((1 - b.fin()) * dp) * radius);
            Fill.quad(b.x, b.y, centerf, b.x + px, b.y + py, edgef, b.x + px2, b.y + py2, edgef, b.x + px3, b.y + py3, edgef);
        }

        Draw.color();
    }

    @Override
    public void hit(Bullet b, float x, float y){
        //Do nothing
    }

    @Override
    public void despawned(Bullet b){
        //Do nothing
    }

    float getRadius(Bullet b){
        return radius + radiusInc * getLevel(b) * b.damageMultiplier();
    }

    @Override
    public void update(Bullet b){
        float temp = b.lifetime/4f;
        float radius = getRadius(b);

        if(b.time() % temp <= 1 && b.lifetime() - b.time() > 100){
            distSplashFx.at(b.x, b.y, 0, new Float[]{radius, temp});
        }

        Units.nearbyEnemies(b.team, b.x, b.y, radius, e -> {
            if(b.owner instanceof ExpBuildc block){
                if(block.levelf() < 1 && Core.settings.getBool("hitexpeffect"))
                    for(int i = 0; i < Math.ceil(expGain); i++) UnityFx.expGain.at(e.x, e.y, 0f, block);
                block.incExp(expGain);
            }

            e.apply(distStatus, 2);
            e.damage(distDamage);
        });

        Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2f, radius * 2f, e ->{
            if(e.team != b.team && e.damage() <= damageLimit && !(e.owner instanceof Kami)){ //slow down bullets which is not from kami
                if(b.owner instanceof ExpBuildc block){
                    if(block.levelf() < 1 && Core.settings.getBool("hitexpeffect"))
                        for(int i = 0; i < Math.ceil(expGain); i++) UnityFx.expGain.at(e.x, e.y, 0f, block);
                    block.incExp(expGain / 30f);
                }
                e.vel.scl(bulletSlowMultiplier);
            }
        });

    }

    @Override
    public void init(Bullet b){
        if(b == null) return;
        float radius = getRadius(b);

        distStart.at(b.x, b.y, 0, radius);
    }
}
