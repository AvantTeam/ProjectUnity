package unity.younggamExperimental.blocks;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.world.blocks.production.*;
import unity.graphics.*;
import unity.younggamExperimental.graphs.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.atlas;

//시도용 I hope GenericCrafters that uses graph can be integrated
public class SporePyrolyser extends GenericCrafter implements GraphBlockBase{
    final Graphs graphs = new Graphs();
    TextureRegion heatSprite, bottom;

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

    //not common probably separated?
    @Override
    public Graphs graphs(){
        return graphs;
    }

    @Override
    public void load(){
        super.load();
        heatSprite = atlas.find(name + "-heat");
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

        //not common probably separated?
        @Override
        public float getProgressIncrease(float baseTime){
            float temp = heat().getTemp();
            return Mathf.sqrt(Mathf.clamp((temp - 370f) / 300f)) / baseTime * edelta();
        }

        @Override
        public void draw(){
            float temp = heat().getTemp();
            Draw.rect(region, x, y, 0f);
            UnityDrawf.drawHeat(heatSprite, x, y, 0f, temp * 1.5f);
            drawTeamTop();
        }

        @Override
        public GraphModules gms(){
            return gms;
        }
    }
}
