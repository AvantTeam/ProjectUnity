package unity.map.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.ai.BaseRegistry.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.world.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static unity.content.UnityBlocks.*;

public class ElectrodePlanetGenerator extends PlanetGenerator{
    BaseGenerator basegen = new BaseGenerator();
    float scl = 6f;
    float waterOffset = 0.07f;

    Block[][] arr = {
        {metalFloor2, electroTile, darkMetal, deepwater, electroTile, deepwater, electroTile, deepwater, deepwater, electroTile, ice, ice, ice},
        {metalFloor2, ice, deepwater, deepwater, electroTile, darkMetal, deepwater, electroTile, deepwater, darkMetal, electroTile, ice, ice},
        {water, metalFloor2, darksand, darkMetal, deepwater, deepwater, darkMetal, deepwater, deepwater, deepwater, electroTile, electroTile, ice},
        {water, metalFloor2, metalFloor2, darkMetal, darksand, darksand, darkMetal, deepwater, darkMetal, deepwater, darkMetal, deepwater, electroTile},

        {deepwater, water, metalFloor2, darksand, darksand, darkMetal, darkMetal, darkMetal, darkMetal, basalt, deepwater, deepwater, electroTile},
        {deepwater, water, metalFloor2, darkMetal, darksand, darkMetal, darkMetal, darkMetal, basalt, basalt, darkMetal, deepwater, deepwater},
        {deepwater, water, metalFloor2, darksand, darkMetal, darkMetal, basalt, darkMetal, darkMetal, basalt, darkMetal, basalt, deepwater},
        {deepwater, water, metalFloor2, darksand, darksand, darkMetal, basalt, darkMetal, basalt, basalt, deepwater, deepwater, electroTile},

        {water, metalFloor2, metalFloor2, darksand, darksand, darkMetal, darkMetal, deepwater, electroTile, deepwater, deepwater, deepwater, electroTile},
        {water, metalFloor2, darksand, darkMetal, deepwater, deepwater, electroTile, deepwater, deepwater, deepwater, electroTile, darkMetal, ice},
        {metalFloor2, ice, deepwater, deepwater, electroTile, deepwater, darkMetal, electroTile, deepwater, darkMetal, electroTile, ice, ice},
        {metalFloor2, electroTile, deepwater, deepwater, darkMetal, deepwater, electroTile, deepwater, deepwater, electroTile, ice, ice, ice}
    };

    float waterf = 2f / arr[0].length;

    float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow((float)Simplex.noise3d(0, 5, 0.5f, 1f / 3f, position.x, position.y, position.z), 3f) + Math.abs(Mathf.sin(position.x) + Mathf.cos(position.y)) / 5 + waterOffset) / (1f + waterOffset);
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        return Math.max(height, waterf);
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);

        if(block == null) return Color.white.cpy();
        return Tmp.c1.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(1, position.x, position.y, position.z, 2, 22) > 0.32){
            tile.block = Blocks.air;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);

        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);

        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = (float)Simplex.noise3d(0, 7, 0.56, 1f / 3f, position.x, position.y + 999f, position.z);

        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        return arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return (float)Simplex.noise3d(0, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    protected void generate(){

        class Room{
            int x, y, radius;
            ObjectSet<Room> connected = new ObjectSet<>();

            Room(int x, int y, int radius){
                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void connect(Room to){
                if(connected.contains(to)) return;

                connected.add(to);
                float nscl = rand.random(20f, 60f);
                int stroke = rand.random(4, 12);
                brush(pathfind(x, y, to.x, to.y, tile -> (tile.solid() ? 5f : 0f) + noise(tile.x, tile.y, 1, 1, 1f / nscl) * 60, Astar.manhattan), stroke);
            }
        }

        cells(4);
        distort(10f, 12f);

        float constraint = 1.3f;
        float radius = width / 2f / Mathf.sqrt3;
        int rooms = rand.random(2, 5);
        Seq<Room> roomseq = new Seq<>();

        for(int i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(radius / constraint));
            float rx = (width / 2f + Tmp.v1.x);
            float ry = (height / 2f + Tmp.v1.y);
            float maxrad = radius - Tmp.v1.len();
            float rrad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            roomseq.add(new Room((int)rx, (int)ry, (int)rrad));
        }

        //check positions on the map to place the player spawn. this needs to be in the corner of the map
        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.chance(0.3) ? 2 : 1;
        int offset = rand.nextInt(360);
        float length = width / 2.55f - rand.random(13, 23);
        int angleStep = 5;
        int waterCheckRad = 5;
        for(int i = 0; i < 360; i += angleStep){
            int angle = offset + i;
            int cx = (int)(width / 2 + Angles.trnsx(angle, length));
            int cy = (int)(height / 2 + Angles.trnsy(angle, length));

            int waterTiles = 0;

            //check for water presence
            for(int rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(int ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    Tile tile = tiles.get(cx + rx, cy + ry);
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    }
                }
            }

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                spawn = new Room(cx, cy, rand.random(8, 15));
                roomseq.add(spawn);

                for(int j = 0; j < enemySpawns; j++){
                    float enemyOffset = rand.range(60f);
                    Tmp.v1.set(cx - width / 2f, cy - height / 2f).rotate(180f + enemyOffset).add(width / 2f, height / 2f);
                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(8, 15));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        for(Room room : roomseq){
            erase(room.x, room.y, room.radius);
        }

        int connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(int i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        }

        for(Room room : roomseq){
            spawn.connect(room);
        }

        cells(1);
        distort(10f, 6f);

        inverseFloodFill(tiles.getn(spawn.x, spawn.y));

        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float scl = 1f;
        float addscl = 1.3f;

        if(Simplex.noise3d(0, 2, 0.5, scl, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.25f * addscl){
            ores.add(Blocks.oreCoal);
        }

        if(Simplex.noise3d(0, 2, 0.5, scl, sector.tile.v.x + 1, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.5f * addscl){
            ores.add(Blocks.oreTitanium);
        }

        if(Simplex.noise3d(0, 2, 0.5, scl, sector.tile.v.x + 2, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.7f * addscl){
            ores.add(Blocks.oreThorium);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.09f, 0.01f) - i * 0.01f);
        }

        pass((x, y) -> {
            if(!floor.asFloor().hasSurface()) return;

            int offsetX = x - 4, offsetY = y + 23;
            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);
                if(Math.abs(0.5f - noise(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22f + i * 0.01 &&
                    Math.abs(0.5f - noise(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37f + freq){
                    ore = entry;
                    break;
                }
            }
        });

        trimDark();

        median(2);

        tech();

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        int ruinCount = rand.random(-2, 4);
        if(ruinCount > 0){
            int padding = 25;

            //create list of potential positions
            for(int x = padding; x < width - padding; x++){
                for(int y = padding; y < height - padding; y++){
                    Tile tile = tiles.getn(x, y);
                    if(!tile.solid() && (tile.drop() != null || tile.floor().liquidDrop != null)){
                        ints.add(tile.pos());
                    }
                }
            }

            ints.shuffle(rand);

            int placed = 0;
            float diffRange = 0.4f;
            //try each position
            for(int i = 0; i < ints.size && placed < ruinCount; i++){
                int val = ints.items[i];
                int x = Point2.x(val), y = Point2.y(val);

                //do not overwrite player spawn
                if(Mathf.within(x, y, spawn.x, spawn.y, 18f)){
                    continue;
                }

                float range = difficulty + rand.random(diffRange);

                Tile tile = tiles.getn(x, y);
                BasePart part = null;

                if(tile.overlay().itemDrop != null){
                    part = bases.forResource(tile.drop()).getFrac(range);
                }else if(tile.floor().liquidDrop != null && rand.chance(0.05)){
                    part = bases.forResource(tile.floor().liquidDrop).getFrac(range);
                }else if(rand.chance(0.05)){ //ore-less parts are less likely to occur.
                    part = bases.parts.getFrac(range);
                }

                //actually place the part
                if(part != null && BaseGenerator.tryPlace(part, x, y, Team.derelict, (cx, cy) -> {
                    Tile other = tiles.getn(cx, cy);
                    other.setOverlay(Blocks.oreScrap);
                    for(int j = 1; j <= 2; j++){
                        for(Point2 p : Geometry.d8){
                            Tile t = tiles.get(cx + p.x * j, cy + p.y * j);
                            if(t != null && t.floor().hasSurface() && rand.chance(j == 1 ? 0.4 : 0.2)){
                                t.setOverlay(Blocks.oreScrap);
                            }
                        }
                    }
                })){
                    placed++;

                    int debrisRadius = Math.max(part.schematic.width, part.schematic.height) / 2 + 3;
                    Geometry.circle(x, y, tiles.width, tiles.height, debrisRadius, (cx, cy) -> {
                        float dst = Mathf.dst(cx, cy, x, y);
                        float removeChance = Mathf.lerp(0.05f, 0.5f, dst / debrisRadius);

                        Tile other = tiles.getn(cx, cy);
                        if(other.build != null && other.isCenter()){
                            if(other.team() == Team.derelict && rand.chance(removeChance)){
                                other.remove();
                            }else if(rand.chance(0.5)){
                                other.build.health = other.build.health - rand.random(other.build.health * 0.9f);
                            }
                        }
                    });
                }
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        for(Room espawn : enemies){
            tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn);
        }

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = true;
        }else{
            state.rules.winWave = 15 * (int)Math.max(difficulty * 10, 1);
        }

        state.rules.waves = true;

        state.rules.spawns = Waves.generate(difficulty);
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }
}
