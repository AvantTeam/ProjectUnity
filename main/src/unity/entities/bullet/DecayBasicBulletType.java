package unity.entities.bullet;

import arc.math.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class DecayBasicBulletType extends BasicBulletType{
    public float minInterval = 0.75f, maxInterval = 1.75f;
    public float decayMinVel = 0.9f, decayMaxVel = 1.1f;
    public float decayMinLife = 0.3f, decayMaxLife = 1.3f;
    public float trailChanceAlt = 0.4f;
    public BulletType decayBullet;

    public DecayBasicBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
    }

    @Override
    public float estimateDPS(){
        float total = decayBullet.estimateDPS() * (lifetime / Math.max(1f, (minInterval + maxInterval) / 2f));
        return super.estimateDPS() + (total / 3f);
    }

    @Override
    public void update(Bullet b){
        super.update(b);
        if(b.timer(1, Mathf.lerp(maxInterval, minInterval, b.fin()))){
            decayBullet.create(b, b.team, b.x, b.y, b.rotation() + Mathf.range(180f), Mathf.random(decayMinVel, decayMaxVel), Mathf.lerp(decayMaxLife, decayMinLife, b.fin()));
        }
        if(trailChanceAlt > 0){
            if(Mathf.chanceDelta(trailChanceAlt)){
                trailEffect.at(b.x, b.y, b.rotation(), trailColor);
            }
        }
    }
}
