package unity.planets;

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

import static mindustry.content.Blocks.*;
import static mindustry.Vars.*;

public class MegalithPlanetGenerator extends PlanetGenerator{
    Simplex noise = new Simplex();
    RidgedPerlin rid = new RidgedPerlin(1, 2);
    BaseGenerator basegen = new BaseGenerator();
    float scl = 6f;
    float waterOffset = 0.07f;

    Block[][] arr = {
        {water, darksandWater, snow, snow, snow, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice, ice, ice},
        {water, darksandWater, darksand, stone, snow, snow, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, stone, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice},
        {water, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice},

        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, basalt, snow, snow, iceSnow, iceSnow, ice},
        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, basalt, basalt, snow, snow, iceSnow, ice},
        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, basalt, basalt, snow, snow, iceSnow, ice},
        {deepwater, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, basalt, snow, snow, iceSnow, iceSnow, ice},

        {water, water, darksandWater, darksand, darksand, darksand, stone, stone, stone, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice},
        {water, water, darksandWater, darksand, darksand, stone, stone, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice},
        {water, darksandWater, darksand, stone, snow, snow, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice, ice},
        {water, darksandWater, snow, snow, snow, snow, snow, iceSnow, iceSnow, iceSnow, iceSnow, ice, ice, ice, ice}
    };

    float waterf = 2f / arr[0].length;

    float rawHeight(Vec3 position){
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

        return arr[Mathf.clamp((int)(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp((int)(height * arr[0].length), 0, arr[0].length - 1)];
    }

    @Override
    protected float noise(float x, float y, double octaves, double falloff, double scl, double mag){
        Vec3 v = sector.rect.project(x, y).scl(5f);
        return (float)noise.octaveNoise3D(octaves, falloff, 1f / scl, v.x, v.y, v.z) * (float)mag;
    }

    @Override
    protected void generate(){
        throw new Error("this is called you eye sore");
    }

    @Override
    public void postGenerate(Tiles tiles){
        if(sector.hasEnemyBase()){
            basegen.postGenerate();
        }
    }
}