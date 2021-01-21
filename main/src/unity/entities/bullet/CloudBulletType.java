package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

public class CloudBulletType extends BasicBulletType{
    /** Starting size */
    public float baseSize = 3f;
    /** Amount it grows by the end of its lifetime */
    public float growAmount = 4.1f;
    /** Trail spawn randomness */
    public float trailRand = 0.6f;
    /** Smoke spawn randomness */
    public float smokeRand = 1.7f;

    public CloudBulletType(float speed, float damage){
        super(speed, damage);
    }
    
    public CloudBulletType(){
        this(1, 1);
    }
    
    @Override
    public void update(Bullet b){
        super.update(b);
        
        if(b.timer.get(0, 1)){
            UnityFx.advanceFlameTrail.at(b.x + Mathf.range(trailRand), b.y + Mathf.range(trailRand), b.rotation());
		}
		
		if(Mathf.chanceDelta(0.7f)){
            UnityFx.advanceFlameSmoke.at(b.x + Mathf.range(smokeRand), b.y + Mathf.range(smokeRand), b.rotation());
		}
	}
	
    @Override
	public void draw(Bullet b){
		Draw.color(Pal.lancerLaser, Color.valueOf("4f72e1"), b.fin());
		Fill.poly(b.x, b.y, 6, baseSize + b.fin() * growAmount, b.rotation() + b.fin() * 270f);
		Draw.reset();
	}
}