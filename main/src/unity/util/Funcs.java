package unity.util;

import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class Funcs{
    private static final Vec2 tV = new Vec2();
    private static final IntSet collidedBlocks = new IntSet();
    private static final Rect rect = new Rect();
    private static final Rect hitRect = new Rect();
    private static Unit result;
    private static float cdist;

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
    public static void trueEachBlock(int wx, int wy, float range, Cons<Building> cons){
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
            
            float dst = unit.dst(x, y);
            
            if(Mathf.dst2(x, y, unit.x, unit.y) < cdist && !targetSeq.contains(unit)){
                result = unit;
                cdist = dst;
            }
        });
        
        if(result == null) result = targetSeq.random();
        return result;
    }
    
    /** The other version of Damage.collideLine */
    public static void collideLineDamageOnly(Team team, float damage, int x, int y, float angle, float length, Bullet hitter){
        collidedBlocks.clear();
        tV.trns(angle, length);
        
        if(hitter.type.collidesGround){
            world.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                
                if(tile != null && !collidedBlocks.contains(tile.pos()) && tile.team != team){
                    tile.damage(damage);
                    collidedBlocks.add(tile.pos());
                }
                
                return false;
            });
        }
        
        rect.setPosition(x, y).setSize(tV.x, tV.y);
        float x2 = tV.x + x, y2 = tV.y + y;
        
        if(rect.width < 0){
            rect.x += rect.width;
            rect.width *= -1;
        }
        if(rect.height < 0){
            rect.y += rect.height;
            rect.height *= -1;
        }
        
        float expand = 3f;
        
        rect.y -= expand;
        rect.x -= expand;
        rect.width += expand * 2;
        rect.height += expand * 2;
        
        Units.nearbyEnemies(team, rect, unit -> {
            if(!unit.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) return;
            unit.hitbox(hitRect);

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitRect.grow(expand * 2));

            if(vec != null) unit.damage(damage);
		});
    }
    
    public static void chanceMultiple(float chance, Runnable run){
        int intC = Mathf.ceil(chance);
        float tmp = chance;
        
        for(int i = 0; i < intC; i++){
            if(tmp >= 1){
                run.run();
                tmp -= 1;
            }else if(tmp > 0){
                if(Mathf.chance(tmp)) run.run();
            }
        }
    }
}