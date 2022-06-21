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
    volcanoRadius = 0.16f, volcanoFalloff = 0.3f, volcanoEdgeDeviation = 0.04f, volcanoEdge = 0.06f,
    volcanoHeight = 0.7f, volcanoHeightDeviation = 0.2f, volcanoHeightInner = volcanoHeight - 0.07f;

    protected final PlanetGrid grid = PlanetGrid.create(6);
    protected float[] heights = new float[grid.tiles.length];
    protected int[] flags = new int[grid.tiles.length];

    protected static final int
        flagFlow = 1;

    {
        for(int i = 0, tlen = grid.tiles.length; i < tlen; i++){
            Ptile tile = grid.tiles[i];
            float height = 0f;

            int clen = tile.corners.length;
            for(int j = 0; j < clen; j++){
                Corner corner = tile.corners[j];
                height += (1f + getHeight(corner, v31.set(corner.v))) * 0.2f;
            }

            heights[i] = height / clen;
        }

        Cons<Ptile> flow = init -> {
            IntSet used = new IntSet();
            Queue<Ptile> queue = new Queue<>();
            Seq<Ptile> candidates = new Seq<>();

            queue.add(init);
            used.add(init.id);

            int flowIndex = -1, flowCount = 24;
            while(!queue.isEmpty() && ++flowIndex < flowCount){
                Ptile current = queue.removeFirst();
                flags[current.id] |= flagFlow;

                // 0 -> near, 1 -> far.
                float prog = flowIndex / (flowCount - 1f);
                int maxFlood = Mathf.ceilPositive(current.edgeCount * Interp.pow2In.apply(1f - prog));

                float height = heights[current.id] * 0.93f;
                for(Ptile neighbor : current.tiles) if(heights[neighbor.id] <= height) candidates.add(neighbor);

                if(candidates.isEmpty()){
                    candidates.add(Structs.findMin(current.tiles, tile -> heights[tile.id]));
                }else{
                    candidates.sort(tile -> heights[tile.id]);
                }

                for(int i = 0, max = Math.min(maxFlood, candidates.size); i < max; i++){
                    Ptile tile = candidates.get(i);
                    if(used.add(tile.id)) queue.add(tile);
                }
            }
        };

        flow.get(Structs.find(grid.tiles, t -> t.v.epsilonEquals(0f, 1f, 0f, 0.1f)));
        flow.get(Structs.find(grid.tiles, t -> t.v.epsilonEquals(0f, -1f, 0f, 0.1f)));
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
        if((flags[tile.id] & flagFlow) == flagFlow) block = Blocks.slag;

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
            float volcanoProg = Mathf.clamp((volcanoDst - volcanoRad) / volcanoFalloff);
            // Raw terrain height goes down, the volcano height goes up.
            height = Mathf.lerp(
                height * Interp.pow2Out.apply(volcanoProg),
                (volcanoHeight + Simplex.noise3d(seed, 3d, 0.5d, 0.56d, position.x, position.y, position.z) * volcanoHeightDeviation) * Interp.smoother.apply(1f - volcanoProg),
                1f - volcanoProg
            );

            if(volcanoDst <= volcanoRad) height = Mathf.lerp(height, volcanoHeightInner, Interp.smoother.apply(1f - Mathf.clamp((volcanoDst - volcanoRad) / volcanoEdge)));
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
        return getHeight(Structs.find(grid.corners, c -> v32.set(c.v).nor().epsilonEquals(v33, 0.01f)), position);
    }

    @Override
    public Color getColor(Vec3 position){
        v33.set(position).nor();
        return getColor(Structs.find(grid.tiles, t -> v32.set(t.v).nor().epsilonEquals(v33, 0.01f)), position);
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
        tile.floor = getBlock(sector.tile, position);
        tile.block = tile.floor.asFloor().wall;

        if(Ridged.noise3d(seed + 1, position.x, position.y, position.z, 2, 20f) > 0.67f) tile.block = Blocks.air;
    }
}
