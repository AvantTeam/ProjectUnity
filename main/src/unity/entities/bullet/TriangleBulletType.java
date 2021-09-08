package unity.entities.bullet;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class TriangleBulletType extends BulletType{
    /** Gets a random number between 0 to the specified value then add it with the lifetime of this bullet. */
    public float lifetimeRand = 0f;
    /** Whether or not it can cast lightning. */
    public boolean castsLightning = false;
    /** The interval in ticks to cast lightning. */
    public int castInterval = 12;
    /** The radius to detect the closest enemy. */
    public float castRadius = 8f;
    public Sound castSound = Sounds.spark;
    public float castSoundVolume = 0.4f;

    public float length, width;
    public Color color = Pal.surge;

    public TriangleBulletType(float length, float width, float speed, float damage){
        super(speed, damage);

        this.length = length;
        this.width = width;
        trailColor = lightningColor = Pal.surge;
        hitColor = Color.valueOf("f2e87b");
    }

    public TriangleBulletType(float speed, float damage){
        this(1, 1, speed, damage);
    }
    
    public TriangleBulletType(){
        this(1, 1, 1, 1);
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        b.lifetime = b.lifetime + Mathf.random(lifetimeRand);
    }

    @Override
    public void draw(Bullet b){
        drawTrail(b);

        Draw.color(lightningColor);
        Drawf.tri(b.x, b.y, width, length, b.rotation());
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        Teamc target = Units.closestTarget(b.team, b.x, b.y, castRadius * tilesize);

        if(castsLightning && target != null && b.timer.get(1, castInterval)){
            castSound.at(b.x, b.y, 1, castSoundVolume);
            Lightning.create(b.team, lightningColor, damage, b.x, b.y, b.angleTo(target), (int)(b.dst(target) / tilesize + 2));
        }
    }
}