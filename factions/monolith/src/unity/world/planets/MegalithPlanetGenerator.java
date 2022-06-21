package unity.world.planets;

import arc.func.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.noise.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.graphics.g3d.*;
import mindustry.graphics.g3d.PlanetGrid.*;
import mindustry.maps.generators.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.content.*;
import unity.graphics.g3d.PUMeshBuilder.*;

/**
 * Planet mesh and sector generator for the {@linkplain MonolithPlanets#megalith megalith} planet.
 * Terrain:
 * <ul>
 *     <li>Temperature is cold around the poles, hot across the equator.</li>
 *     <li>Air pressure is more intense across the equator, due to the amount of eneraphyte in the air.</li>
 *     <li>Terrain is dominated by rocks, mostly slates. Most of them erodes and discolors due to temperature and pressure.</li>
 *     <li>Streams of (hot) liquefied eneraphyte from both poles, goes thinner as it approaches the equator.</li>
 *     <li>Crater... somewhere. Barely any exposed ground liquid. Lots of eneraphyte infusion, emission, and crystals.</li>
 *     <li>Ruinous structures, progressively more common near the crater.</li>
 * </ul>
 * @author GlennFolker
 */
public class MegalithPlanetGenerator extends PlanetGenerator implements PUHexMesher{
    protected static final Color color = new Color();
    protected static final Vec3 rad = new Vec3(), v31 = new Vec3(), v32 = new Vec3(), v33 = new Vec3();

    public static float
    volcanoRadius = 0.16f, volcanoFalloff = 0.3f, volcanoEdgeDeviation = 0.1f, volcanoEdge = 0.06f,
    volcanoHeight = 0.7f, volcanoHeightDeviation = 0.2f, volcanoHeightInner = volcanoHeight - 0.05f;

    protected boolean initialized = false;

    protected PlanetGrid grid;
    protected float[] heights;
    protected int[] flags;

    protected static final int
        flagFlow = 1;

    @Override
    public void init(int divisions, boolean lines, float radius, float intensity){
        grid = PlanetGrid.create(divisions);
        heights = new float[grid.tiles.length];
        flags = new int[grid.tiles.length];

        calculateHeight(intensity);
        Cons2<Ptile, Float> flow = (init, pole) -> {
            IntSet used = new IntSet();
            Seq<Ptile> queue = new Seq<>();
            Seq<Ptile> candidates = new Seq<>();

            queue.add(init);
            used.add(init.id);

            int flowIndex = -1, flowCount = Mathf.randomSeed(init.id, 16, 36);
            while(!queue.isEmpty() && ++flowIndex < flowCount){
                candidates.clear();

                Ptile current = queue.pop();
                flags[current.id] |= flagFlow;

                float prog = flowIndex / (flowCount - 1f), heightDeviation = 0.2f, dotDeviation = 0.3f;
                int maxFlood = Mathf.ceilPositive(current.edgeCount * Interp.pow2In.apply(1f - prog));

                float height = heights[current.id] * 1.1f;
                for(Ptile neighbor : current.tiles){
                    if(!used.contains(neighbor.id) && heights[neighbor.id] <= height + Mathf.randomSeed(neighbor.id, heightDeviation) && v31.set(neighbor.v).sub(init.v).dot(0f, pole, 0f) <= 0.5f + Mathf.randomSeedRange(neighbor.id + 1, dotDeviation / 2f)){
                        candidates.add(neighbor);
                    }
                }

                if(candidates.isEmpty()){
                    candidates.add(Structs.findMin(current.tiles, tile -> used.contains(tile.id) ? Float.MAX_VALUE : heights[tile.id]));
                    if(v31.set(candidates.first().v).sub(init.v).dot(0f, pole, 0f) > 0.5f + Mathf.randomSeed(candidates.first().id + 1, dotDeviation / 2f)) candidates.clear();
                }else{
                    candidates.sort(tile -> heights[tile.id] * -(v31.set(tile.v).sub(init.v).dot(0f, pole, 0f) + Mathf.randomSeed(tile.id + 1, dotDeviation / 2f)));
                }

                for(int i = 0, max = Math.min(maxFlood, candidates.size); i < max; i++){
                    Ptile tile = candidates.get(i);
                    if(used.add(tile.id)) queue.add(tile);
                }
            }
        };

        for(int sign : Mathf.signs){
            for(long i = 0; i < 12; i++){
                Tmp.v1.trns(i / 12f * 360f + Mathf.randomSeed(seed + i * sign, 1 / 12f * 360f * 0.67f), 1f).setLength(volcanoRadius(v31.set(Tmp.v1.x, 0.25f * sign, Tmp.v1.y)));
                v31.set(Tmp.v1.x, sign, Tmp.v1.y);

                flow.get(Structs.findMin(grid.tiles, t -> t.v.dst2(v31)), (float)sign);
            }
        }

        initialized = true;
        calculateHeight(intensity);
    }

    protected void calculateHeight(float intensity){
        for(int i = 0, tlen = grid.tiles.length; i < tlen; i++){
            Ptile tile = grid.tiles[i];
            float height = 0f;

            int clen = tile.corners.length;
            for(int j = 0; j < clen; j++){
                Corner corner = tile.corners[j];
                height += (1f + getHeight(corner, v31.set(corner.v))) * intensity;
            }

            heights[i] = height / clen;
        }
    }

    protected float volcanoRadius(Vec3 position){
        float pole = position.dst2(0f, 1f, 0f) < position.dst2(0f, -1f, 0f) ? 1f : -1f;
        rad.set(position).sub(0f, pole, 0f).setLength(volcanoRadius).add(0f, pole, 0f);

        return volcanoRadius + Simplex.noise3d(seed + 1, 3d, 0.5d, 0.7d, rad.x, rad.y, rad.z) * volcanoEdgeDeviation;
    }

    protected Block getBlock(Ptile tile, Vec3 position){
        // Raw block, yet to be processed further.
        //TODO floor noise
        Block block = MonolithBlocks.erodedSlate;

        // Volcano stream.
        //TODO eneraphyte liquid block
        if((flags[tile.id] & flagFlow) == flagFlow || Math.min(position.dst(0f, 1f, 0f), position.dst(0f, -1f, 0f)) <= volcanoRadius(position)) block = Blocks.slag;

        return block;
    }

    @Override
    public float getHeight(Corner corner, Vec3 position){
        // Raw terrain height, yet to be processed further.
        float height = Simplex.noise3d(seed, 4d, 0.6d, 1.1d, position.x, position.y, position.z);

        // Volcano height.
        float
            volcanoRad = volcanoRadius(position),
            volcanoDst = Math.min(position.dst(0f, 1f, 0f), position.dst(0f, -1f, 0f));
        if(volcanoDst <= volcanoRad + volcanoFalloff){
            // 0 -> near, 1 -> far.
            float volcanoProg = (volcanoDst - volcanoRad) / volcanoFalloff;
            if(initialized) volcanoProg = Mathf.clamp(volcanoProg);

            // Raw terrain height goes down, the volcano height goes up.
            height = Mathf.lerp(
                height * Interp.pow5Out.apply(volcanoProg),
                (volcanoHeight + Simplex.noise3d(seed, 3d, 0.5d, 0.56d, position.x, position.y, position.z) * volcanoHeightDeviation) * Interp.smoother.apply(1f - volcanoProg),
                1f - volcanoProg
            );

            if(initialized && volcanoDst <= volcanoRad) height = Mathf.lerp(height, volcanoHeightInner, Interp.smoother.apply(1f - Mathf.clamp((volcanoDst - volcanoRad) / volcanoEdge)));
        }

        return height;
    }

    @Override
    public Color getColor(Ptile tile, Vec3 position){
        Block block = getBlock(tile, position);
        return color.set(block.mapColor).a(1f - block.albedo);
    }

    @Override
    public float getHeight(Vec3 position){
        v33.set(position).nor();
        return getHeight(Structs.findMin(grid.corners, c -> v32.set(c.v).nor().dst2(v33)), position);
    }

    @Override
    public Color getColor(Vec3 position){
        v33.set(position).nor();
        return getColor(Structs.findMin(grid.tiles, t -> v32.set(t.v).nor().dst2(v33)), position);
    }

    @Override
    public boolean allowLanding(Sector sector){
        //TODO avoid landing on crater sectors until the "requirements" have been fulfilled.
        return super.allowLanding(sector);
    }

    @Override
    public void generateSector(Sector sector){
        //TODO i don't know how the bases should be...
    }

    @Override
    public void addWeather(Sector sector, Rules rules){
        //TODO i don't know how any of this works (yet), when i do i'll make the sectors have a "very cool" megalith climate
        super.addWeather(sector, rules);
    }

    @Override
    protected void genTile(Vec3 position, TileGen tile){
        v33.set(position).nor();
        tile.floor = getBlock(Structs.findMin(grid.tiles, t -> v32.set(t.v).nor().dst2(v33)), position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 20f) > 0.67f) tile.block = Blocks.air;
    }
}
