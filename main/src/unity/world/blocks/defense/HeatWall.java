package unity.world.blocks.defense;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.modules.*;

import static arc.Core.atlas;

//youngchaWalls
public class HeatWall extends Block implements GraphBlockBase{
    protected float minStatusRadius = 4f, statusRadiusMul = 20f,
        minStatusDuration = 3f, statusDurationMul = 40f,
        statusTime = 60f, maxDamage;
    final Graphs graphs = new Graphs();
    TextureRegion heatRegion;//heatSprite

    public HeatWall(String name){
        super(name);
        update = solid = true;
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
    }

    public class HeatWallBuild extends Building implements GraphBuildBase{
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

        //not common probably separated?
        @Override
        public void updatePost(){
            if(timer(timerDump, statusTime)){
                float intensity = Mathf.clamp(Mathf.map(heat().getTemp(), 400f, 1000f, 0f, 1f));
                Damage.status(team, x, y, intensity * statusRadiusMul + minStatusRadius, StatusEffects.burning, minStatusDuration + intensity * statusDurationMul, false, true);
                if(maxDamage > 0f) Damage.damage(team, x, y, intensity * 10f + 8f, intensity * maxDamage, false, true);
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            UnityDrawf.drawHeat(heatRegion, x, y, 0f, heat().getTemp());
            drawTeamTop();
        }
    }
}
