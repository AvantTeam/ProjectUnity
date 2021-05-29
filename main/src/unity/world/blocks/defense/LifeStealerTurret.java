package unity.world.blocks.defense;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.content.*;
import unity.graphics.*;

import static mindustry.Vars.*;

/** @author GlennFolker */
public class LifeStealerTurret extends GenericTractorBeamTurret<Teamc>{
    public boolean targetAir = true;
    public boolean targetGround = true;

    public float damage = 60f;
    public float maxContain = 100f;
    public float healPercent = 0.05f;

    public Color healColor = UnityPal.monolithLight;
    public Effect healEffect = Fx.healBlockFull;
    public Effect healTrnsEffect = UnityFx.supernovaPullEffect;

    public LifeStealerTurret(String name){
        super(name);
    }

    public class LifeStealerTurretBuild extends GenericTractorBeamTurretBuild{
        public float contained;

        @Override
        protected void findTarget(){
            target = Units.closestTarget(team, x, y, range,
                unit -> unit.team != team && unit.isValid() && unit.checkTarget(targetAir, targetGround),
                tile -> targetGround && tile.isValid()
            );
        }

        @Override
        protected void findTarget(Vec2 pos){
            target = Units.closestTarget(team, pos.x, pos.y, laserWidth,
                unit -> unit.team != team && unit.isValid() && unit.checkTarget(targetAir, targetGround),
                tile -> targetGround && tile.isValid()
            );
        }

        @Override
        protected void apply(){
            if(target instanceof Healthc h){
                float health = (damage / 60f) * efficiency();
                h.damageContinuous(health);
                contained += health * Time.delta;
            }

            if(contained >= maxContain){
                tryHeal();
            }
        }

        protected void tryHeal(){
            boolean any = indexer.eachBlock(this, range, b -> b.health() < b.maxHealth(), b -> {
                healTrnsEffect.at(x, y, rotation, new Float[]{x, y, b.x, b.y, 2.5f + Mathf.range(0.3f)});
                Time.run(healEffect.lifetime, () -> {
                    if(b.isValid()){
                        healEffect.at(b.x, b.y, b.block.size, healColor);
                        b.healFract(healPercent);
                    }
                });
            });

            if(any){
                contained %= maxContain;
            }
        }
    }
}
