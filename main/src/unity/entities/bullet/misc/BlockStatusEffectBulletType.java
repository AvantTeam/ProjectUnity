package unity.entities.bullet.misc;

import arc.math.Mathf;
import unity.world.blocks.defense.turrets.BlockOverdriveTurret;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

public class BlockStatusEffectBulletType extends BasicBulletType{
    public float strength = 2f;

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

        if (target != null){
            if (b.x == target.x && b.y == target.y){
                strength = Mathf.lerpDelta(1f, strength, 0.08f);
                target.applyBoost(strength, 60f);
                if (target.health < target.maxHealth){
                    target.heal(strength);
                }
            }
        }
    }
}