package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class TriangleBulletType extends BasicBulletType{
    /** Picks a random number between 0 to lifetimeRand and add it with lifetiime */
    public float lifetimeRand = 0f;
    public boolean summonsLightning = false;
    /** The delay in tick(s) to summon lightning */
    public int lightningDelay = 12;
    public float detectRadius = 8f;
    
    public float trailWidth = 0f;
    public int trailLength = 0;
    
    public TriangleBulletType(float speed, float damage){
        super(speed, damage);
        
        lightningColor = Pal.surge;
        hitColor = Color.valueOf("f2e87b");
    }
    
    public TriangleBulletType(){
        this(1, 1);
    }
    
    @Override
    public void init(Bullet b){
        super.init(b);
        
        b.data = new Trail(trailLength);
        lifetime = lifetime + Mathf.random(lifetimeRand);
    }
    
    @Override
    public void draw(Bullet b){
        ((Trail) b.data).draw(lightningColor, trailWidth);
        
        Draw.color(lightningColor);
        Drawf.tri(b.x, b.y, width, height, b.rotation());
    }
    
    @Override
    public void update(Bullet b){
        super.update(b);
        
        Teamc target = Units.closestTarget(b.team, b.x, b.y, detectRadius * tilesize);
        
        ((Trail) b.data).update(b.x, b.y);
        if(summonsLightning && target != null && b.timer.get(1, lightningDelay)){
            Lightning.create(b.team, lightningColor, damage, b.x, b.y, b.angleTo(target), (int)(b.dst(target) / tilesize + 2));
        }
    }
}