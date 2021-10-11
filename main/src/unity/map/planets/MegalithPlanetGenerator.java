package unity.map.planets;

import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Log.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.*;
import unity.content.*;
import unity.graphics.*;
import unity.map.*;
import unity.mod.*;
import unity.world.blocks.LoreMessageBlock.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;
import static unity.content.UnityBlocks.*;

/** @author GlennFolker */
public class MegalithPlanetGenerator extends PlanetGenerator{
    protected BaseGenerator basegen = new BaseGenerator();
    protected float scl = 4.5f;
    protected float waterOffset = 0.1f;

    protected Block[][] blocks = {
        {deepwater, water, water, water, darksandWater, darksandWater, darksand, basalt, sharpslate, darksand},
        {deepwater, water, water, darksandWater, darksand, sharpslate, sharpslate, basalt, sharpslate, dacite},
        {deepwater, water, darksandWater, darksand, darksand, basalt, sharpslate, sharpslate, darksand, snow},
        {deepwater, water, darksandWater, darksand, sharpslate, darksand, darksand, sharpslate, sharpslate, sharpslate},

        {water, darksandWater, darksand, darksand, basalt, darksandWater, snow, sharpslate, sharpslate, dacite},
        {water, darksandWater, darksand, sharpslate, darksandWater, water, darksandWater, dacite, sharpslate, dacite},
        {darksandWater, darksand, darksand, darksandWater, water, deepwater, water, darksandWater, dacite, sharpslate},
        {sharpslate, sharpslate, sharpslate, sharpslate, darksandWater, water, darksandWater, dacite, snow, snow},

        {sharpslate, darksand, sharpslate, dacite, snow, darksandWater, snow, sharpslate, snow, iceSnow},
        {sharpslate, dacite, dacite, dacite, sharpslate, sharpslate, snow, snow, iceSnow, ice},
        {dacite, dacite, sharpslate, dacite, snow, snow, snow, snow, iceSnow, ice},
        {dacite, snow, snow, snow, sharpslate, snow, snow, iceSnow, ice, ice}
    };

    protected float waterHeight = 2f / blocks[0].length;

    protected Vec3 crater = new Vec3(-0.023117876f, 0.36916345f, -0.9290769f);
    protected float craterRadius = 0.36f;
    protected float craterDepth = 1f;

    protected boolean withinCrater(Vec3 position){
        return withinCrater(position, 0f);
    }

    protected boolean withinCrater(Vec3 position, float excess){
        return position.within(crater, craterRadius + excess);
    }

    protected float rawHeight(Vec3 position){
        Tmp.v33.set(position).scl(scl);
        float res = (Mathf.pow(Simplex.noise3d(0, 6d, 0.5d, 1d / 3d, Tmp.v33.x, Tmp.v33.y, Tmp.v33.z), 2.3f) + waterOffset) / (1f + waterOffset);

        if(withinCrater(position, 0.03f)){
            float n = Simplex.noise3d(0, 8.4d, 0.4d, 0.27d, Tmp.v33.x, Tmp.v33.y, Tmp.v33.z) * (craterRadius / 4f);
            float depth = Interp.pow2Out.apply(1f - (position.dst(crater) / craterRadius));

            return res - (craterDepth * depth + (1f - depth) * n);
        }

        return res;
    }

    @Override
    public float getHeight(Vec3 position){
        float height = rawHeight(position);
        return withinCrater(position) ? height : Math.max(height, waterHeight);
    }

    @Override
    public Color getColor(Vec3 position){
        Block block = getBlock(position);

        Color base = Tmp.c1.set(block.mapColor);
        if(block == sharpslate){
            float res = Simplex.noise3d(0, 6d, 0.5d, 0.5d, position.x, position.y, position.z) * 0.2f;
            base.lerp(UnityPal.monolithLight, res);
        }

        return base.a(1f - block.albedo);
    }

    @Override
    public void genTile(Vec3 position, TileGen tile){
        tile.floor = getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(1, position.x, position.y, position.z, 3, 22f) > 0.32f){
            tile.block = Blocks.air;
        }
    }

    Block[] getBlockset(Vec3 position){
        position = Tmp.v33.set(position).scl(scl);

        float rad = scl;
        float temp = Mathf.clamp(Math.abs(position.y * 2f) / rad);
        float tnoise = Simplex.noise3d(0, 7, 0.56, 1f / 3f, position.x, position.y + 999f, position.z);

        temp = Mathf.lerp(temp, tnoise, 0.5f);

        return blocks[Mathf.clamp((int)(temp * blocks.length), 0, blocks.length - 1)];
    }

    protected Block getBlock(Vec3 position){
        var set = getBlockset(position);

        int i = Mathf.clamp((int)(Mathf.clamp(rawHeight(position) * 1.2f) * set.length), 0, set.length - 1);
        if(withinCrater(position, 0.1f)){
            while(i < set.length && set[i].asFloor().isLiquid){
                i++;
            }
        }

        return set[i];
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(this.scl);
        return Simplex.noise3d(0, octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
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
        //temporary "room" class, used for opening up spaces in the tile set
        class Room{
            final String name;
            final int x;
            final int y;
            final int radius;

            final ObjectSet<Room> connected = new ObjectSet<>();

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

                brush(pathfind(x, y, to.x, to.y, tile -> (tile.solid() ? 5f : 0f) + noise(tile.x, tile.y, 1, 1, 1f / nscl) * 32f, Astar.manhattan), stroke);
            }
        }

        cells(4);
        distort(10f, 12f);

        //generate empty rooms
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

        //generate spawn and enemy rooms
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

        Unity.print(LogLevel.debug, Strings.format("Generated @ rooms", rooms.size));
        for(Room room : rooms){
            Unity.print(LogLevel.debug, Strings.format("Generated room @", room.name));
            erase(room.x, room.y, room.radius);
        }

        //connect random rooms
        int connections = rand.random(Math.max(roomCount - 1, 1), roomCount + 3);
        for(int i = 0; i < connections; i++){
            rooms.random(rand).connect(rooms.random(rand));
        }

        //connect all rooms to the spawn room
        for(Room r : rooms){
            spawn[0].connect(r);
        }

        //room post-processing
        cells(1);
        distort(10f, 6f);

        median(2);

        float difficulty = sector.threat;

        //replace sand with dark sand
        pass((x, y) -> {
            if(floor == sand) floor = darksand;
            if(block == sandWall) block = duneWall;
        });

        //noise archaic and infused sharpslates
        pass((x, y) -> {
            if(floor == sharpslate){
                float sel = noise(x, y, 4d, 17d, 460d, 0.84d);
                if(sel < 0.5f || !rand.chance(sel)) return;

                float noise = noise(x, y, 6d, 30d, 360d, 0.63d);
                if(noise > 0.4f){
                    floor = archSharpslate;
                    if(block == sharpslate.asFloor().wall){
                        block = archSharpslate.asFloor().wall;
                    }
                }else if(noise > 0.3f){
                    floor = infusedSharpslate;
                    if(block == sharpslate.asFloor().wall){
                        block = infusedSharpslate.asFloor().wall;
                    }
                }
            }
        });

        //scatter archaic energies on top of archaic sharpslates
        Block target = archSharpslate;
        Block over = archEnergy;
        pass((x, y) -> {
            float start = 0.03f;
            float inc = 0.01f;

            float chance = start + inc * difficulty;
            if(rand.chance(chance)){
                boolean found = false;
                if(floor == target){
                    ore = over;
                    found = true;
                }

                if(found){
                    for(Point2 p : Geometry.d4){
                        var tile = tiles.get(x + p.x, y + p.y);
                        if(tile != null && tile.floor() == target && rand.chance(1f / Geometry.d4.length)){
                            tile.setOverlay(over);
                        }
                    }
                }
            }
        });

        //generate ore frequencies
        Seq<Block> ores = Seq.with(Blocks.oreCopper, Blocks.oreLead, UnityBlocks.oreMonolite);

        float poles = Math.abs(sector.tile.v.y);
        float nmag = 0.5f;
        float addscl = 1.3f;

        if(Simplex.noise3d(0, 2d, 0.5d, 1d, sector.tile.v.x, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.25d * addscl){
            ores.add(Blocks.oreCoal);
        }

        if(Simplex.noise3d(0, 2d, 0.5d, 1d, sector.tile.v.x + 1f, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.5d * addscl){
            ores.add(Blocks.oreTitanium);
        }

        if(Simplex.noise3d(0, 2d, 0.5d, 1d, sector.tile.v.x + 2f, sector.tile.v.y, sector.tile.v.z) * nmag + poles > 0.7d * addscl){
            ores.add(Blocks.oreThorium);
        }

        if(rand.chance(0.3f)){
            ores.add(Blocks.oreScrap);
        }

        FloatSeq frequencies = new FloatSeq();
        for(int i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.09f, 0.01f) - i * 0.01f);
        }

        //generate ores in tile set
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

        trimDark();
        inverseFloodFill(tiles.getn(spawn[0].x, spawn[0].y));

        //generate random lore message block
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
                                    if(Mathf.within(tile.x, tile.y, tile.x + tx, tile.y + ty, 4f) && rand.chance((4f - Mathf.dst(tile.x, tile.y, tile.x + tx, tile.y + ty)) / 4f)){
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

                            Unity.print(LogLevel.debug, Strings.format("Generated a message block at (@, @).", tile.x, tile.y));

                            hasMessage = true;
                            break;
                        }
                    }
                }
            }
        }

        //place launch loadout + enemy spawn point
        Schematics.placeLaunchLoadout(spawn[0].x, spawn[0].y);
        enemies.each(espawn -> tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        //generate enemy base if necessary
        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.getn(spawn[0].x, spawn[0].y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = sector.info.attack = true;
        }else{
            state.rules.winWave = sector.info.winWave = 15 * (int)Math.max(difficulty * 5f, 1f);
        }

        //rules applications
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
