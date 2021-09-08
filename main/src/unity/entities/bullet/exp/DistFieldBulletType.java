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

public class DistFieldBulletType extends ExpBulletType {
    public Color centerColor, edgeColor;
    public Effect distSplashFx, distStart;
    public StatusEffect distStatus;
    public float fieldRadius;
    public float bulletSlowMultiplier;
    public float damageLimit;
    public float distDamage;

    public DistFieldBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void draw(Bullet b){
        final float radius;

        if(b.data instanceof Float[]) radius = ((Float[]) b.data)[0];
        else if(b.data instanceof Float) radius = (float)b.data;
        else radius = fieldRadius;

        Draw.color(Pal.lancerLaser);
        Lines.stroke(1);
        Lines.circle(b.x, b.y, Mathf.clamp((1 - b.fin()) * 20) * radius);

        float centerf = centerColor.toFloatBits();
        float edgef = edgeColor.cpy().a(0.7f + 0.25f * Mathf.sin(b.time() * 0.05f)).toFloatBits();
        float sides = Mathf.ceil(Lines.circleVertices(radius) / 2f) * 2;
        float space = 360f / sides;

        for(int i = 0; i < sides; i += 2){
            float px = Angles.trnsx(space * i, Mathf.clamp((1 - b.fin()) * 20) * radius);
            float py = Angles.trnsy(space * i, Mathf.clamp((1 - b.fin()) * 20) * radius);
            float px2 = Angles.trnsx(space * (i + 1), Mathf.clamp((1 - b.fin()) * 20) * radius);
            float py2 = Angles.trnsy(space * (i + 1), Mathf.clamp((1 - b.fin()) * 20) * radius);
            float px3 = Angles.trnsx(space * (i + 2), Mathf.clamp((1 - b.fin()) * 20) * radius);
            float py3 = Angles.trnsy(space * (i + 2), Mathf.clamp((1 - b.fin()) * 20) * radius);
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

    @Override
    public void update(Bullet b){
        int temp = 80;
        final float radius;

        if(b.data instanceof Float[]){
            radius = ((Float[]) b.data)[0];
            temp /= ((Float[]) b.data)[1];
        }

        else if(b.data instanceof Float) radius = (float)b.data;
        else radius = fieldRadius;

        if(b.time() % temp <= 1 && b.lifetime() - b.time() > 100){
            if((b.data instanceof Float[])) distSplashFx.at(b.x, b.y, 0, new Float[]{radius, (float)temp});
            else distSplashFx.at(b.x, b.y, 0, radius);
        }

        Units.nearbyEnemies(b.team, b.x - radius, b.y - radius, b.x + radius, b.y + radius, e -> {

            if(Mathf.within(b.x, b.y, e.x, e.y, radius)){
                if(b.owner instanceof ExpBuildc block){
                    if(block.levelf() < 1 && Core.settings.getBool("hitexpeffect"))
                        for(int i = 0; i < Math.ceil(expGain); i++) UnityFx.expGain.at(e.x, e.y, 0f, block);
                    block.incExp(expGain);
                }

                e.apply(distStatus, 2);
                e.damage(distDamage);
            }
        });

        Groups.bullet.intersect(b.x - radius, b.y - radius, b.x + radius, b.y + radius, e -> {
            if(Mathf.within(b.x, b.y, e.x, e.y, radius) && e.team != b.team){
                if(e.damage() > damageLimit) return;
                if(b.owner instanceof ExpBuildc block){
                    if(block.levelf() < 1 && Core.settings.getBool("hitexpeffect"))
                        for(int i = 0; i < Math.ceil(expGain); i++) UnityFx.expGain.at(e.x, e.y, 0f, block);
                    block.incExp(expGain / 30f);
                }
                if(!(e.owner instanceof Kami)){ //nerf
                    e.vel.x = e.vel.x * bulletSlowMultiplier;
                    e.vel.y = e.vel.y * bulletSlowMultiplier;
                }
            }
        });

    }

    @Override
    public void init(Bullet b){
        if(b == null) return;
        final float radius;

        if(b.data instanceof Float[]) radius = ((Float[]) b.data)[0];
        else if(b.data instanceof Float) radius = (float)b.data;
        else radius = fieldRadius;
        distStart.at(b.x, b.y, 0, radius);
    }
}
