package unity.world.blocks.defense;

import mindustry.entities.Effect;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;
import unity.content.UnityFx;

import static arc.Core.bundle;

public class LimitWall extends Wall{
    protected Effect maxDamageFx = UnityFx.maxDamageFx, withstandFx = UnityFx.withstandFx;
    protected float maxDamage = 30f, over9000 = 90000000;

    public LimitWall(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.abilities, "@", bundle.format("stat.unity.maxDamage", maxDamage));
    }

    public class LimitWallBuild extends WallBuild{
        @Override
        public float handleDamage(float amount){
            if(amount > maxDamage && amount < over9000){
                withstandFx.at(x, y, size);
                return super.handleDamage(Math.min(amount, maxDamage));
            }
            return super.handleDamage(amount);
        }
    }
}
