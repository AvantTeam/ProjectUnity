package unity.content;

import arc.math.*;
import mindustry.entities.Units;
import mindustry.entities.bullet.*;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.blocks.defense.turrets.*;

public class ShieldTurret extends ChargeTurret {

    public ShieldTurret(String name){
        super(name);
        // nothing for now
    }

    public class ShieldTurretBuild extends ChargeTurretBuild {
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
            return this.target != null;
        }

        public boolean targetShield(Building t, ShieldTurretBuild b, float radius){

            Groups.bullet.intersect(t.x-radius, t.y-radius, radius*2, radius*2, e -> {
                if(e != null && e.team == b.team && e.data != null && ((Object[]) e.data)[2] != null && ((Object[]) e.data)[2] == "shield"){
                    shield = true;
                }
	        });

            shield = !shield;

            return t.damaged() && shield;
        }

    }

}
