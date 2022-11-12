package unity.mod;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.ConstructBlock.*;
import unity.entities.prop.*;
import unity.mod.EndBuilderGraph.*;
import unity.util.*;
import unity.world.*;

import static mindustry.Vars.*;

public class EndBuilders{
    final static BasicPool<EndBuilder> builderPool = new BasicPool<>(EndBuilderGraph.EndBuilder::new);
    //final static Seq<EndBuilder> tmp = new Seq<>();
    static EndBuilder n, n2;
    static float dst, dstb;
    static boolean valid, inside;

    static long lastFrame = -1;

    public static EndBuilders builders;
    public static TextureRegion laserEnd, laser;

    EndBuilderData[] teams = new EndBuilderData[Team.all.length];

    public EndBuilders(){
        Events.on(EventType.ContentInitEvent.class, e -> {
            if(!headless){
                laserEnd = Core.atlas.find("unity-end-builder-laser-end");
                laser = Core.atlas.find("unity-end-builder-laser");
            }
        });
        Events.on(EventType.BlockBuildBeginEvent.class, e -> {
            if(e.tile.build instanceof ConstructBuild b){
                //Log.info("end builder BlockBuildBeginEvent " + e.team);
                EndBuilderData d = data(e.team.id);
                d.addBuildPlan(b, e.tile.x, e.tile.y, b.rotation, e.breaking, b.lastConfig);
            }
        });
        Events.on(EventType.BuildTeamChangeEvent.class, e -> {
            EndBuilderData prev = data(e.previous.id), next = data(e.build.team.id);
            prev.removeBuilding(e.build);
            next.addBuilding(e.build);
        });
        Events.on(EventType.BuildSelectEvent.class, e -> {
            if(e.tile.build instanceof ConstructBuild b){
                if(e.builder instanceof BlockUnitc bu && bu.tile() instanceof EndBuilderBuilding) return;
                EndBuilderData d = data(e.team.id);
                d.validatePlan(b, e.tile.x, e.tile.y, e.breaking);
                //Log.info("end builder BuildSelectEvent");
            }
        });
        Events.on(EventType.ResetEvent.class, e -> {
            //if(Core.graphics.getFrameId() != lastFrame) return;
            for(EndBuilderData t : teams){
                if(t == null) continue;
                t.tree.clear();
                t.buildings.clear();
                t.lastPlan = null;
                t.queue.clear();
                t.queueSet.clear();
                //Log.info("endbuilder reset");
            }
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            for(TeamData data : state.teams.active){
                this.data(data.team.id);
            }
            for(EndBuilderData t : teams){
                if(t == null) continue;
                //Log.info("endbuilder data world load");
                t.tree.clear();
                t.buildings.clear();
                t.resize();
                //Log.info("endbuilder world load");
            }
            for(EndBuilderData t : teams){
                if(t == null) continue;

                //Log.info("endbuilder data world load post");
                t.queue.removeAll(b -> {
                    //t.addBuilding(b);
                    data(b.team.id).addBuilding(b);
                    return true;
                });
                t.queueSet.clear();
                //Log.info("endbuilder world load");
            }
        });
        Events.run(Trigger.update, this::update);
        RestrictedProps.limit = this::limit;
        RestrictedProps.planValid = (bp, team) -> data(team.id).planValid(bp);
    }

    public static void load(){
        builders = new EndBuilders();
    }

    public static void drawLaser(Position a, float aDst, Position b, float bDst, float scl){
        float rot = a.angleTo(b);
        float vx = Mathf.cosDeg(rot), vy = Mathf.sinDeg(rot);
        Vec2 v1 = Tmp.v1.set(vx, vy).scl(aDst).add(a);
        Vec2 v2 = Tmp.v2.set(vx, vy).scl(-bDst).add(b);
        Draw.rect(laserEnd, v1.x, v1.y,
                laserEnd.width * scl * Draw.scl, laserEnd.height * scl * Draw.scl, rot);
        Draw.rect(laserEnd, v2.x, v2.y,
                laserEnd.width * scl * Draw.scl, laserEnd.height * scl * Draw.scl, rot + 180f);
        v1.set(vx, vy).scl(aDst + (laserEnd.width * scl * Draw.scl / 2f)).add(a);
        v2.set(vx, vy).scl(-bDst - (laserEnd.width * scl * Draw.scl / 2f)).add(b);
        Lines.stroke(laser.height * Draw.scl * scl);
        Lines.line(laser, v1.x, v1.y, v2.x, v2.y, false);
    }

    public static float getEfficiency(int range, int blockRange){
        float br = (float)blockRange / 1.5f;
        float v = Mathf.clamp(range / br);
        return v * v;
    }

    public static void drawPlace(Block b, int x, int y, int tileRange, float range){
        int eff = tileRange + b.size / 2;
        for(int i = 0; i < 4; i++){
            int maxLen = tileRange + b.size / 2;
            var dir = Geometry.d4[i];
            int dx = dir.x, dy = dir.y;
            int offset = b.size / 2;
            Building dest = null;

            for(int j = 1 + offset; j <= tileRange + offset; j++){
                Building other = world.build(x + j * dir.x, y + j * dir.y);
                if(other != null && other.team == player.team() && other instanceof EndBuilderBuilding && (other.tileX() == x || other.tileY() == y)){
                    maxLen = j;
                    dest = other;
                    break;
                }
            }
            eff = Math.min(eff, maxLen);

            Drawf.dashLine(Pal.placing,
                    x * tilesize + dx * (tilesize * b.size / 2f + 2),
                    y * tilesize + dy * (tilesize * b.size / 2f + 2),
                    x * tilesize + dx * (maxLen) * tilesize,
                    y * tilesize + dy * (maxLen) * tilesize
            );
            if(dest != null){
                Drawf.square(dest.x, dest.y, dest.block.size * tilesize/2f + 2.5f, 0f);
            }
            Drawf.circles(x * tilesize, y * tilesize, range, Pal.placing);
        }
        float efff = 1f - getEfficiency(eff, tileRange);
        if(efff > 0){
            b.drawPlaceText("%-" + (efff * 100), x, y, false);
        }
    }

    void update(){
        if(Core.graphics.getFrameId() != lastFrame) return;
        lastFrame = Core.graphics.getFrameId();
        for(TeamData t : state.teams.active){
            EndBuilderData d = data(t.team.id);
            if(!t.plans.isEmpty() && d.lastPlan != t.plans.last()){
                Queue<BlockPlan> pq = t.plans;
                for(int i = pq.size - 1; i >= 0; i--){
                    BlockPlan p = pq.get(i);
                    if(p != null && d.addBlockPlan(p)){
                        pq.removeIndex(i);
                        i++;
                    }
                    if(p == d.lastPlan){
                        break;
                    }
                }
            }
        }
    }

    EndBuilderData data(int id){
        if(teams[id] == null) teams[id] = new EndBuilderData();
        return teams[id];
    }

    AdvanceQuadTree<EndBuilder> tree(int id){
        return data(id).tree;
    }

    public boolean limit(Unit unit){
        AdvanceQuadTree<EndBuilder> tree = tree(unit.team.id);
        boolean valid = false;
        Rect r = Tmp.r1;
        unit.hitbox(r);
        n = null;
        n2 = null;
        dst = 0f;
        dstb = 0f;
        inside = false;
        tree.intersect(r, e -> {
            if(!((EndBuilderBuilding)e.b).builderValid()) return;
            float x = e.b.x, y = e.b.y;
            float dst2 = Mathf.dst(x, y, unit.lastX, unit.lastY);
            float dst3 = Mathf.dst(x, y, unit.x, unit.y);
            if(dst3 <= e.range) inside = true;
            if(dst2 <= e.range){
                if((n == null || dst2 < dst)){
                    n = e;
                    dst = dst2;
                }
                if(n2 == null || Math.abs(dst2 - e.range) > dstb){
                    n2 = e;
                    dstb = Math.abs(dst2 - e.range);
                }
            }
        });
        if(n != null){
            if(!inside){
                float x = n2.b.x, y = n2.b.y;
                Tmp.v1.set(unit.x, unit.y).sub(x, y).limit(n2.range - 0.0002f).add(x, y).sub(unit.x, unit.y);
                unit.x += Tmp.v1.x;
                unit.y += Tmp.v1.y;
                Tmp.v1.scl(0.1f / Time.delta);
                unit.vel.add(Tmp.v1);
            }
            ((EndBuilderBuilding)n.b).updateUnit(unit);
            valid = true;
        }
        return valid;
    }

    static class EndBuilderData{
        AdvanceQuadTree<EndBuilder> tree = new AdvanceQuadTree<>(new Rect(0, 0, world.unitWidth(), world.unitHeight()));
        IntMap<EndBuilder> buildings = new IntMap<>(102);

        Seq<Building> queue = new Seq<>();
        IntSet queueSet = new IntSet();

        BlockPlan lastPlan = null;

        public void validatePlan(ConstructBuild cb, int x, int y, boolean breaking){
            Rect r = Tmp.r1;
            Block b = cb.block;
            cb.hitbox(r);
            tree.intersect(r, e -> {
                if(e.within(cb.x, cb.y, b.size * 4) && ((EndBuilderBuilding)e.b).builderValid()){
                    BuildPlan p = ((EndBuilderBuilding)e.b).getPlan(x, y);
                    if(p != null){
                        p.breaking = breaking;
                        //Log.info("end builder validatePlan");
                    }
                }
            });
        }

        public boolean planValid(BuildPlan bp){
            Block b = bp.block;
            float ts = tilesize;
            float x = bp.x * ts + b.offset, y = bp.y * ts + b.offset;
            Rect r = Tmp.r1.setCentered(x, y, b.size * ts);
            valid = false;
            tree.intersect(r, e -> {
                if(!valid && e.within(x, y, b.size * 4) && ((EndBuilderBuilding)e.b).builderValid()){
                    valid = true;
                }
            });
            return valid;
        }

        public boolean addBlockPlan(BlockPlan bp){
            Block b = content.block(bp.block);
            float ts = tilesize;
            float x = bp.x * ts + b.offset, y = bp.y * ts + b.offset;
            Rect r = Tmp.r1.setCentered(x, y, b.size * ts);
            valid = false;
            tree.intersect(r, e -> {
                if(e.within(x, y, b.size * 4) && ((EndBuilderBuilding)e.b).builderValid()){
                    if(((EndBuilderBuilding)e.b).validPlan(bp.x, bp.y, bp.rotation, bp.removed, bp.config)){
                        BuildPlan plan = new BuildPlan(bp.x, bp.y, bp.rotation, b, bp.config);
                        ((EndBuilderBuilding)e.b).addPlan(plan);
                    }
                    valid = true;
                }
            });
            return valid;
        }

        public void addBuildPlan(ConstructBuild cb, int x, int y, int rotation, boolean breaking, Object config){
            Rect r = Tmp.r1;
            Block b = cb.block;
            cb.hitbox(r);
            tree.intersect(r, e -> {
                if(e.within(cb.x, cb.y, b.size * 4) && ((EndBuilderBuilding)e.b).builderValid()){
                    //Log.info("end builder addBuildPlan test");
                    if(((EndBuilderBuilding)e.b).validPlan(x, y, rotation, breaking, config)){
                        BuildPlan plan = new BuildPlan(x, y, rotation, cb.current, config);
                        ((EndBuilderBuilding)e.b).addPlan(plan);
                        //Log.info("end builder addBuildPlan");
                    }
                }
            });
        }

        void resize(){
            Rect r = new Rect();
            world.getQuadBounds(r);
            tree = new AdvanceQuadTree<>(r);
            //Log.info("test-endbuilders:" + tree.bounds);
        }

        public void addBuilding(Building b){
            if(world.isGenerating() && queueSet.add(b.pos())){
                queue.add(b);
                return;
            }
            if(!buildings.containsKey(b.pos())){
                EndBuilder e = builderPool.obtain();
                e.b = b;
                e.range = ((EndBuilderBuilding)b).range();
                tree.insert(e);
                buildings.put(b.pos(), e);
            }
        }

        public void removeBuilding(Building b){
            EndBuilder e = buildings.remove(b.pos());
            if(e != null && e.tree != null){
                e.tree.remove(e);
                builderPool.free(e);
            }
        }
    }
}
