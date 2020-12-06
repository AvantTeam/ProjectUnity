package unity.entities.bullet;

import arc.*;
import arc.audio.*;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

public class ShieldBulletType extends BasicBulletType{
    /** Shield stats */
    public float shieldHealth = 3000f;
    public float maxRadius = 10f;
    /** Other shield stuff */
    public Sound breakSound;
    public Effect breakFx = new Effect(5, e -> {
        Draw.z(Layer.shields);
        Draw.color(e.color);
        float radius = ((int)e.data) * e.fout();

        if(Core.settings.getBool("animatedshields")){
            Fill.poly(e.x, e.y, 6, radius);
        }else{
            Lines.stroke(1.5f);
            Draw.alpha(0.09f);
            Fill.poly(e.x, e.y, 6, radius);
            Draw.alpha(1);
            Lines.poly(e.x, e.y, 6, radius);
            Draw.reset();
        }
        Draw.z(Layer.block);
    });

    public ShieldBulletType(float speed){
        super(speed, 0);
        /** Please add drag, it can end up very bad */
        drag = 0.3f;
        lifetime = 20000f;
        shootEffect = Fx.none;
        despawnEffect = Fx.none;
        collides = false;
        hitSize = 0;
        hittable = false;
        hitEffect = Fx.none;
    }

    @Override
    public void update(Bullet b){
        if(b.data == null){
            float[] data = new float[2];
            data[0] = shieldHealth;
            data[1] = 0f;
            b.data = data;
        }

        float radius = (((speed - b.vel.len()) * maxRadius) + 1) * 0.8f;
        float[] temp = (float[])b.data;
        Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2, radius * 2, e -> {
            if(e != null && e.team != b.team){
                if(e.owner instanceof TurretBuild build){
                    if(build.block.name != "unity-shielder"){
                        float health = temp[0] - e.damage;
                        temp[0] = health;
                        temp[1] = 1;
                        e.remove();
                    }
                }else{
                    float health = temp[0] - e.damage;
                    temp[0] = health;
                    temp[1] = 1;
                    e.remove();
                }

            }
        });

        if(temp[0] <= 0){
            breakSound.at(b.x, b.y, Mathf.random(0.8f, 1));
            breakFx.at(b.x, b.y, 0, b.team.color, radius);
            b.remove();
        }

        if(temp[0] > 0){
            float hit = temp[1] - 1f - 0.2f * ((float)Time.delta);
            temp[1] = hit;
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.shields);
        if(b.data == null) return;
        float[] temp = (float[])b.data;
        Draw.color(b.team.color, Color.white, Mathf.clamp(temp[1]));

        float radius = ((speed - b.vel.len()) * maxRadius) + 1;

        if(Core.settings.getBool("animatedshields")){
            Fill.poly(b.x, b.y, 6, radius);
        }else{
            Lines.stroke(1.5f);
            Draw.alpha(0.09f + Mathf.clamp(0.08f * temp[1]));
            Fill.poly(b.x, b.y, 6, radius);
            Draw.alpha(1);
            Lines.poly(b.x, b.y, 6, radius);
            Draw.reset();
        }
        Draw.z(Layer.block);

        Draw.color();
    }
}