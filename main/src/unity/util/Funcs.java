package unity.util;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class Funcs{
	private static final Vec2 tV = new Vec2();
	private static final IntSet collidedBlocks = new IntSet();
	private static final Rect rect = new Rect();
	private static final Rect hitRect = new Rect();

    /** Same thing like the drawer from UnitType without applyColor and outlines. */
	public static void simpleUnitDrawer(Unit unit, boolean drawLegs){
	    UnitType type = unit.type;
	    
	    if(drawLegs){
	        if(unit instanceof Mechc){
	            //TODO draw the legs
	        }
	    }
	    
	    Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90f);
	    
	    for(WeaponMount mount : unit.mounts){
	        Weapon weapon = mount.weapon;
            
	        float rotation = unit.rotation - 90f;
	        float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0f);
			float recoil = -(mount.reload / weapon.reload * weapon.recoil);
            
			float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, recoil);
			float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, recoil);
            
            Draw.rect(weapon.region, wx, wy, weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite), weapon.region.height * Draw.scl, weaponRotation);
	    }
	}
    
    /** Iterates over all blocks in a radius. */
    public static void Building trueEachBlock(int wx, int wy, float range, Cons<Building> cons){
        collidedBlocks.clear();
        
        int tx = world.toTile(wx);
        int ty = world.toTile(wy);
        int tileRange = Mathf.floorPositive(range / tilesize + 1);
        
        for(int x = -tileRange + tx; x <= tileRange + tx; x++){
            yGroup:
            for(int y = -tileRange + ty; y <= tileRange + ty; y++){
                if(!Mathf.within(x * tilesize, y * tilesize, wx, wy, range)) continue yGroup;
                Building other = world.build(x, y);
                
                if(other == null) continue yGroup;
                if(!collidedBlocks.contains(other.pos())){
                    cons.get(other);
                    collidedBlocks.add(other.pos());
                }
            }
        }
    }
    
    /** Targets any units that is not in the array.
     *  @returns the unit, picks a random target if all potential targets is in the array.
     */
    public static Unit targetUnique(Team team, int x, int y, float radius, Seq<Unit> targetSeq){
        result = null;
        cdist = (radius * radius) + 1;
        
        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2, radius * 2, unit -> {
            if(!unit.within(x, y, radius)) return;
            
            dst = unit.dst(x, y);
            
            if(Mathf.dst2(x, y, unit.x, unit.y) < cdist && !targetSeq.contains(unit)){
                result = unit;
                cdist = dst;
            }
        });
        
        if(result == null) result = targetSeq.random();
        return result;
    }
}
