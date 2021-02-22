package unity.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import unity.graphics.*;
import unity.util.*;

import static arc.Core.atlas;

public class SporeFarm extends Block{
    static final int frames = 5;
    int gTimer;
    
    public final TextureRegion[] sporeRegions = new TextureRegion[frames], groundRegions = new TextureRegion[frames];
    public TextureRegion[] fenceRegions;
    public TextureRegion cageFloor;

    public SporeFarm(String name){
        super(name);
        
        update = true;
        gTimer = timers++;
    }

    @Override
    public void load(){
        super.load();
        
        for(int i = 0; i < 5; i++){
            sporeRegions[i] = atlas.find(name + "-spore" + (i + 1));
            groundRegions[i] = atlas.find(name + "-ground" + (i + 1));
        }
        
        fenceRegions = Utils.getRegions(atlas.find(name + "-fence"), 12, 4);
        cageFloor = atlas.find(name + "-floor");
    }

    public class SporeFarmBuild extends Building{
        float growth, delay = -1;
        int tileIndex = -1;
        boolean needsTileUpdate;

        boolean randomChk(){
            var cTile = Vars.world.tile(tileX() + Mathf.range(3), tileY() + Mathf.range(3));
            
            return cTile != null && cTile.floor().liquidDrop == Liquids.water;
        }

        void updateTilings(){
            tileIndex = 0;
            
            for(int i = 0; i < 8; i++){
                var other = tile.nearby(Geometry.d8(i));
                
                if(other == null || !(other.build instanceof SporeFarmBuild)) continue;
                tileIndex += 1 << i;
            }
        }

        void updateNeighbours(){
            for(int i = 0; i < 8; i++){
                var other = tile.nearby(Geometry.d8(i));
                
                if(other == null || !(other.build instanceof SporeFarmBuild b)) continue;
                b.needsTileUpdate = true;
            }
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();
            
            updateNeighbours();
        }

        @Override
        public void updateTile(){
            if(tileIndex == -1){
                updateTilings();
                updateNeighbours();
            }
            if(needsTileUpdate){
                updateTilings();
                needsTileUpdate = false;
            }
            if(timer(gTimer, (60f + delay) * 5f)){
                if(delay == -1){
                    delay = (tileX() * 89f + tileY() * 13f) % 21f;
                }else{
                    boolean chk = randomChk();
                    
                    if(growth == 0f && !chk) return;
                    growth += chk ? growth > frames - 2 ? 0.1f : 0.45f : -0.1f;
                    
                    if(growth >= frames){
                        growth = frames - 1f;
                        if(items.total() < 1) offload(Items.sporePod);
                    }
                    if(growth < 0f) growth = 0f;
                }
            }
            if(timer(timerDump, 15f)) dump(Items.sporePod);
        }

        @Override
        public void draw(){
            float rrot = (tileX() * 89f + tileY() * 13f) % 4f;
            float rrot2 = (tileX() * 69f + tileY() * 42f) % 4f;
            
            if(growth < frames - 0.5f){
                Tile t = Vars.world.tileWorld(x, y);
                
                if(t != null && t.floor() != Blocks.air){
                    Floor f = t.floor();
                    
                    Mathf.rand.setSeed(t.pos());
                    Draw.rect(f.variantRegions()[Mathf.randomSeed(t.pos(), 0, Math.max(0, variantRegions().length - 1))], x, y);
                }
                
                Draw.rect(cageFloor, x, y);
            }

            if(growth != 0f){
                Draw.rect(groundRegions[Mathf.floor(growth)], x, y, rrot * 90f);
                Draw.rect(sporeRegions[Mathf.floor(growth)], x, y, rrot2 * 90f);
            }
            
            Draw.rect(fenceRegions[UnityDrawf.tileMap[tileIndex]], x, y, 8f, 8f);
            drawTeamTop();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            
            write.f(growth);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            growth = read.f();
        }
    }
}
