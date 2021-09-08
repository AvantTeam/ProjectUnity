package unity.entities.bullet.energy;

import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class DecayBasicBulletType extends BasicBulletType{
    public float backMinRadius = 3f, frontMinRadius = 1.75f;
    public float backRadius = 6f, frontRadius = 5.75f;
    public float minInterval = 0.75f, maxInterval = 1.75f;
    public float decayMinVel = 0.9f, decayMaxVel = 1.1f;
    public float decayMinLife = 0.3f, decayMaxLife = 1.3f;
    public Effect decayEffect = Fx.none;
    public BulletType decayBullet;

    public DecayBasicBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public float estimateDPS(){
        float total = decayBullet.estimateDPS() * (lifetime / Math.max(1f, (minInterval + maxInterval) / 2f));
        return super.estimateDPS() + (total / 3f);
    }
    
    @Override
    public void draw(Bullet b){
        Draw.color(backColor);
        Fill.circle(b.x, b.y, backMinRadius + b.fout() * backRadius);
        Draw.color(frontColor);
        Fill.circle(b.x, b.y, frontMinRadius + b.fout() * frontRadius);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.timer(1, Mathf.lerp(maxInterval, minInterval, b.fin()))){
            decayEffect.at(b.x, b.y, b.rotation() + 180f, trailColor);
            decayBullet.create(b, b.team, b.x, b.y, b.rotation() + Mathf.range(180f), Mathf.random(decayMinVel, decayMaxVel), Mathf.lerp(decayMaxLife, decayMinLife, b.fin()));
        }
    }
}
