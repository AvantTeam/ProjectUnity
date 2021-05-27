package unity.util;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.Interp.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.graphics.*;

import static mindustry.Vars.*;

public final class Utils{
    public static final PowIn pow6In = new PowIn(6);

    private static final Vec2 tV = new Vec2();
    private static final Seq<Healthc> tmpUnitSeq = new Seq<>();
    private static final IntMap<Float[]> effectArray = new IntMap<>(204);
    private static final IntSet collidedBlocks = new IntSet();
    private static final Rect rect = new Rect(), rectAlt = new Rect(), hitRect = new Rect();
    private static Posc result;
    private static float cdist;
    private static int idx;
    private static Tile furthest;
    private static Building tmpBuilding;
    private static Unit tmpUnit;
    private static boolean hit;

    public static <T extends Buildingc> Tile getBestTile(T build, int before, int after){
        Tile tile = build.tile();
        int bound = before - after + 1;
        int offset = Mathf.floorPositive(bound / 2);

        if(bound % 2 == 0 && after % 2 == 0) offset--;
        offset *= -1;

        int minScore = bound * bound * 2;
        Tile ctile = null;

        for(int i = offset; i < offset + bound; i++){
            for(int j = offset; j < offset + bound; j++){
                if(Math.max(Math.abs(i), Math.abs(j)) < minScore && notSolid(tile, before, i, j)){
                    minScore = Math.max(Math.abs(i), Math.abs(j));
                    ctile = tile.nearby(i, j);
                }
            }
        }

        return ctile;
    }

    public static boolean notSolid(Tile tile, int size, int x, int y){
        Tile ttile = tile.nearby(x, y);
        int off = Mathf.floorPositive((size - 1) / 2) * -1;

        for(int i = off; i < size + off; i++){
            for(int j = off; j < size + off; j++){
                Tile check = ttile.nearby(i, j);

                if(check.solid()){
                    if(check.build != null && check.build.tile == tile){
                        continue;
                    }else{
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean hasBuilding(float wx, float wy, float range, Boolf<Building> pred){
        collidedBlocks.clear();

        int tx = World.toTile(wx);
        int ty = World.toTile(wy);

        int tileRange = (int)(range / tilesize + 1);
        boolean any = false;

        loop:
        for(int x = -tileRange + tx; x <= tileRange + tx; x++){
            for(int y = -tileRange + ty; y <= tileRange + ty; y++){
                if(!Mathf.within(x * tilesize, y * tilesize, wx, wy, range)) continue;

                Building other = world.build(x, y);

                if(other == null) continue;

                if(pred.get(other) && collidedBlocks.add(other.pos())){
                    any = true;
                    break loop;
                }
            }
        }

        return any;
    }

    public static float angleDistSigned(float a, float b){
        a += 360f;
        a %= 360f;
        b += 360f;
        b %= 360f;
        float d = Math.abs(a - b) % 360f;
        int sign = (a - b >= 0f && a - b <= 180f) || (a - b <= -180f && a - b >= -360f) ? 1 : -1;
        return (d > 180f ? 360f - d : d) * sign;
    }

    public static float angleDistSigned(float a, float b, float start){
        float dst = angleDistSigned(a, b);
        if(Math.abs(dst) > start){
            return dst > 0 ? dst - start : dst + start;
        }
        return 0f;
    }

    public static float angleDist(float a, float b){
        float d = Math.abs(a - b) % 360f;
        return (d > 180f ? 360f - d : d);
    }

    public static float clampedAngle(float angle, float relative, float limit){
        if(limit > 360) return angle;
        float dst = angleDistSigned(angle, relative);
        if(Math.abs(dst) > limit){
            float val = dst > 0 ? dst - limit : dst + limit;
            return (angle - val) % 360f;
        }
        return angle;
    }

    /** Same thing like the drawer from UnitType without applyColor and outlines. */
    public static void simpleUnitDrawer(Unit unit, boolean drawLegs){
        UnitType type = unit.type;

        if(drawLegs){
            if(unit instanceof Mechc){
                //TODO draw the legs
            }
        }

        Draw.rect(type.region, unit.x, unit.y, unit.rotation - 90f);
        float rotation = unit.rotation - 90f;
        for(WeaponMount mount : unit.mounts){
            Weapon weapon = mount.weapon;

            float weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0f);
            float recoil = -(mount.reload / weapon.reload * weapon.recoil);

            float wx = unit.x + Angles.trnsx(rotation, weapon.x, weapon.y) + Angles.trnsx(weaponRotation, 0f, recoil);
            float wy = unit.y + Angles.trnsy(rotation, weapon.x, weapon.y) + Angles.trnsy(weaponRotation, 0f, recoil);

            Draw.rect(weapon.region, wx, wy, weapon.region.width * Draw.scl * -Mathf.sign(weapon.flipSprite), weapon.region.height * Draw.scl, weaponRotation);
        }
    }

    public static void shotgunRange(int points, float range, float angle, Floatc cons){
        if(points <= 1){
            cons.get(angle);
            return;
        }
        for(int i = 0; i < points; i++){
            float in = Mathf.lerp(-range, range, i / (points - 1f));
            cons.get(in + angle);
        }
    }

    public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, int rays, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator){
        return castConeTile(wx, wy, range, angle, cone, consBuilding, insulator, new float[rays]);
    }

    public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator, float[] ref){
        collidedBlocks.clear();
        idx = 0;
        float expand = 3;
        rect.setCentered(wx, wy, expand);
        shotgunRange(3, cone, angle, con -> {
            tV.trns(con, range).add(wx, wy);
            rectAlt.setCentered(tV.x, tV.y, expand);
            rect.merge(rectAlt);
        });
        if(insulator != null){
            shotgunRange(ref.length, cone, angle, con -> {
                tV.trns(con, range).add(wx, wy);
                ref[idx] = range * range;
                world.raycastEachWorld(wx, wy, tV.x, tV.y, (x, y) -> {
                    Tile tile = world.tile(x, y);
                    if(tile != null && insulator.get(tile)){
                        ref[idx] = Mathf.dst2(wx, wy, x * tilesize, y * tilesize);
                        return true;
                    }
                    return false;
                });
                idx++;
            });
        }
        int tx = Mathf.round(rect.x / tilesize);
        int ty = Mathf.round(rect.y / tilesize);
        int tw = tx + Mathf.round(rect.width / tilesize);
        int th = ty + Mathf.round(rect.height / tilesize);
        for(int x = tx; x <= tw; x++){
            for(int y = ty; y <= th; y++){
                float ofX = (x * tilesize) - wx, ofY = (y * tilesize) - wy;
                int angIdx = Mathf.clamp(Mathf.round(((angleDistSigned(Angles.angle(ofX, ofY), angle) + cone) / (cone * 2f)) * (ref.length - 1)), 0, ref.length - 1);
                float dst = ref[angIdx];
                float dst2 = Mathf.dst2(ofX, ofY);
                if(dst2 < dst && dst2 < range * range && angleDist(Angles.angle(ofX, ofY), angle) < cone){
                    Tile tile = world.tile(x, y);
                    Building building = null;
                    if(tile != null){
                        Building b = world.build(x, y);
                        if(b != null && !collidedBlocks.contains(b.id)){
                            building = b;
                            collidedBlocks.add(b.id);
                        }
                        consBuilding.get(building, tile);
                    }
                }
            }
        }
        collidedBlocks.clear();
        return ref;
    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons4<Tile, Building, Float, Float> consTile, Cons3<Unit, Float, Float> consUnit){
        collidedBlocks.clear();
        float expand = 3;
        float rangeSquare = range * range;
        if(consTile != null){
            rect.setCentered(wx, wy, expand);
            for(int i = 0; i < 3; i++){
                float angleC = (-1 + i) * cone + angle;
                tV.trns(angleC, range).add(wx, wy);
                rectAlt.setCentered(tV.x, tV.y, expand);
                rect.merge(rectAlt);
            }
            int tx = Mathf.round(rect.x / tilesize);
            int ty = Mathf.round(rect.y / tilesize);
            int tw = tx + Mathf.round(rect.width / tilesize);
            int th = ty + Mathf.round(rect.height / tilesize);
            for(int x = tx; x <= tw; x++){
                for(int y = ty; y <= th; y++){
                    float temp = Angles.angle(wx, wy, x * tilesize, y * tilesize);
                    float tempDst = Mathf.dst(x * tilesize, y * tilesize, wx, wy);
                    if(tempDst >= rangeSquare || !Angles.within(temp, angle, cone)) continue;
                    Tile other = world.tile(x, y);
                    if(other == null) continue;
                    if(!collidedBlocks.contains(other.pos())){
                        float dst = 1f - tempDst / range;
                        float anDst = 1f - Angles.angleDist(temp, angle) / cone;
                        consTile.get(other, other.build, dst, anDst);
                        collidedBlocks.add(other.pos());
                    }
                }
            }
        }
        if(consUnit != null){
            Groups.unit.intersect(wx - range, wy - range, range * 2f, range * 2f, e -> {
                float temp = Angles.angle(wx, wy, e.x, e.y);
                float tempDst = Mathf.dst(e.x, e.y, wx, wy);
                if(tempDst >= rangeSquare || !Angles.within(temp, angle, cone)) return;
                float dst = 1f - tempDst / range;
                float anDst = 1f - Angles.angleDist(temp, angle) / cone;
                consUnit.get(e, dst, anDst);
            });
        }
    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons4<Tile, Building, Float, Float> consTile){
        castCone(wx, wy, range, angle, cone, consTile, null);
    }

    public static void castCone(float wx, float wy, float range, float angle, float cone, Cons3<Unit, Float, Float> consUnit){
        castCone(wx, wy, range, angle, cone, null, consUnit);
    }

    public static float offsetSin(float offset, float scl){
        return Mathf.absin(Time.time + (offset * Mathf.radDeg), scl, 0.5f) + 0.5f;
    }

    public static float offsetSinB(float offset, float scl){
        return Mathf.absin(Time.time + (offset * Mathf.radDeg), scl, 0.25f);
    }

    /** Iterates over all blocks in a radius. */
    public static void trueEachBlock(float wx, float wy, float range, Cons<Building> cons){
        collidedBlocks.clear();

        int tx = World.toTile(wx);
        int ty = World.toTile(wy);
        int tileRange = Mathf.floorPositive(range / tilesize + 1);

        for(int x = -tileRange + tx, lenX = tileRange + tx; x <= lenX; x++){
            for(int y = -tileRange + ty, lenY = tileRange + ty; y <= lenY; y++){
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

    public static float getBulletDamage(BulletType type){
        return type.damage + type.splashDamage + (Math.max(type.lightningDamage / 2f, 0f) * type.lightning * type.lightningLength);
    }

    /**
     * Targets any units that is not in the array.
     * @returns the unit, picks a random target if all potential targets is in the array.
     */
    public static Posc targetUnique(Team team, float x, float y, float radius, Posc[] targetArray){
        result = null;
        float radiusSquare = radius * radius;
        cdist = radiusSquare + 1;

        Posc[] tmpArray = new Posc[targetArray.length];
        int size = 0;
        for(Posc posc : targetArray){
            if(posc == null) continue;
            tmpArray[size++] = posc;
        }

        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2, radius * 2, unit -> {
            float dst = unit.dst2(x, y);
            if(!Structs.contains(targetArray, unit) && dst < cdist && dst < radiusSquare){
                result = unit;
                cdist = dst;
            }
        });

        if(result == null && size > 0) result = tmpArray[Mathf.random(0, size - 1)];

        return result;
    }

    public static float findLaserLength(float wx, float wy, float wx2, float wy2, Boolf<Tile> pred){
        furthest = null;

        boolean found = world.raycast(World.toTile(wx), World.toTile(wy), World.toTile(wx2), World.toTile(wy2),
        (x, y) -> (furthest = world.tile(x, y)) != null && pred.get(furthest));

        return found && furthest != null ? Math.max(6f, Mathf.dst(wx, wy, furthest.worldx(), furthest.worldy())) : Mathf.dst(wx, wy, wx2, wy2);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, sort, effectHandler);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler){
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, false);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        Boolf<Unit> ucons = unit -> {
            unitCons.get(unit);
            return false;
        };
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons == null ? null : ucons, sort, effectHandler, stopSort);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        collidedBlocks.clear();
        tmpUnitSeq.clear();
        effectArray.clear();
        idx = 0;
        tV.set(x2, y2);
        if(buildingCons != null){
            world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                Building build = world.build(cx, cy);
                if(build != null && (buildingFilter == null || buildingFilter.get(build)) && !collidedBlocks.add(build.pos())){
                    boolean hit;
                    //if(effectHandler != null) effectHandler.get(cx * tilesize, cy * tilesize);
                    if(sort == null){
                        if(effectHandler != null) effectHandler.get(cx * tilesize, cy * tilesize);
                        hit = buildingCons.get(build, true);
                    }else{
                        if(effectHandler != null){
                            effectArray.put(build.id, new Float[]{cx * (float)tilesize, cy * (float)tilesize});
                        }
                        tmpUnitSeq.add(build);
                        hit = buildingCons.get(build, false);
                    }
                    if(hit) tV.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, build.x, build.y)).add(x, y);
                    return hit;
                }
                return false;
            });
        }
        if(unitCons != null){
            rect.setPosition(x, y).setSize(tV.x - x, tV.y - y);

            if(rect.width < 0){
                rect.x += rect.width;
                rect.width *= -1;
            }
            if(rect.height < 0){
                rect.y += rect.height;
                rect.height *= -1;
            }

            rect.grow(unitWidth * 2f);

            Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, unit -> {
                if(unitFilter.get(unit)){
                    unit.hitbox(hitRect);
                    hitRect.grow(unitWidth * 2);

                    Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);

                    if(vec != null){
                        if(sort == null){
                            if(effectHandler != null) effectHandler.get(vec.x, vec.y);
                            unitCons.get(unit);
                        }else{
                            if(effectHandler != null) effectArray.put(unit.id, new Float[]{vec.x, vec.y});
                            tmpUnitSeq.add(unit);
                        }
                    }
                }
            });
        }
        if(sort != null){
            hit = false;
            tmpUnitSeq.sort(sort).each(e -> {
                Float[] eff = effectArray.get(e.id());
                if(e instanceof Building && buildingCons != null && (!stopSort || !hit)){
                    hit = buildingCons.get(((Building)e), true);
                    if(eff != null) effectHandler.get(eff[0], eff[1]);
                }
                if(e instanceof Unit && unitCons != null && (!stopSort || !hit)){
                    hit |= unitCons.get(((Unit)e));
                    if(eff != null) effectHandler.get(eff[0], eff[1]);
                }
            });
        }
        effectArray.clear();
        tmpUnitSeq.clear();
    }

    @Deprecated
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf<Building> buildC, Cons<Unit> unitC, Effect effect){
        collideLineRaw(x, y, x2, y2, b -> b.team != team, u -> u.team != team, buildC, unitC, unit -> unit.dst2(x, y), effect);
    }

    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC){
        collideLineRaw(x, y, x2, y2, buildB, unitB, buildC, unitC, null, null);
    }

    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC, Floatf<Healthc> sort, Effect effect){
        collideLineRaw(x, y, x2, y2, buildB, unitB, buildC, unitC, sort, e -> false, effect);
    }

    @Deprecated
    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC, Floatf<Healthc> sort, Boolf<Building> buildAlt, Effect effect){
        collidedBlocks.clear();
        tmpUnitSeq.clear();
        tV.set(x2, y2);
        if(buildC != null){
            world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                if(tile != null && (buildB == null || buildB.get(tile)) && !collidedBlocks.contains(tile.pos())){
                    boolean s;
                    if(sort == null){
                        s = buildC.get(tile);
                    }else{
                        tmpUnitSeq.add(tile);
                        s = buildAlt.get(tile);
                    }
                    collidedBlocks.add(tile.pos());
                    if(effect != null) effect.at(cx * tilesize, cy * tilesize);
                    if(s){
                        //Mathf.dst();
                        tV.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, tile.x, tile.y));
                        tV.add(x, y);
                        return true;
                    }
                }
                return false;
            });
        }

        if(unitB != null && unitC != null){
            rect.setPosition(x, y).setSize(tV.x - x, tV.y - y);

            if(rect.width < 0){
                rect.x += rect.width;
                rect.width *= -1;
            }
            if(rect.height < 0){
                rect.y += rect.height;
                rect.height *= -1;
            }

            float expand = 2f;

            rect.grow(expand * 2f);

            if(sort == null){
                Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, unit -> {
                    if(unitB.get(unit)){
                        unit.hitbox(hitRect);
                        hitRect.grow(expand * 2);

                        Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);

                        if(vec != null){
                            if(effect != null) effect.at(vec.x, vec.y);
                            unitC.get(unit);
                        }
                    }
                });
            }else{
                Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, unit -> {
                    if(unitB.get(unit)){
                        unit.hitbox(hitRect);
                        hitRect.grow(expand * 2);

                        Vec2 vec = Geometry.raycastRect(x, y, tV.x, tV.y, hitRect);

                        if(vec != null){
                            if(effect != null) effect.at(vec.x, vec.y);
                            tmpUnitSeq.add(unit);
                        }
                    }
                });
                hit = false;
                tmpUnitSeq.sort(sort).each(e -> {
                    if(e instanceof Building && buildC != null && !hit) hit = buildC.get((Building)e);
                    if(e instanceof Unit) unitC.get((Unit)e);
                });
                tmpUnitSeq.clear();
            }
        }
    }

    /** The other version of Damage.collideLine */
    public static void collideLineDamageOnly(Team team, float damage, float x, float y, float angle, float length, Bullet hitter){
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

    public static float linear(float current, float target, float maxTorque, float coefficient){
        current = Math.min(target, current);
        
        return Math.min(coefficient * (target - current) * maxTorque / target, 99999f);
    }

    public static TextureRegion getRegionRect(TextureRegion region, float x, float y, int rw, int rh, int w, int h){
        TextureRegion reg = new TextureRegion(region);
        float tileW = (reg.u2 - reg.u) / w;
        float tileH = (region.v2 - region.v) / h;
        float tileX = x / w;
        float tileY = y / h;

        reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
        reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
        reg.u2 = reg.u + tileW * (rw - 0.02f);
        reg.v2 = reg.v + tileH * (rh - 0.02f);
        reg.width = 32 * rw;
        reg.height = 32 * rh;
        
        return reg;
    }

    /**
     * Gets multiple regions inside a {@link TextureRegion}. The size for each region has to be 32.
     * @param w The amount of regions horizontally.
     * @param h The amount of regions vertically.
     */
    public static TextureRegion[] getRegions(TextureRegion region, int w, int h){
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];
        
        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;

        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % w)) / w;
            float tileY = ((float)(i / w)) / h;
            TextureRegion reg = new TextureRegion(region);

            //start coordinate
            reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
            reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
            //end coordinate
            reg.u2 = reg.u + tileW * 0.96f;
            reg.v2 = reg.v + tileH * 0.96f;
            
            reg.width = reg.height = 32;
            
            regions[i] = reg;
        }
        return regions;
    }

    /**
     * Lerps 2 TextureRegions.
     * @author sk7725
     */
    public static TextureRegion blendSprites(TextureRegion a, TextureRegion b, float f, String name){
        PixmapRegion r1 = Core.atlas.getPixmap(a);
        PixmapRegion r2 = Core.atlas.getPixmap(b);

        Pixmap out = new Pixmap(r1.width, r1.height, r1.pixmap.getFormat());
        out.setBlending(Pixmap.Blending.none);
        Color color1 = new Color();
        Color color2 = new Color();

        for(int x = 0; x < r1.width; x++){
            for(int y = 0; y < r1.height; y++){

                r1.getPixel(x, y, color1);
                r2.getPixel(x, y, color2);
                out.draw(x, y, color1.lerp(color2, f));
            }
        }

        Texture texture  = new Texture(out);
        return Core.atlas.addRegion(name + "-blended-" + (int)(f * 100), new TextureRegion(texture));
    }

    public static Color tempColor(float temp){
        float a;
        if(temp > 273.15f){
            a = Math.max(0f, (temp - 498f) * 0.001f);
            if(a < 0.01f) return Color.clear.cpy();
            Color fcol = Pal.turretHeat.cpy().a(a);
            if(a > 1f){
                fcol.b += 0.01f * a;
                fcol.mul(a);
            }
            return fcol;
        }else{
            a = 1f - Mathf.clamp(temp / 273.15f);
            if(a < 0.01f) return Color.clear.cpy();
            return UnityPal.coldColor.cpy().a(a);
        }
    }

    public static IntSeq unpackInts(IntSeq intpack){
        IntSeq out = new IntSeq();
        for(int i = 0, len = intpack.size * 2; i < len; i++){
            int cint = intpack.get(i / 2);
            int value = (cint >>> (i % 2 == 0 ? 0 : 16)) & 65535;
            int am = (value >> 8) & 255;
            for(int k = 0; k < am; k++) out.add(value & 255);
        }
        return out;
    }

    public static IntSeq unpackIntsFromString(String sintpack){
        IntSeq out = new IntSeq();
        for(int i = 0, len = sintpack.length(); i < len; i += 2){
            int val = sintpack.codePointAt(i + 1);
            int am = sintpack.codePointAt(i);
            for(int k = 0; k < am; k++) out.add(val);
        }
        return out;
    }

    /**
     * Casts forward in a line.
     * @return the first encountered object.
     * There's an issue with the one in 126.2, which I fixed in a pr. This can be removed after the next Mindustry release.
     */
    public static Healthc linecast(Bullet hitter, float x, float y, float angle, float length){
        tV.trns(angle, length);
        
        tmpBuilding = null;

        if(hitter.type.collidesGround){
            world.raycastEachWorld(x, y, x + tV.x, y + tV.y, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                if(tile != null && tile.team != hitter.team){
                    tmpBuilding = tile;
                    return true;
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

        tmpUnit = null;

        Units.nearbyEnemies(hitter.team, rect, e -> {
            if((tmpUnit != null && e.dst2(x, y) > tmpUnit.dst2(x, y)) || !e.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) return;

            e.hitbox(hitRect);
            Rect other = hitRect;
            other.y -= expand;
            other.x -= expand;
            other.width += expand * 2;
            other.height += expand * 2;

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);

            if(vec != null){
                tmpUnit = e;
            }
        });

        if(tmpBuilding != null && tmpUnit != null){
            if(Mathf.dst2(x, y, tmpBuilding.getX(), tmpBuilding.getY()) <= Mathf.dst2(x, y, tmpUnit.getX(), tmpUnit.getY())){
                return tmpBuilding;
            }
        }else if(tmpBuilding != null){
            return tmpBuilding;
        }

        return tmpUnit;
    }
}