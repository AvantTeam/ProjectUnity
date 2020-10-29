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
import mindustry.world.blocks.defense.turrets.ChargeTurret;

public class ShieldBulletType extends BasicBulletType{
    /** Shield stats */
    public float shieldHealth = 3000f;
    public float maxRadius = 10f;
    /** Other shield stuff */
    public Sound breakSound;
    public Effect breakFx = new Effect(5, e -> {
        Draw.z(Layer.shields);
        Draw.color(e.color);
        float radius = ((int) e.data) * e.fout();

        if(Core.settings.getBool("animatedshields")){
            Fill.poly(e.x, e.y, 6, radius);
        } else {
            Lines.stroke(1.5f);
            Draw.alpha(0.09f);
            Fill.poly(e.x, e.y, 6, radius);
            Draw.alpha(1);
            Lines.poly(e.x, e.y, 6, radius);
            Draw.reset();
        }
	    Draw.z(Layer.block);

	    Draw.color();
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
            Object[] data = new Object[3];
            data[0] = shieldHealth;
            data[1] = 0f;
            data[2] = "shield";
            b.data = data;
        }

        float radius = (((speed-b.vel.len())*maxRadius)+1)*0.8f;

        Groups.bullet.intersect(b.x-radius, b.y-radius, radius*2, radius*2, e -> {
            if(e != null && e.team != b.team){
                if(e.owner instanceof Building){
                    if(((ChargeTurret.ChargeTurretBuild) e.owner).block.name != "unity-shielder"){
                        float health = (float) ((Object[]) b.data)[0] - e.damage;
                        ((Object[]) b.data)[0] = health;
                        ((Object[]) b.data)[1] = 1;
                        e.remove();
                    }
                } else {
                    float health = (float) ((Object[]) b.data)[0] - e.damage;
                    ((Object[]) b.data)[0] = health;
                    ((Object[]) b.data)[1] = 1;
                    e.remove();
                }

            }
		});

        if((float) ((Object[]) b.data)[0] <= 0){
            breakSound.at(b.x, b.y, Mathf.random(0.8f, 1));
            breakFx.at(b.x, b.y, 0, b.team.color, radius);
            b.remove();
        }

        if((float) ((Object[]) b.data)[0] > 0){
            float hit = ((float) ((Object[]) b.data)[1]) - 1f - 0.2f * ((float) Time.delta);
            ((Object[]) b.data)[1] = hit;
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.z(Layer.shields);
        Draw.color(b.team.color, Color.white, b.data != null ? Mathf.clamp((float) ((Object[]) b.data)[1]) : 0);

        float radius = ((speed-b.vel.len())*maxRadius)+1;

        if(Core.settings.getBool("animatedshields")){
            Fill.poly(b.x, b.y, 6, radius);
        } else {
            Lines.stroke(1.5f);
            Draw.alpha(0.09f + Mathf.clamp(0.08f * ((float) ((Object[]) b.data)[1])));
            Fill.poly(b.x, b.y, 6, radius);
            Draw.alpha(1);
            Lines.poly(b.x, b.y, 6, radius);
            Draw.reset();
        }
        Draw.z(Layer.block);

        Draw.color();
    }
}