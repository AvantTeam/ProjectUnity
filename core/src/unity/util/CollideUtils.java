package unity.util;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;
import mindustry.game.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public final class CollideUtils{
    private static final IntSet collidedBlocks = new IntSet();
    private static final Rect rect = new Rect(), hitRect = new Rect();
    private static final BoolGrid collideLineCollided = new BoolGrid();
    private static final IntSeq lineCast = new IntSeq(), lineCastNext = new IntSeq();
    private static final Seq<Hit> hitEffects = new Seq();
    private static final Vec2 v1 = new Vec2(), v2 = new Vec2();
    private static boolean hit, hitB;

    static class Hit implements Poolable{
        Healthc ent;
        float x, y;

        @Override
        public void reset(){
            ent = null;
            x = y = 0f;
        }
    }

    public interface HitHandler{
        boolean get(float x, float y, Healthc ent, boolean direct);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, boolean hitTiles, boolean hitUnits, boolean stopSort, HitHandler handler){
        collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, hitTiles, hitUnits, h -> h.dst2(x, y), handler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, sort, effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, unitWidth, tileWidth, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler){
        collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, sort, effectHandler);
    }

    public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf<Healthc> pred, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team && pred.get(b), u -> u.team != team && pred.get(u), buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler){
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, false);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons == null ? null : unit -> {
            unitCons.get(unit);
            return false;
        }, sort, effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, unitWidth, 0f, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, stopSort);
    }

    /** @author EyeOfDarkness */
    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, Boolf2<Building, Boolean> buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort){
        collideLineRaw(x, y, x2, y2, unitWidth, tileWidth,
        buildingFilter, unitFilter, buildingCons != null, unitCons != null,
        sort, (ex, ey, ent, direct) -> {
            boolean hit = false;
            if(unitCons != null && direct && ent instanceof Unit){
                hit = unitCons.get((Unit)ent);
            }else if(buildingCons != null && ent instanceof Building){
                hit = buildingCons.get((Building)ent, direct);
            }

            if(effectHandler != null && direct) effectHandler.get(ex, ey);
            return hit;
        }, stopSort
        );
    }

    /** @author EyeOfDarkness */
    public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, boolean hitTile, boolean hitUnit, Floatf<Healthc> sort, HitHandler hitHandler, boolean stopSort){
        hitEffects.clear();
        lineCast.clear();
        lineCastNext.clear();
        collidedBlocks.clear();

        v1.set(x2, y2);
        if(hitTile){
            collideLineCollided.clear();
            Runnable cast = () -> {
                hitB = false;
                lineCast.each(i -> {
                    int tx = Point2.x(i), ty = Point2.y(i);
                    Building build = world.build(tx, ty);

                    boolean hit = false;
                    if(build != null && (buildingFilter == null || buildingFilter.get(build)) && collidedBlocks.add(build.pos())){
                        if(sort == null){
                            hit = hitHandler.get(tx * tilesize, ty * tilesize, build, true);
                        }else{
                            hit = hitHandler.get(tx * tilesize, ty * tilesize, build, false);
                            Hit he = Pools.obtain(Hit.class, Hit::new);
                            he.ent = build;
                            he.x = tx * tilesize;
                            he.y = ty * tilesize;

                            hitEffects.add(he);
                        }

                        if(hit && !hitB){
                            v1.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, build.x, build.y)).add(x, y);
                            hitB = true;
                        }
                    }

                    Vec2 segment = Intersector.nearestSegmentPoint(x, y, v1.x, v1.y, tx * tilesize, ty * tilesize, v2);
                    if(!hit && tileWidth > 0f){
                        for(Point2 p : Geometry.d8){
                            int newX = (p.x + tx);
                            int newY = (p.y + ty);
                            boolean within = !hitB || Mathf.within(x / tilesize, y / tilesize, newX, newY, v1.dst(x, y) / tilesize);
                            if(segment.within(newX * tilesize, newY * tilesize, tileWidth) && collideLineCollided.within(newX, newY) && !collideLineCollided.get(newX, newY) && within){
                                lineCastNext.add(Point2.pack(newX, newY));
                                collideLineCollided.set(newX, newY, true);
                            }
                        }
                    }
                });

                lineCast.clear();
                lineCast.addAll(lineCastNext);
                lineCastNext.clear();
            };

            world.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
                if(collideLineCollided.within(cx, cy) && !collideLineCollided.get(cx, cy)){
                    lineCast.add(Point2.pack(cx, cy));
                    collideLineCollided.set(cx, cy, true);
                }

                cast.run();
                return hitB;
            });

            while(!lineCast.isEmpty()) cast.run();
        }

        if(hitUnit){
            rect.setPosition(x, y).setSize(v1.x - x, v1.y - y);

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
                if(unitFilter == null || unitFilter.get(unit)){
                    unit.hitbox(hitRect);
                    hitRect.grow(unitWidth * 2);

                    Vec2 vec = Geometry.raycastRect(x, y, v1.x, v1.y, hitRect);

                    if(vec != null){
                        float scl = (unit.hitSize - unitWidth) / unit.hitSize;
                        vec.sub(unit).scl(scl).add(unit);
                        if(sort == null){
                            hitHandler.get(vec.x, vec.y, unit, true);
                        }else{
                            Hit he = Pools.obtain(Hit.class, Hit::new);
                            he.ent = unit;
                            he.x = vec.x;
                            he.y = vec.y;
                            hitEffects.add(he);
                        }
                    }
                }
            });
        }

        if(sort != null){
            hit = false;
            hitEffects.sort(he -> sort.get(he.ent)).each(he -> {
                if(!stopSort || !hit){
                    hit = hitHandler.get(he.x, he.y, he.ent, true);
                }

                Pools.free(he);
            });
        }

        hitEffects.clear();
    }
}
