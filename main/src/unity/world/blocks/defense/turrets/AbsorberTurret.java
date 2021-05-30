package unity.world.blocks.defense.turrets;

import arc.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/** @author GlennFolker */
public class AbsorberTurret extends GenericTractorBeamTurret<Bullet>{
    public float powerProduction = 2.5f;
    public float resistance = 0.4f;
    public float damageScale = 18f;
    public float speedScale = 3.5f;

    public AbsorberTurret(String name){
        super(name);
        outputsPower = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();

        bars.add("power", (AbsorberTurretBuild entity) -> new Bar(() ->
            Core.bundle.format("bar.poweroutput",
            Strings.fixed(entity.getPowerProduction() * 60f * entity.timeScale(), 1)),
            () -> Pal.powerBar,
            () -> entity.getPowerProduction() / powerProduction)
        );
    }

    public class AbsorberTurretBuild extends GenericTractorBeamTurretBuild{
        @Override
        protected void findTarget(){
            target = Groups.bullet
                .intersect(x - range, y - range, range * 2f, range * 2f)
                .min(b -> b.team != team && b.type().hittable, b -> b.dst2(this));
        }

        @Override
        protected void findTarget(Vec2 pos){
            float r = laserWidth / 2f;
            target = Groups.bullet
                .intersect(pos.x - r, pos.y - r, r * 2f, r * 2f)
                .min(b -> b.team != team && b.type().hittable, b -> b.dst2(pos));
        }

        @Override
        protected void apply(){
            target.vel.setLength(Math.max(target.vel.len() - resistance * strength, 0f));
            target.damage = Math.max((resistance / 2f) * strength * Time.delta, 0f);

            if(target.vel.isZero(0.01f) || target.damage <= 0f){
                target.remove();
            }
        }

        @Override
        public float getPowerProduction(){
            if(target == null || target.type == null) return 0f;

            return (target.type.damage / damageScale) * (target.vel.len() / speedScale) * powerProduction;
        }
    }
}
