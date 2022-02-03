package unity.entities.comp;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.Time.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.BaseTurret.*;
import mindustry.world.blocks.defense.turrets.Turret.*;
import mindustry.world.blocks.power.PowerNode.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.type.*;
import unity.util.*;

@EntityComponent
abstract class WorldComp implements Unitc{
    final static Vec2 vec = new Vec2();
    final static Seq<Building> tmp = new Seq<>();
    final static Seq<Runnable> tmpr = new Seq<>();
    final static IntMap<IntSeq> tmpLinks = new IntMap<>(102);

    @Import float rotation, x, y;
    @Import UnitType type;

    protected transient Seq<DelayRun> runs = new Seq<>();

    transient World unitWorld;
    transient Seq<Building> buildings = new Seq<>(16);
    transient Seq<TurretBuild> turrets = new Seq<>();
    transient FloatSeq positions = new FloatSeq();

    @MethodPriority(100)
    @Override
    public void update(){
        float cx = unitWorld.width() * Vars.tilesize / 2f, cy = unitWorld.height() * Vars.tilesize / 2f;
        float r = rotation - 90f;
        positions.clear();

        TimeReflect.swapRuns(runs);
        World ow = Vars.world;
        Vars.world = unitWorld;

        if(isPlayer()){
            for(TurretBuild t : turrets){
                t.logicControlTime = 5f;
                t.logicShooting = isShooting();
                t.targetPos.set(aimX(), aimY());
            }
        }

        for(int i = 0; i < buildings.size; i++){
            Building b = buildings.get(i);
            positions.add(b.x, b.y);

            if(b instanceof BaseTurretBuild){
                BaseTurretBuild t = (BaseTurretBuild)b;
                t.rotation += r;
            }

            vec.set(b.x - cx, b.y - cy).rotate(r).add(self());

            b.set(vec);
            b.update();
        }

        TimeReflect.updateDelays(runs);

        for(int i = 0; i < buildings.size; i++){
            Building b = buildings.get(i);
            b.x = positions.get(i * 2);
            b.y = positions.get(i * 2 + 1);

            if(b instanceof BaseTurretBuild){
                BaseTurretBuild t = (BaseTurretBuild)b;
                t.rotation -= r;
            }
        }

        TimeReflect.resetRuns();
        Vars.world = ow;
    }

    void setup(){
        UnityUnitType uType = (UnityUnitType)type;
        int w = uType.worldWidth, h = uType.worldHeight;
        unitWorld = new World();
        unitWorld.tiles = new Tiles(w, h);
        for(int i = 0; i < w * h; i++){
            unitWorld.tiles.set(i % w, i / w, new UnitTile(i % w, i / w));
        }
        unitWorld.tiles.eachTile(tile -> tile.setFloor(Blocks.metalFloor.asFloor()));

        tmp.clear();
        tmpr.clear();
        TeamData data = team().data();
        if(data.buildings != null){
            Tmp.r1.setCentered(x, y, w * Vars.tilesize, h * Vars.tilesize);
            data.buildings.intersect(Tmp.r1, tmp);
        }

        tmpLinks.clear();
        for(Building building : tmp){
            if(validPlace(building.tile)){
                int tx = conX(building.tile.x);
                int ty = conY(building.tile.y);

                if(building.power != null && building instanceof PowerNodeBuild && !building.power.links.isEmpty()){
                    IntSeq seq = building.power.links, nseq = new IntSeq();
                    for(int i = 0; i < seq.size; i++){
                        int pos = seq.get(i);
                        int cx = conX(Point2.x(pos)), cy = conY(Point2.y(pos));
                        if(valid(cx, cy)){
                            nseq.add(Point2.pack(cx, cy));
                        }
                    }
                    if(!nseq.isEmpty()){
                        tmpLinks.put(building.id, nseq);
                    }
                }

                tmpr.add(() -> {
                    building.x = cwX(building.x);
                    building.y = cwY(building.y);
                    unitWorld.tile(tx, ty).setBlock(building.block, building.team, building.rotation, () -> building);
                });

                building.tile.remove();
                buildings.add(building);
            }
        }

        World ow = Vars.world;
        Vars.world = unitWorld;

        for(Runnable r : tmpr){
            r.run();
        }
        for(Building b : buildings){
            if(b instanceof PowerNodeBuild){
                IntSeq seq = tmpLinks.get(b.id);
                if(seq != null && b.power != null){
                    b.power.links.clear();
                    for(int i = 0; i < seq.size; i++){
                        int pos = seq.get(i);
                        b.configureAny(pos);
                    }
                }
            }
            b.updateProximity();
            if(b instanceof TurretBuild){
                TurretBuild tb = (TurretBuild)b;
                turrets.add(tb);
            }
        }

        Vars.world = ow;
        tmpr.clear();
        tmp.clear();
        tmpLinks.clear();
    }

    float cwX(float x){
        return (x - this.x) + (unitWorld.width() * Vars.tilesize / 2f);
    }

    float cwY(float y){
        return (y - this.y) + (unitWorld.height() * Vars.tilesize / 2f);
    }

    int conX(int x){
        return (x - (int)(this.x / Vars.tilesize)) + (unitWorld.width() / 2) - 1;
    }

    int conY(int y){
        return (y - (int)(this.y / Vars.tilesize)) + (unitWorld.height() / 2) - 1;
    }

    boolean validPlace(Tile tile){
        Block block = tile.block();
        int offset = -(block.size - 1) / 2;
        int tx = conX(tile.x);
        int ty = conY(tile.y);

        boolean valid = tx >= 0 && tx < unitWorld.width() && ty >= 0 && ty < unitWorld.height();
        if(tile.block().isMultiblock()){
            for(int dx = 0; dx < block.size; dx++){
                for(int dy = 0; dy < block.size; dy++){
                    int wx = dx + offset + tx, wy = dy + offset + ty;
                    valid &= wx >= 0 && wx < unitWorld.width() && wy >= 0 && wy < unitWorld.height();
                }
            }
        }
        return valid;
    }

    boolean valid(int x, int y){
        return x >= 0 && x < unitWorld.width() && y >= 0 && y < unitWorld.height();
    }
}
