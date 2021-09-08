package unity.entities.bullet.advance;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.graphics.*;

public class AdvanceFlameBulletType extends BulletType{
    public float orbSize = 3f;
    public Color fromColor = UnityPal.advance;
    public Color toColor = UnityPal.advanceDark;
    public Effect smokeTrail;
    public Effect flameTrail;
    public float smokeTrailChance = 0.7f;
    public float flameRange = 0.6f;
    public float smokeRange = 1.7f;

    public AdvanceFlameBulletType(float speed, float damage){
        this.speed = speed;
        this.damage = damage;
        drag = 0.016f;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        pierce = true;
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.timer(0, 1f) && flameTrail != null) flameTrail.at(b.x + Mathf.range(flameRange), b.y + Mathf.range(flameRange), b.rotation());

        if(Mathf.chanceDelta(smokeTrailChance) && smokeTrail != null) smokeTrail.at(b.x + Mathf.range(smokeRange), b.y + Mathf.range(smokeRange), b.rotation());
    }

    @Override
    public void draw(Bullet b){
        float rot = Mathf.randomSeed(b.id, -1, 1) * 270f;
        Draw.color(fromColor, toColor, b.fin());
        Fill.poly(b.x, b.y, 6, orbSize + b.fin() * 4.1f, b.rotation() + (b.fin() * rot));
    }
}
