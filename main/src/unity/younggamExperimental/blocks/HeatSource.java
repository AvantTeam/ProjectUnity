package unity.younggamExperimental.blocks;

import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.younggamExperimental.graphs.*;
import unity.younggamExperimental.modules.*;

import static arc.Core.atlas;

public class HeatSource extends Block implements GraphBlockBase{
    final Graphs graphs = new Graphs();
    TextureRegion heatRegion, baseRegion;//heatSprite,bottom

    public HeatSource(String name){
        super(name);
        update = true;
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

    //not common probably separated?
    @Override
    public void load(){
        super.load();
        heatRegion = atlas.find(name + "-heat");
        baseRegion = atlas.find(name + "-base");
    }

    public class HeatSourceBuild extends Building implements GraphBuildBase{
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

        @Override
        public GraphModules gms(){
            return gms;
        }

        //not common probably separated?
        @Override
        public void updatePost(){
            GraphHeatModule hgraph = heat();
            hgraph.heat = hgraph.heat + Math.max(0, 9999f - hgraph.getTemp()) * 0.5f;
        }

        @Override
        public void draw(){
            float temp = heat().getTemp();
            Draw.rect(baseRegion, x, y, 0f);
            UnityDrawf.drawHeat(heatRegion, x, y, rotdeg(), temp);
            super.drawTeamTop();
        }
    }
}
