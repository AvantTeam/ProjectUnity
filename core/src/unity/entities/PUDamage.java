package unity.entities;

import arc.func.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.Pool.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import unity.util.*;

public class PUDamage{
    public final static CollideLineData lineData = new CollideLineData();
    private final static BasicPool<Hit> hitPool = new BasicPool<>(Hit::new);
    private static final Seq<Hit> hitEffects = new Seq<>();
    private static final Rect tr = new Rect(), hr = new Rect();

    public static void collideLine(Team team, float x, float y, float x2, float y2,
                                   CollideLineData data, CollideLineHandler handler){
        hitEffects.clear();

        tr.setPosition(x, y).setSize(x2 - x, y2 - y).normalize().grow(Math.max(data.buildingWidth, data.unitWidth) * 2f);

        for(TeamData td : Vars.state.teams.present){
            if(td.team != team){
                if(td.buildingTree != null && data.hitBuilding){
                    td.buildingTree.intersect(tr.x, tr.y, tr.width, tr.height, b -> {
                        if(!(data.buildingFilter == null || data.buildingFilter.get(b))) return;
                        b.hitbox(hr);
                        hr.grow(data.buildingWidth * 2f);

                        Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hr);

                        if(vec != null){
                            float scl = (b.hitSize() - data.buildingWidth) / b.hitSize();
                            vec.sub(b).scl(scl).add(b);
                            if(data.sort == null){
                                handler.get(vec.x, vec.y, b);
                            }else{
                                Hit he = hitPool.obtain();
                                he.ent = b;
                                he.x = vec.x;
                                he.y = vec.y;
                                hitEffects.add(he);
                            }
                        }
                    });
                }
                if(td.unitTree != null && data.hitUnit){
                    td.unitTree.intersect(tr.x, tr.y, tr.width, tr.height, u -> {
                        if(!(data.unitFilter == null || data.unitFilter.get(u))) return;
                        u.hitbox(hr);
                        hr.grow(data.unitWidth * 2f);
                        float width = (u.hitSize / 2f) + data.unitWidth;
                        float w = u.hitSize() / 2f;

                        Vec2 vec;
                        if((!data.circularUnitCollision || Intersector.intersectSegmentCircle(Tmp.v1.set(x, y), Tmp.v2.set(x2, y2), Tmp.v3.set(u.x, u.y), width * width)) &&
                                (vec = Geometry.raycastRect(x, y, x2, y2, hr)) != null){
                            float scl = (w - data.unitWidth) / w;
                            vec.sub(u).scl(scl).add(u);
                            if(data.sort == null){
                                handler.get(vec.x, vec.y, u);
                            }else{
                                Hit he = hitPool.obtain();
                                he.ent = u;
                                he.x = vec.x;
                                he.y = vec.y;
                                hitEffects.add(he);
                            }
                        }
                    });
                }
            }
        }
        if(data.sort != null){
            boolean hit = false;
            hitEffects.sort(h -> data.sort.get(h.ent));
            for(Hit h : hitEffects){
                if(!hit || !data.breakSort){
                    hit = handler.get(h.x, h.y, h.ent);
                }
                hitPool.free(h);
            }
        }
        hitEffects.clear();
    }

    public interface CollideLineHandler{
        boolean get(float x, float y, Healthc ent);
    }

    static class Hit implements Poolable{
        Healthc ent;
        float x, y;

        @Override
        public void reset(){
            ent = null;
            x = y = 0f;
        }
    }

    public static class CollideLineData{
        public boolean hitBuilding, hitUnit, breakSort, circularUnitCollision;
        public float unitWidth, buildingWidth;
        public Boolf<Unit> unitFilter;
        public Boolf<Building> buildingFilter;
        public Floatf<Healthc> sort;

        public CollideLineData clear(){
            hitBuilding = hitUnit = true;
            circularUnitCollision = false;
            unitFilter = null;
            buildingFilter = null;
            sort = null;
            unitWidth = buildingWidth = 3f;
            return this;
        }
    }
}
