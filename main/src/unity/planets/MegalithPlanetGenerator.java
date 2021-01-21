package unity.planets;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.ai.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.maps.generators.*;
import mindustry.world.*;
import mindustry.world.blocks.logic.MessageBlock.*;
import unity.content.*;

import static mindustry.Vars.*;
import static mindustry.content.Blocks.*;

public class MegalithPlanetGenerator extends PlanetGenerator{
    protected RidgedPerlin rid = new RidgedPerlin(1, 2);
    protected BaseGenerator basegen = new BaseGenerator();
    protected float scl = 6f;
    protected float waterOffset = 0.07f;

    protected int msgIndex;

    protected Block[][] blocks = {
        {water, darksandWater, snow, snow, snow, snow, snow, snow, snow, iceSnow, ice, ice, ice, ice},
        {water, darksandWater, darksand, stone, snow, snow, snow, snow, iceSnow, iceSnow, iceSnow, ice, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, snow, snow, iceSnow, snow, iceSnow, iceSnow, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, stone, stone, snow, iceSnow, snow, snow, ice, ice},

        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, basalt, snow, snow, iceSnow, iceSnow, ice},
        {deepwater, water, darksandWater, darksand, darksand, stone, stone, stone, basalt, basalt, snow, snow, snow, ice},
        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, basalt, basalt, snow, snow, iceSnow, ice},
        {deepwater, water, darksandWater, darksand, darksand, stone, stone, stone, basalt, snow, snow, iceSnow, iceSnow, ice},

        {water, water, darksandWater, darksand, darksand, darksand, stone, stone, snow, iceSnow, iceSnow, iceSnow, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, snow, snow, iceSnow, snow, snow, iceSnow, ice, ice},
        {water, darksandWater, darksand, stone, snow, snow, snow, iceSnow, snow, snow, iceSnow, ice, ice, ice},
        {water, darksandWater, snow, snow, snow, snow, snow, snow, iceSnow, snow, ice, ice, ice, ice}
    };

    protected float waterf = 3f / blocks[0].length;

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

    @Override
    protected void generate(){
        class Room{
            int x;
            int y;
            int radius;

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
                to.connected.add(this);
                int nscl = rand.random(20, 60);
                int stroke = rand.random(4, 12);

                try{
                    brush(pathfind(x, y, to.x, to.y, tile -> (tile.solid() ? 5f : 0f) + noise(tile.x, tile.y, 1d, 1d, 1d / nscl) * 48f, Astar.manhattan), stroke);
                }catch(Throwable e){
                    Log.info("@, @", x, y);
                }
            }
        }

        cells(4);
        distort(10f, 12f);

        float roomPos = width / 2f / Mathf.sqrt3;
        int roomCount = rand.random(4, 8);
        Seq<Room> rooms = new Seq<>();

        for(int i = 0; i < roomCount; i++){
            Tmp.v1.trns(rand.random(360f), rand.random(roomPos));
            float tx = width / 2f + Tmp.v1.x;
            float ty = height / 2f + Tmp.v2.y;
            float rad = Math.min(rand.random(6f, (roomPos - Tmp.v1.len()) / 2f), 24f);

            rooms.add(new Room((int)tx, (int)ty, (int)rad));
        }

        Room spawn = null;
        Seq<Room> enemies = new Seq<>();
        int enemySpawns = rand.random(2, Math.max((int)(sector.threat * 4), 2));
        int angleStep = 5;
        int offset = width / 2 - rand.random(26, 49);
        int offsetAngle = rand.nextInt(360);
        int waterCheck = 5;

        for(int i = 0; i < 360; i += angleStep){
            int angle = offsetAngle + i;

            int tx = (int)(width / 2 + Angles.trnsx(angle, offset));
            int ty = (int)(height / 2 + Angles.trnsx(angle, offset));

            int waterTiles = 0;

            for(int rx = -waterCheck; rx <= waterCheck; rx++){
                for(int ry = -waterCheck; ry <= waterCheck; ry++){
                    Tile tile = tiles.get(tx + rx, ty + ry);

                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    }
                }
            }

            if(waterTiles <= 4 || i + angleStep >= 360){
                rooms.add(spawn = new Room(tx, ty, rand.random(16, 24)));

                for(int j = 0; j < enemySpawns; j++){
                    int enemyOffset = rand.range(60);

                    Tmp.v1.set(tx - width / 2f, ty - height / 2f).rotate(180f + enemyOffset).add(width / 2f, height / 2f);

                    Room espawn = new Room((int)Tmp.v1.x, (int)Tmp.v1.y, rand.random(12, 16));

                    rooms.add(espawn);
                    enemies.add(espawn);
                }

                break;
            }
        }

        rooms.each(r -> erase(r.x, r.y, r.radius));

        int connections = rand.random(Math.max(roomCount - 1, 1), roomCount + 3);
        for(int i = 0; i < connections; i++){
            rooms.random(rand).connect(rooms.random(rand));
        }

        for(Room r : rooms){
            spawn.connect(r);
        }

        cells(1);
        distort(10f, 6f);
        inverseFloodFill(tiles.getn(spawn.x, spawn.y));
        
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
        tech();

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

        float difficulty = sector.threat;
        ints.clear();
        ints.ensureCapacity(width * height / 4);

        boolean hasMessage = false;
        for(Room r : rooms){
            if(r == spawn) continue;
            if(hasMessage) return;

            int angleStep2 = 10;
            int off = rand.random(360);

            for(int i = 0; i < 360; i += angleStep2){
                int angle = off + i;

                Tmp.v1.trns(angle, r.radius - rand.random(r.radius / 2f)).add(r.x, r.y);

                Tile tile = tiles.get((int)Tmp.v1.x, (int)Tmp.v1.y);
                if(rand.chance(0.1f) || i + angleStep2 >= 360){
                    hasMessage = true;

                    if(tile == null){
                        tile = new Tile((int)Tmp.v1.x, (int)Tmp.v1.y);
                        tiles.set((int)Tmp.v1.x, (int)Tmp.v1.y, tile);
                    }

                    tile.setFloor(Blocks.metalFloor.asFloor());
                    tile.setBlock(Blocks.message, Team.sharded, 0, () -> {
                        MessageBuild build = (MessageBuild)Blocks.message.buildType.get();
                        build.message.append(nextMessage());

                        return build;
                    });

                    Log.info("@, @", (int)Tmp.v1.x, (int)Tmp.v1.y);
                }
            }
        }

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn -> tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        if(sector.hasEnemyBase()){
            basegen.generate(tiles, enemies.map(r -> tiles.getn(r.x, r.y)), tiles.get(spawn.x, spawn.y), state.rules.waveTeam, sector, difficulty);

            state.rules.attackMode = true;
        }else{
            state.rules.winWave = 15 * (int)Math.max(difficulty * 5f, 1f);
        }

        float waveTimeDec = 0.3f;

        state.rules.waveSpacing = Mathf.lerp(60f * 65f * 2f, 60f * 60f, Math.max(difficulty - waveTimeDec, 0) / 0.8f);
        state.rules.waves = sector.info.waves = true;
        state.rules.enemyCoreBuildRadius = 600;

        state.rules.spawns = Waves.generate(difficulty, new Rand(), state.rules.attackMode);
    }

    protected String nextMessage(){
        return Core.bundle.get("lore.unity.megalith-" + msgIndex++, "...");
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }
}
