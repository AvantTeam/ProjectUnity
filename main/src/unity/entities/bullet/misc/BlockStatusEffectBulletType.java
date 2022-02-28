package unity.entities.bullet.misc;

import arc.math.Mathf;
import unity.entities.ExpOrbs;
import unity.world.blocks.defense.turrets.BlockOverdriveTurret;
import unity.world.blocks.exp.ExpHolder;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;

public class BlockStatusEffectBulletType extends BasicBulletType{
    public float strength = 2f;
    public int amount = 3;
    public boolean upgrade = false;

    public BlockStatusEffectBulletType(float speed, float damage){
        super(speed, damage);
    }

    @Override
    public void draw(Bullet b){
        //no
    }

    @Override
    public void update(Bullet b){
        Building target = ((BlockOverdriveTurret.BlockOverdriveTurretBuild) b.owner).target;
        boolean buffing = ((BlockOverdriveTurret.BlockOverdriveTurretBuild) b.owner).buffing;

        if (buffing){
            if (b.x == target.x && b.y == target.y){
                strength = Mathf.lerpDelta(strength, 3f, 0.02f);
                if (b.timer(0, 179f)) {
                    if (upgrade) addExp(target, 5, true);
                    else buff(target, strength);
                }
            }
        }
    }

    public void buff(Building b, float intensity){
        b.applyBoost(intensity, 180f);
        if (b.health < b.maxHealth) b.heal(intensity);
        else addExp(b, 1, false);
    }

    public void addExp(Building b, int intensity, boolean spread){
        if (b instanceof ExpHolder) {
            ((ExpHolder) b).handleExp(Mathf.round(((ExpHolder) b).getExp() / 10) / 10 * intensity);
            if (spread) ExpOrbs.spreadExp(b.x, b.y, amount);
        }
    }
}