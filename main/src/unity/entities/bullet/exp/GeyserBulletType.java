package unity.entities.bullet.exp;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.content.*;

import static arc.graphics.g2d.Lines.*;
import static mindustry.Vars.world;
import static mindustry.gen.Nulls.unit;

public class GeyserBulletType extends ExpBulletType{
    protected static final Rand rand = new Rand();
    public float radius = 25f;
    public float radiusInc = 2.5f;

    public Effect spawnEffect = UnityFx.giantSplash;
    public Effect hotSmokeEffect = UnityFx.hotSteam;
    public Effect coldSmokeEffect = UnityFx.iceSheet;
    public float puddleSpeed = 60f;
    public float smallEffectChance = 0.02f;

    public float a = 1f;
    public int particles = 25;
    public float particleLife = 50f, particleLen = 9f;
    public float shake = 1f;

    public GeyserBulletType(float lifetime, float damage){
        super(0.0001f, damage);
        this.lifetime = lifetime;
        collides = false;
        collidesAir = collidesGround = collidesTiles = false;
        absorbable = hittable = false;
        knockback = 10f;
        statusDuration = 60f;
        despawnEffect = Fx.none;
        hitEffect = Fx.none;
        expChance = 0.07f;
        expOnHit = false;
    }

    public GeyserBulletType(){
        this(200f, 20f);
    }

    @Override
    public void init(){
        super.init();
        drawSize = radius * 2f + 8f;
        despawnHit = false;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        Effect.shake(shake, b.lifetime, b);
        spawnEffect.at(b.x, b.y, 0, getLiquid(b).color);
    }

    public Liquid getLiquid(Bullet b){
        return b.data instanceof Liquid l ? l : Liquids.water;
    }

    public float getRad(Bullet b){
        return (getLevel(b) * radiusInc + radius) * b.fout(0.9f);
    }

    public float damageScale(Liquid l){
        return 1f + l.explosiveness + Math.abs(l.temperature - 0.5f);
    }

    public float knockbackScale(Liquid l){
        return Math.max(0f, 1f - damageScale(l)) * l.viscosity;
    }

    public void effects(Bullet b, Liquid l){
        if(l.temperature < 0.3f) coldSmokeEffect.at(b.x, b.y, l.color);
        else hotSmokeEffect.at(b.x, b.y, l.temperature > 1.4f ? Color.gray : l.temperature > 0.7f ? Color.lightGray : Color.white);

        if(l.effect != StatusEffects.none && Mathf.chance(smallEffectChance) && l.effect.effect != Fx.none){
            Tmp.v1.trns(Mathf.random(360f), Mathf.random() * getRad(b) * 1.1f);
            l.effect.effect.at(Tmp.v1.x, Tmp.v1.y, 0, l.effect.color, null);
        }
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        float rad = getRad(b);
        Liquid l = getLiquid(b);

        Units.nearbyEnemies(b.team, b.x, b.y, rad, unit -> {
            Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f * knockbackScale(l) * Mathf.clamp(1f - 0.9f * unit.dst2(b) / (rad * rad)));
            unit.impulse(Tmp.v3);
            unit.apply(l.effect, statusDuration);
            unit.damageContinuousPierce(b.damage * damageScale(l));
            if(Mathf.chance(expChance)) handleExp(b, unit.x, unit.y, expGain);
        });

        if(world.tileWorld(b.x, b.y) != null) Puddles.deposit(world.tileWorld(b.x, b.y), l, 25f);
        effects(b, l);
    }

    @Override
    public void draw(Bullet b){
        float rad = getRad(b);
        Liquid l = getLiquid(b);

        //the rings
        Draw.z(Layer.debris - 1);
        Draw.color(l.color, Color.white, 0.3f);
        for(int i = 0; i < 2; i++){
            float fin = ((Time.time + i * puddleSpeed / 2f) % puddleSpeed) / puddleSpeed;
            float fout = Mathf.clamp((1-fin) * 5f);
            stroke(fout * 6f);
            Draw.alpha(0.3f);
            circle(b.x, b.y, rad * fin * fin);
            stroke(fout * 5f);
            Draw.alpha(0.3f);
            circle(b.x, b.y, rad * fin * fin);
            stroke(fout * 3f);
            Draw.alpha(1f);
            circle(b.x, b.y, rad * fin * fin);
        }

        //the geyser
        Draw.z(Layer.bullet + 1);
        float base = (Time.time / particleLife);
        rand.setSeed(b.id);
        for(int i = 0; i < particles; i++){
            float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
            float angle = rand.random(360f);
            float len = rand.random(0.3f, 0.7f) * Interp.pow3Out.apply(fin) * rad;
            float roff = rand.random(0.8f, 1.1f);
            Draw.color(Tmp.c1.set(l.color).mul(0.5f + fout * 0.5f).lerp(Color.white, fout * 0.2f + rand.random(0f, 0.2f)), a);
            Fill.circle(b.x + Angles.trnsx(angle, len), b.y + Angles.trnsy(angle, len), particleLen * roff * fout * Mathf.clamp(fin * 5f) * b.fout(0.9f) + 0.01f);
        }

        Draw.reset();
    }
}
