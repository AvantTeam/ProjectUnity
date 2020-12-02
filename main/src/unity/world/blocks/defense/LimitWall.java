package unity.world.blocks.defense;

import mindustry.entities.Effect;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import unity.content.UnityFx;
import unity.graphics.UnityPal;

import static arc.Core.bundle;

import arc.util.Time;

public class LimitWall extends Wall{
    protected Effect maxDamageFx = UnityFx.maxDamageFx, withstandFx = UnityFx.withstandFx, blinkFx = UnityFx.blinkFx;
    protected float maxDamage = 30f, over9000 = 90000000, blinkFrame = -1f;

    public LimitWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        if(maxDamage > 0f && blinkFrame > 0f) stats.add(Stat.abilities, "@\n@", bundle.format("stat.unity.maxDamage", maxDamage), bundle.format("stat.unity.blinkFrame", blinkFrame));
        else if(maxDamage > 0f) stats.add(Stat.abilities, "@", bundle.format("stat.unity.maxDamage", maxDamage));
        else if(blinkFrame > 0f) stats.add(Stat.abilities, "@", bundle.format("stat.unity.blinkFrame", blinkFrame));
    }

    public class LimitWallBuild extends WallBuild{
        protected float blink;

        @Override
        public float handleDamage(float amount){
            if(blinkFrame > 0f){
                if(Time.time - blink >= blinkFrame){
                    blink = Time.time;
                    blinkFx.at(x, y, size);
                }else return 0;
            }
            if(maxDamage > 0f && amount > maxDamage && amount < over9000){
                withstandFx.at(x, y, size);
                return super.handleDamage(Math.min(amount, maxDamage));
            }
            return super.handleDamage(amount);
        }
    }
}
