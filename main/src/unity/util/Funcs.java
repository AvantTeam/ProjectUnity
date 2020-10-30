package unity.util;

import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;
import arc.func.Cons;
import arc.graphics.g2d.*;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class Funcs{
    private static final Vec2 tV = new Vec2();
    private static final IntSet collidedBlocks = new IntSet(127);
    private static final Rect rect = new Rect(), hitRect = new Rect();
    private static Unit result;
    private static float cdist;

    public static void simpleUnitDrawer(Unit unit, boolean drawLegs){
        UnitType type = unit.type;
        if(drawLegs){
            //TODO
            if(unit instanceof Mechc){

            }
        }
        Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90f);
        for(int i = 0, len = unit.mounts.length; i < len; i++){
            WeaponMount mount = unit.mounts[i];
            Weapon weapon = mount.weapon;
            float rot = unit.rotation - 90f;
            float weaponRot = rot + (weapon.rotate ? mount.rotation : 0f);
            float recoil = -mount.reload / weapon.reload * weapon.recoil;
            float wx = unit.x + Angles.trnsx(rot, weapon.x, weapon.y) + Angles.trnsx(weaponRot, 0f, recoil);
            float wy = unit.y + Angles.trnsy(rot, weapon.x, weapon.y) + Angles.trnsy(weaponRot, 0f, recoil);
            TextureRegion weaponReg = weapon.region;
            Draw.rect(weaponReg, wx, wy, weaponReg.width * Draw.scl * -Mathf.sign(weapon.flipSprite), weaponReg.height * Draw.scl, weaponRot);
        }
    }

    public static void trueEachBlock(float wx, float wy, float range, Cons<Building> cons){
        collidedBlocks.clear();
        int tx = World.toTile(wx);
        int ty = World.toTile(wy);
        int tileRange = Mathf.floorPositive(range / tilesize + 1f);
        for(int x = -tileRange + tx; x <= tileRange + tx; x++){
            for(int y = -tileRange + ty; y <= tileRange + ty; y++){
                if(!Mathf.within(x * tilesize, y * tilesize, wx, wy, range)) continue;
                Building other = world.build(x, y);
                if(other == null) continue;
                if(!collidedBlocks.contains(other.pos())){
                    cons.get(other);
                    collidedBlocks.add(other.pos());
                }
            }
        }
    }

    public static Unit targetUnique(Team team, float x, float y, float radius, Seq<Unit> targetSeq){
        result = null;
        cdist = radius * radius + 1f;
        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2f, radius * 2f, e -> {
            float dst = e.dst2(x, y);
            if(!targetSeq.contains(e) && dst < radius * radius && dst < cdist){
                result = e;
                cdist = dst;
            }
        });
        if(result == null) result = targetSeq.random();
        return result;
    }
    /*
    public static boolean collideLineDamageOnly(Team team, float damage, float x, float y, float angle, float length,
    	BulletType bulletType){
    	collidedBlocks.clear();
    	tV.trns(angle,length);
    	if(bulletType.collidesGround) {
    		
    	}
    }*/
}
