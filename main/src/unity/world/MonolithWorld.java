package unity.world;

import arc.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import mindustry.ai.*;
import mindustry.core.*;
import mindustry.game.EventType.*;
import mindustry.world.*;
import unity.gen.*;
import unity.mod.*;

import static mindustry.Vars.*;

/**
 * Additional class similar to {@link BlockIndexer} to index {@linkplain Faction#monolith monolith} blocks.
 * @author GlennFolker
 */
//TODO this probably would be better off as a universal world context binding, rather than just monolith.
public class MonolithWorld{
    public static final int chunkSize = 10;

    private Chunk[] chunks;
    private int width;

    private static float lastPriority;
    private static Chunk lastChunk;

    public MonolithWorld(){
        Events.on(WorldLoadEvent.class, e -> reload());
        Events.on(TileChangeEvent.class, e -> changed(e.tile));
    }

    public void reload(){
        width = Mathf.ceilPositive(world.width() / (float)chunkSize) * chunkSize;
        int h = Mathf.ceilPositive(world.height() / (float)chunkSize) * chunkSize;

        chunks = new Chunk[width * h];
        for(int y = 0; y < h; y++){
            for(int x = 0; x < width; x++){
                chunks[x + y * width] = new Chunk(
                    x, y,
                    Math.min(world.width() - x * chunkSize, chunkSize),
                    Math.min(world.height() - y * chunkSize, chunkSize)
                );
            }
        }

        for(Chunk chunk : chunks) chunk.updateAll();
    }

    public void changed(Tile tile){
        Chunk chunk = getChunk(tile.x, tile.y);
        if(chunk != null) chunk.update(tile);
    }

    public Chunk getChunk(int x, int y){
        if(!world.tiles.in(x, y)) return null;
        return chunks[x / chunkSize + y / chunkSize * width];
    }

    public void intersect(int x, int y, int width, int height, Cons<Chunk> cons){
        width = Math.min(width, this.width - x);
        height = Math.min(height, (chunks.length / this.width) - y);
        x = Math.max(x, 0);
        y = Math.max(y, 0);

        int tx = x / chunkSize, ty = y / chunkSize,
            tw = Math.min(Mathf.ceilPositive((x + width) / (float)chunkSize) * chunkSize, this.width),
            th = Math.min(Mathf.ceilPositive((y + height) / (float)chunkSize) * chunkSize, chunks.length / this.width);

        for(int cy = ty; cy < th; cy++){
            int pos = cy * this.width;
            for(int cx = tx; cx < tw; cx++){
                cons.get(chunks[cx + pos]);
            }
        }
    }

    public Chunk nearest(float x, float y, float range, Floatf<Chunk> priority){
        lastChunk = null;
        lastPriority = 0f;

        int r = World.toTile(range) * 2;
        intersect(World.toTile(x), World.toTile(y), r, r, c -> {
            if(lastChunk == null || lastPriority < priority.get(c)) lastChunk = c;
        });

        return lastChunk;
    }

    public static class Chunk{
        public int x;
        public int y;
        public int width;
        public int height;

        public float centerX;
        public float centerY;

        public Seq<Tile> monolithTiles = new Seq<>();
        public IntSet monolithTilePos = new IntSet();

        public Chunk(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean within(Position pos){
            return within(pos.getX(), pos.getY());
        }

        public boolean within(float x, float y){
            return
                x >= World.unconv(this.x) && x <= World.unconv(this.width) &&
                y >= World.unconv(this.y) && y <= World.unconv(this.height);
        }

        public void addMonolithTile(Tile tile){
            if(monolithTilePos.add(tile.pos())){
                monolithTiles.add(tile);
            }
        }

        public void removeMonolithTile(Tile tile){
            if(monolithTilePos.remove(tile.pos())){
                monolithTiles.remove(tile);
            }
        }

        public void update(Tile tile){
            if(tile == null) return;
            if(FactionMeta.map((tile.solid() && !tile.synthetic()) ? tile.block() : tile.floor()) == Faction.monolith){
                addMonolithTile(tile);
            }else{
                removeMonolithTile(tile);
            }
        }

        public void updateAll(){
            centerX = width / 2f - x;
            centerY = height / 2f - y;

            monolithTiles.clear();
            monolithTilePos.clear();

            for(int y = this.y; y < height; y++){
                for(int x = this.x; x < width; x++) update(world.tile(x, y));
            }
        }
    }
}
