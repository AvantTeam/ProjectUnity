package unity.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.world.blocks.production.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.modules.*;

import static arc.Core.*;

public class SporePyrolyser extends GenericCrafter implements GraphBlockBase{
    final Graphs graphs = new Graphs();
    TextureRegion heatRegion;

    public SporePyrolyser(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        
        graphs.setStats(stats);
        setStatsExt(stats);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        graphs.drawPlace(x, y, size, rotation, valid);
        
        super.drawPlace(x, y, rotation, valid);
    }

    @Override
    public Graphs graphs(){
        return graphs;
    }

    @Override
    public void load(){
        super.load();
        
        heatRegion = atlas.find(name + "-heat");
    }

    public class SporPyrolyserBuild extends GenericCrafterBuild implements GraphBuildBase{
        GraphModules gms;

        @Override
        public void created(){
            gms = new GraphModules(this);
            graphs.injectGraphConnector(gms);
            gms.created();
        }

        @Override
        public float efficiency(){
            return super.efficiency() * gms.efficiency();
        }

        @Override
        public void onRemoved(){
            gms.updateGraphRemovals();
            onDelete();
            
            super.onRemoved();
            onDeletePost();
        }

        @Override
        public void updateTile(){
            if(graphs.useOriginalUpdate()) super.updateTile();
            
            updatePre();
            gms.updateTile();
            
            updatePost();
            gms.prevTileRotation(rotation);
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            
            gms.onProximityUpdate();
            proxUpdate();
        }

        @Override
        public void display(Table table){
            super.display(table);
            
            gms.display(table);
            displayExt(table);
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            
            gms.displayBars(table);
            displayBarsExt(table);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            
            gms.drawSelect();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            
            gms.write(write);
            writeExt(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            
            gms.read(read, revision);
            readExt(read, revision);
        }

        @Override
        public GraphModules gms(){
            return gms;
        }

        @Override
        public float getProgressIncrease(float baseTime){
            return Mathf.sqrt(Mathf.clamp((heat().getTemp() - 370f) / 300f)) / baseTime * edelta();
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            UnityDrawf.drawHeat(heatRegion, x, y, 0f, heat().getTemp() * 1.5f);
            
            drawTeamTop();
        }
    }
}
