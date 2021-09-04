package unity.world.blocks.defense.turrets;

import arc.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/**
 * @author GlennFolker
 * @author ThePythonGuy3
*/
public class AbsorberTurret extends GenericTractorBeamTurret<Teamc>{
    public float powerProduction = 2.5f;
    public float resistance = 0.4f;
    public float damageScale = 18f;
    public float damage = 0f;
    public float speedScale = 3.5f;

    public StatusEffect status;

    public boolean targetBullets, targetUnits, targetBuildings = false;

    private Seq<Building> buildings = new Seq<>();

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
            findTarget(x, y, range);
        }

        @Override
        protected void findTarget(Vec2 pos){
            findTarget(pos.x, pos.y, laserWidth / 2f);
        }

        protected void findTarget(float x, float y, float r){
            Teamc tempTarget = null;
            target = null;
            float distance = Float.MAX_VALUE;

            if(targetBullets) {
                tempTarget = Groups.bullet
                        .intersect(x - r, y - r, r * 2f, r * 2f)
                        .min(b -> b.team != team && b.type().hittable, b -> b.dst2(x, y));

                if (tempTarget != null) {
                    target = tempTarget;
                    distance = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                }
            }

            if(targetUnits) {
                tempTarget = Groups.unit
                        .intersect(x - r, y - r, r * 2f, r * 2f)
                        .min(b -> b.team != team && !b.dead, b -> b.dst2(x, y));

                if (tempTarget != null) {
                    float d = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                    if (d < distance) {
                        distance = d;
                        target = tempTarget;
                    }
                }
            }

            if(targetBuildings) {
                buildings.clear();

                Vars.indexer.eachBlock(null, x, y, r, b -> b.team != team && !b.dead, buildings::add);

                tempTarget = buildings.min(b -> b.dst2(x, y));

                if (tempTarget != null) {
                    float d = Mathf.dst(x, y, tempTarget.x(), tempTarget.y());
                    if (d < distance)target = tempTarget;
                }
            }
        }

        @Override
        protected void apply(){
            if(target instanceof Bullet bullet) {
                bullet.vel.setLength(Math.max(bullet.vel.len() - resistance * strength, 0f));
                bullet.damage = Math.max(bullet.damage - (resistance / 2f) * strength * Time.delta, 0f);

                if (bullet.vel.isZero(0.01f) || bullet.damage <= 0f) {
                    bullet.remove();
                }
            }

            if(target instanceof Unit unit && damage > 0f) {
                unit.apply(status);
                unit.damage(damage);
            }

            if(target instanceof Building building && damage > 0f){
                building.damage(damage);
            }
        }

        @Override
        public float getPowerProduction(){
            if(target == null) return 0f;

            if(target instanceof Bullet bullet) {
                if(bullet.type == null) return 0f;

                return (bullet.type.damage / damageScale) * (bullet.vel.len() / speedScale) * powerProduction;
            }

            if(target instanceof Unit unit) {
                if(unit.type == null) return 0f;

                return (unit.type.dpsEstimate / damageScale) * (unit.vel.len() / speedScale) * powerProduction;
            }

            //TODO add Building support (why tho)

            return 0f;
        }
    }
}
