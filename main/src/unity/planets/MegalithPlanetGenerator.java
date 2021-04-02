package unity.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.struct.ObjectMap.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.content.*;
import unity.graphics.*;
import unity.mod.*;
import unity.type.sector.*;
import unity.world.blocks.LoreMessageBlock.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static unity.content.UnityBlocks.*;

public class MegalithPlanetGenerator extends PlanetGenerator{
    protected RidgedPerlin rid = new RidgedPerlin(1, 2);
    protected BaseGenerator basegen = new BaseGenerator();
    protected float scl = 7f;
    protected float waterOffset = 0.1f;

    protected Block[][] blocks = {
        {deepwater, water, darksandWater, darksandWater, darksand, darksandWater, stone, stone, darksandWater, snow, darksandWater, darksandWater, iceSnow, ruinousRock},
        {deepwater, water, darksandWater, darksand, darksand, stone, stone, stone, ruinousRock, dacite, stone, snow, snow, ice},
        {deepwater, water, darksandWater, darksand, stone, dacite, darksandWater, dacite, darksandWater, stone, ruinousRock, dacite, ruinousRock, ice},
        {deepwater, water, darksandWater, darksand, darksand, stone, ruinousRock, stone, dacite, dacite, snow, iceSnow, iceSnow, ice},

        {deepwater, water, darksandWater, darksand, darksandWater, stone, stone, dacite, snow, dacite, snow, snow, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, snow, ruinousRock, stone, darksandWater, ruinousRock, iceSnow, ice, ice},
        {water, darksandWater, darksand, stone, darksand, ruinousRock, darksandWater, ruinousRock, dacite, iceSnow, iceSnow, ice, ice, ice},
        {water, darksandWater, dacite, ruinousRock, dacite, snow, ruinousRock, snow, dacite, iceSnow, ice, ice, ice, ice},

        {deepwater, water, darksandWater, darksand, ruinousRock, darksand, stone, stone, snow, iceSnow, iceSnow, iceSnow, ice, ice},
        {water, water, darksandWater, darksand, darksandWater, stone, snow, snow, dacite, snow, snow, iceSnow, ice, ice},
        {water, darksandWater, darksand, stone, ruinousRock, dacite, ruinousRock, stone, snow, snow, iceSnow, ice, ice, ice},
        {water, darksandWater, ruinousRock, snow, dacite, stone, snow, dacite, iceSnow, snow, ice, ice, ice, ice}
    };

    ObjectMap<Block, Entry<Block, float[]>> dec = ObjectMap.of(
        ruinousRock, new Entry<Block, float[]>(){{ key = archEnergy; value = new float[]{0.005f, 0.02f}; }}
    );

    protected float waterf = 4f / blocks[0].length;

    protected float rawHeight(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);
        return (Mathf.pow((float)noise.octaveNoise3D(7, 0.5f, 1f / 3f, position.x, position.y, position.z), 2.3f) + waterOffset) / (1f + waterOffset);
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

        if(rid.getValue(position.x, position.y, position.z, 22) > 0.32){
            tile.block = Blocks.air;
        }
    }

    Block getBlock(Vec3 position){
        float height = rawHeight(position);

        Tmp.v31.set(position);
        position = Tmp.v33.set(position).scl(scl);

        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / (rad));
        float tnoise = (float)noise.octaveNoise3D(7, 0.56, 1f / 3f, position.x, position.y + 999f, position.z);

        temp = Mathf.lerp(temp, tnoise, 0.5f);
        height *= 1.2f;
        height = Mathf.clamp(height);

        return blocks[Mathf.clamp((int)(temp * blocks.length), 0, blocks[0].length - 1)][Mathf.clamp((int)(height * blocks[0].length), 0, blocks[0].length - 1)];
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return (float)noise.octaveNoise3D(octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    protected void clamp(Vec2 vec){
        float margin = width / 16f;
        vec.x = Math.max(Math.min(vec.x, width - margin), margin);
        vec.y = Math.max(Math.min(vec.y, width - margin), margin);
    }

    @Override
    public void generateSector(Sector sector){
        if(sector.id % 10 == 0){
            sector.generateEnemyBase = false;
            return;
        }

        super.generateSector(sector);
    }

    @Override
    protected void generate(){
        class Room{
            final String name;
            final int x;
            final int y;
            final int radius;

            ObjectSet<Room> connected = new ObjectSet<>();

            Room(String name, int x, int y, int radius){
                this.name = name;

                if((x < 0 || x >= width) || (y < 0 || y >= height)){
                    throw new IllegalArgumentException(Strings.format("'@' out of bounds: (@, @) must be between (@, @) and exclusive (@, @)", name, x, y, 0, 0, width, height));
                }

                this.x = x;
                this.y = y;
                this.radius = radius;
                connected.add(this);
            }

            void connect(Room to){
                if(connected.contains(to)) return;

                connected.add(to);
                to.connected.add(this);
                float nscl = rand.random(12f, 48f);
                int stroke = rand.random(4, 12);

                brush(pathfind(x, y, to.x, to.y, tile -> (tile.solid() ? 3f : 0f) + noise(tile.x, tile.y, 1, 1, 1f / nscl) * 32f, Astar.manhattan), stroke);
            }
        }

        cells(4);
        distort(10f, 12f);

        float roomPos = width / 2f / Mathf.sqrt3;
        int roomMin = 4, roomMax = 8, roomCount = rand.random(roomMin, roomMax);
        Seq<Room> rooms = new Seq<>();

        float minRadius = 0.6f;
        float scl = minRadius + (1f - (float)(roomCount - roomMin) / (float)(roomMax - roomMin)) * (1f - minRadius);
        for(int i = 0; i < roomCount; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(roomPos / 1.3f));
            clamp(Tmp.v2.set(Tmp.v1).add(width / 2f, height / 2f));

            float maxrad = scl * (roomPos - Tmp.v1.len());
            float rad = Math.min(rand.random(9f, maxrad / 2f), 30f);
            rooms.add(new Room("Room-" + i, (int)Tmp.v2.x, (int)Tmp.v2.y, (int)rad));
        }

        Room[] spawn = {null};
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(2, Math.max((int)(sector.threat * 4), 2));
        int angleStep = 5;
        int offset = (int)(width / 2f / Mathf.sqrt3 - rand.random(26f, 49f));
        int offsetAngle = rand.nextInt(360);
        int waterCheck = 5;

        for(int i = 0; i < 360; i += angleStep){
            int angle = offsetAngle + i;

            Tmp.v1.set(
                (int)((width / 2f) + Angles.trnsx(angle, offset)),
                (int)((height / 2f) + Angles.trnsy(angle, offset))
            );

            int waterTiles = 0;

            for(int rx = -waterCheck; rx <= waterCheck; rx++){
                for(int ry = -waterCheck; ry <= waterCheck; ry++){
                    Tile tile = tiles.get((int)Tmp.v1.x + rx, (int)Tmp.v1.y + ry);

                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    }
                }
            }

            if(waterTiles <= 4 || i + angleStep >= 360){
                rooms.add(spawn[0] = new Room("Room-Spawn", (int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(16, 24)));

                for(int j = 0; j < enemySpawns; j++){
                    int enemyOffset = rand.range(60);

                    clamp(Tmp.v2
                        .set(Tmp.v1.x - width / 2f, Tmp.v1.y - height / 2f)
                        .rotate(180f + enemyOffset)
                        .add(width / 2f, height / 2f)
                    );

                    Room espawn = new Room("Room-Espawn" + j, (int)Tmp.v2.x, (int)Tmp.v2.y, rand.random(12, 16));

                    rooms.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        Log.debug("Generated @ rooms", rooms.size);
        for(Room room : rooms){
            Log.debug("Generated room @", room.name);
            erase(room.x, room.y, room.radius);
        }

        int connections = rand.random(Math.max(roomCount - 1, 1), roomCount + 3);
        for(int i = 0; i < connections; i++){
            rooms.random(rand).connect(rooms.random(rand));
        }

        for(Room r : rooms){
            spawn[0].connect(r);
        }

        cells(1);
        distort(10f, 6f);
        inverseFloodFill(tiles.getn(spawn[0].x, spawn[0].y));

        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead, UnityBlocks.oreMonolite);

        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float addscl = 1.3f;

        if(noise.octaveNoise3D(2d, 0.5d, 1d, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.25d * addscl){
            ores.add(Blocks.oreCoal);
        }

        if(noise.octaveNoise3D(2d, 0.5d, 1d, sector.tile.v.x + 1f, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.5d * addscl){
            ores.add(Blocks.oreTitanium);
        }

        if(noise.octaveNoise3D(2d, 0.5d, 1d, sector.tile.v.x + 3f, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.7d * addscl){
            ores.add(Blocks.oreThorium);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.09f, 0.01f) - i * 0.01f);
        }

        trimDark();
        median(2);

        float difficulty = sector.threat;

        for(var entry : dec){
            pass((x, y) -> {
                Block key = entry.key;
                var val = entry.value;

                float chance = val.value[0] + val.value[1] * difficulty;
                if(rand.chance(chance)){
                    boolean found = false;
                    if(floor == key){
                        floor = val.key;
                        found = true;
                    }else if(block == key){
                        block = val.key;
                        found = true;
                    }

                    if(found){
                        try{
                            for(Point2 p : Geometry.d4){
                                var tile = tiles.getn(x + p.x, y + p.y);
                                if(rand.chance(1f / Geometry.d4.length)){
                                    if(tile.floor() == key){
                                        tile.setFloor(val.key.asFloor());
                                    }else if(tile.block() == key){
                                        tile.setBlock(val.key);
                                    }
                                }
                            }
                        }catch(Throwable ignored){}
                    }
                }
            });
        }

        pass((x, y) -> {
            if(floor.asFloor().isLiquid) return;

            float offsetX = x - 4f;
            float offsetY = y + 23f;

            for(int i = ores.size - 1; i >= 0; i--){
                Block entry = ores.get(i);
                float freq = frequencies.get(i);

                if(
                    Math.abs(0.5f - noise(offsetX, offsetY + i * 999f, 2d, 0.7d, (40d + i * 2d))) > 0.22d + i * 0.01d &&
                    Math.abs(0.5f - noise(offsetX, offsetY - i * 999f, 1d, 1d, (30d + i * 4d))) > 0.37d + freq
                ){
                    ore = entry;
                    break;
                }
            }
        });

        //don't generate message blocks if enemy base is present
        if(!sector.hasEnemyBase()){
            Seq<Room> msgRoom = rooms.select(r ->
                r != spawn[0] &&
                !enemies.contains(e ->
                    e.name.equals(r.name) ||
                    Mathf.within(e.x, e.y, r.x, r.y, (state.rules.dropZoneRadius * 1.2f) / tilesize)
                )
            );

            boolean hasMessage = false;
            for(int r = 0; r < msgRoom.size; r++){
                if(hasMessage) break;

                Room room = msgRoom.get(r);
                if(rand.chance(0.3f) || r == msgRoom.size - 1){
                    int angleStep2 = 10;
                    int off = rand.random(360);

                    find:
                    for(int i = 0; i < 360; i += angleStep2){
                        int angle = off + i;

                        Tmp.v1.trns(angle, room.radius - rand.random(room.radius / 2f)).add(room.x, room.y);
                        Tile tile = tiles.getn((int)Tmp.v1.x, (int)Tmp.v1.y);

                        if(rand.chance(0.1f) || i + angleStep2 >= 360){
                            if(tile == null){
                                tile = new Tile((int)Tmp.v1.x, (int)Tmp.v1.y);
                                tiles.set((int)Tmp.v1.x, (int)Tmp.v1.y, tile);
                            }

                            Block[] floors = new Block[]{metalFloor, metalFloor2, metalFloor3, metalFloor5};

                            for(int tx = -4; tx < 4; tx++){
                                for(int ty = -4; ty < 4; ty++){
                                    if(
                                        Mathf.within(tile.x, tile.y, tile.x + tx, tile.y + ty, 4f) &&
                                        rand.chance((4f - Mathf.dst(tile.x, tile.y, tile.x + tx, tile.y + ty)) / 4f)
                                    ){
                                        Tile other = tiles.getn(tile.x + tx, tile.y + ty);
                                        if(other == null){
                                            other = new Tile(tile.x + tx, tile.y + ty);
                                            tiles.set(other.x, other.y, other);
                                        }

                                        Block block = floors[rand.random(floors.length - 1)];

                                        other.setFloor(block.asFloor());
                                        if(other.solid() && !other.synthetic()){
                                            other.setBlock(block);
                                        }
                                    }
                                }
                            }

                            tile.setBlock(loreMonolith, Team.sharded, 0, () -> {
                                LoreMessageBuild build = loreMonolith.newBuilding().as();
                                build.setMessage("lore.unity.megalith-" + sector.id);
                                return build;
                            });

                            Log.debug("Generated a message block at (@, @).", tile.x, tile.y);

                            hasMessage = true;
                            break find;
                        }
                    }
                }
            }
        }

        Schematics.placeLaunchLoadout(spawn[0].x, spawn[0].y);
        enemies.each(espawn -> tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.getn(spawn[0].x, spawn[0].y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        }else{
            state.rules.winWave = sector.info.winWave = 15 * (int)Math.max(difficulty * 5f, 1f);
        }

        float waveTimeDec = 0.3f;

        state.rules.waveSpacing = Mathf.lerp(Time.toSeconds * 50f * 2f, Time.toSeconds * 40f, Math.max(difficulty - waveTimeDec, 0) / 0.8f);
        state.rules.waves = sector.info.waves = true;
        state.rules.enemyCoreBuildRadius = 600f;

        state.rules.lighting = true;
        state.rules.ambientLight = UnityPal.monolithAtmosphere;

        state.rules.spawns = UnityWaves.generate(Faction.monolith, difficulty, new Rand(), state.rules.attackMode);
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }
}
