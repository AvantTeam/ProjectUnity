package unity.world.blocks.defense.turrets;

import arc.math.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.entities.bullet.imber.*;

public class ShieldTurret extends PowerTurret{

    public ShieldTurret(String name){
        super(name);
        // nothing for now
    }

    public class ShieldTurretBuild extends PowerTurretBuild{
        public boolean shield;

        @Override
        public void bullet(BulletType type, float angle){
            float spdScl = Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / range, 0, 1);

            type.create(this, team, x + tr.x, y + tr.y, angle, spdScl, 1);
        }

        @Override
        public void findTarget(){
            this.target = Units.findAllyTile(team, x, y, range, e -> targetShield(e, this, 10) && e != this);
        }

        @Override
        public boolean validateTarget(){
            return this.target != null || isControlled() || logicControlled();
        }

        public boolean targetShield(Building t, ShieldTurretBuild b, float radius){

            Groups.bullet.intersect(t.x - radius, t.y - radius, radius * 2, radius * 2, e -> {
                if(e != null && e.team == b.team && e.type instanceof ShieldBulletType){
                    shield = true;
                }
            });

            shield = !shield;

            return t.damaged() && shield;
        }

    }

}
