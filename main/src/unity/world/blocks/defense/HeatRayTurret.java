package unity.world.blocks.defense;

import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;

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

    public class HeatRayTurretBuild extends GenericTractorBeamTurretBuild{
        @Override
        protected void findTarget(){
            target = Units.closestTarget(team, x, y, range, 
                unit -> unit.team != team && unit.isValid() && ((unit.isFlying() && targetAir) || (unit.isGrounded() && targetGround)),
                tile -> tile.isValid() && targetGround
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
