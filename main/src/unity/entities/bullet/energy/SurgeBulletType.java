package unity.entities.bullet.energy;

import arc.graphics.*;
import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SurgeBulletType extends BasicBulletType{
    /** Amount of lightning spawned on despawn. */
    public int shocks;
    /** Despawn lightning damage */
    public float shockDamage;
    /** Despawn lightning length */
    public int shockLength;

    public SurgeBulletType(float speed, float damage){
        super(speed, damage);

        lightningColor = Pal.surge;
        hitColor = Color.valueOf("f2e87b");
        //sprite = "unity-large-bomb"; //Does not exist
        sprite = "large-bomb"; //Use Quad bomb instead I guess.
    }
    
    public SurgeBulletType(){
        this(1, 1);
    }
    
    @Override
    public void despawned(Bullet b){
        super.despawned(b);
        
        for(int i = 0; i < shocks; i++){
            Lightning.create(b, lightningColor, shockDamage, b.x, b.y, Mathf.random(360), shockLength);
        }
    }
}