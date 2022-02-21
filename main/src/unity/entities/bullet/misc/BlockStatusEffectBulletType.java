package unity.entities.bullet.misc;

import arc.math.Mathf;
import unity.world.blocks.defense.turrets.BlockOverdriveTurret;
import unity.world.blocks.exp.ExpHolder;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.*;

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
        boolean buffing = ((BlockOverdriveTurret.BlockOverdriveTurretBuild) b.owner).buffing;

        if (buffing){
            strength = Mathf.lerpDelta(strength, 3f, 0.03f);
            if (b.x == target.x && b.y == target.y){
                target.applyBoost(strength, 180f);
                if (target.health < target.maxHealth){
                    target.heal(strength);
                }else if (target instanceof ExpHolder exp){
                    if (b.timer(0, 179f)){
                        exp.handleExp(exp.getExp() / 100);
                    }
                }
            }
        }else{
            strength = 1f;
        }
    }
}