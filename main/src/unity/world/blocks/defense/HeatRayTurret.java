package unity.world.blocks.defense;

import arc.math.geom.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

/** @author GlennFolker */
public class HeatRayTurret extends GenericTractorBeamTurret<Teamc>{
    public boolean targetAir = true;
    public boolean targetGround = true;

    public float damage = 60f;
    public StatusEffect status = StatusEffects.melting;
    public float statusDuration = 60f;

    public HeatRayTurret(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();

        //TODO display status
        stats.add(Stat.damage, damage / 60f, StatUnit.perSecond);
        stats.add(Stat.targetsAir, targetAir);
        stats.add(Stat.targetsGround, targetGround);
    }

    public class HeatRayTurretBuild extends GenericTractorBeamTurretBuild{
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
                h.damageContinuous((damage / 60f) * efficiency());
            }

            if(target instanceof Unitc unit){
                unit.apply(status, statusDuration);
            }
        }
    }
}
