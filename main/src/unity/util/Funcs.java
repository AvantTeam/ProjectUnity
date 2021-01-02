package unity.util;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
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
import mindustry.world.Tile;
import unity.graphics.*;

import static mindustry.Vars.*;

public final class Funcs{
    private static final Vec2 tV = new Vec2();
    private static final Seq<Unit> tmpUnitSeq = new Seq<>();
    private static final IntSet collidedBlocks = new IntSet();
    private static final Rect rect = new Rect(), rectAlt = new Rect(), hitRect = new Rect();
    private static Posc result;
    private static float cdist;

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
    public static void trueEachBlock(int wx, int wy, float range, Cons<Building> cons){
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
    public static Posc targetUnique(Team team, float x, float y, float radius, Seq<Posc> targetSeq){
        result = null;
        float radiusSquare = radius * radius;
        cdist = radiusSquare + 1;

        Units.nearbyEnemies(team, x - radius, y - radius, radius * 2, radius * 2, unit -> {
            float dst = unit.dst(x, y);
            if(!targetSeq.contains(unit) && dst < cdist && dst < radiusSquare){
                result = unit;
                cdist = dst;
            }
        });

        if(result == null) result = targetSeq.random();
        return result;
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC){
        collideLineRaw(x, y, x2, y2, buildB, unitB, buildC, unitC, null, null);
    }

    public static void collideLineRaw(float x, float y, float x2, float y2, Boolf<Building> buildB, Boolf<Unit> unitB, Boolf<Building> buildC, Cons<Unit> unitC, Floatf<Unit> sort, Effect effect){
        collidedBlocks.clear();
        tV.set(x2, y2);
        if(buildC != null){
            world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                Building tile = world.build(cx, cy);
                if(tile != null && (buildB == null || buildB.get(tile)) && !collidedBlocks.contains(tile.pos())){
                    boolean s = buildC.get(tile);
                    collidedBlocks.add(tile.pos());
                    if(effect != null) effect.at(cx, cy);
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
                tmpUnitSeq.clear();
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
                tmpUnitSeq.sort(sort).each(unitC);
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

    //graph associated below
    public static float linear(float current, float target, float maxTorque, float coefficient){
        current = Math.min(target, current);
        return Math.min(coefficient * (target - current) * maxTorque / target, 99999f);
    }

    public static TextureRegion getRegionRect(TextureRegion region, float x, float y, int rw, int rh, int w, int h){
        TextureRegion nRegion = new TextureRegion(region);
        float tileW = (nRegion.u2 - nRegion.u) / w;
        float tileH = (region.v2 - region.v) / h;
        float tileX = x / w;
        float tileY = y / h;

        nRegion.u = Mathf.map(tileX, 0f, 1f, nRegion.u, nRegion.u2) + tileW * 0.02f;
        nRegion.v = Mathf.map(tileY, 0f, 1f, nRegion.v, nRegion.v2) + tileH * 0.02f;
        nRegion.u2 = nRegion.u + tileW * (rw - 0.02f);
        nRegion.v2 = nRegion.v + tileH * (rh - 0.02f);
        nRegion.width = 32 * rw;
        nRegion.height = 32 * rh;
        return nRegion;
    }

    public static TextureRegion[] getRegions(TextureRegion region, int sheetW, int sheetH){
        int size = sheetW * sheetH;
        TextureRegion[] ret = new TextureRegion[size];
        float tileW = (region.u2 - region.u) / sheetW;
        float tileH = (region.v2 - region.v) / sheetH;
        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % sheetW)) / sheetW;
            float tileY = ((float)(i / sheetW)) / sheetH;
            TextureRegion nRegion = new TextureRegion(region);

            nRegion.u = Mathf.map(tileX, 0f, 1f, nRegion.u, nRegion.u2) + tileW * 0.02f;
            nRegion.v = Mathf.map(tileY, 0f, 1f, nRegion.v, nRegion.v2) + tileH * 0.02f;
            nRegion.u2 = nRegion.u + tileW * 0.96f;
            nRegion.v2 = nRegion.v + tileH * 0.96f;
            nRegion.width = nRegion.height = 32;
            ret[i] = nRegion;
        }
        return ret;
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
}