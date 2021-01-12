package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.liquid.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.meta.*;
import unity.world.modules.*;

import static arc.Core.atlas;

public class WaterTurbin extends ArmoredConduit implements GraphBlockBase{
    final TextureRegion[] topRegions = new TextureRegion[4], bottomRegions = new TextureRegion[2], liquidRegions = new TextureRegion[2];//topsprite,base,liquidsprite
    TextureRegion rotorRegion;//rotor
    protected final Graphs graphs = new Graphs();
    public float requiredLiquid = 0.001f;

    public WaterTurbin(String name){
        super(name);
        solid = true;
        noUpdateDisabled = false;
    }

    @Override
    public void load(){
        super.load();
        for(int i = 0; i < 4; i++) topRegions[i] = atlas.find(name + "-top" + (i + 1));
        for(int i = 0; i < 2; i++) bottomRegions[i] = atlas.find(name + "-bottom" + (i + 1));
        for(int i = 0; i < 2; i++) liquidRegions[i] = atlas.find(name + "-liquid" + (i + 1));
        rotorRegion = atlas.find(name + "-rotor");
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
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list){
        Draw.alpha(0.5f);
        Draw.rect(region, req.drawx(), req.drawy(), req.rotation * 90f);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    public class WaterTurbinBuild extends ArmoredConduitBuild implements GraphBuildBase{
        protected GraphModules gms;
        float flowRate;

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

        @Override
        public void drawSelect(){
            super.drawSelect();
            gms.drawSelect();
        }

        //
        @Override
        public void updatePre(){
            float flow = flowRate * 40f;
            smoothLiquid = Mathf.lerpDelta(smoothLiquid, liquids.currentAmount() / liquidCapacity, 0.05f);
            if(liquids.total() > 0.001f && timer(timerFlow, 1f)) flowRate = moveLiquidForward(leaks, liquids.current());
            float mul = flow / 100f;
            if(mul > 1f && liquids.total() > requiredLiquid){
                mul = 0.5f * Mathf.log2(mul) + 1f;
            }else{
                mul = 0;
            }
            torque().setMotorForceMult(mul);
        }

        @Override
        public void draw(){
            float rot = torque().getRotation();
            Draw.rect(bottomRegions[rotation % 2], x, y);
            if(liquids.total() > 0.001f) Drawf.liquid(liquidRegions[rotation % 2], x, y, liquids.total() / liquidCapacity, liquids.current().color);
            Drawf.shadow(rotorRegion, x - size / 2f, y - size / 2f, rot);
            Draw.rect(rotorRegion, x, y, rot);
            Draw.rect(topRegions[rotation], x, y);
            drawTeamTop();
        }

        @Override
        public float moveLiquidForward(boolean leaks, Liquid liquid){
            var rPos = GraphData.getConnectSidePos(1, 3, rotation).toPos;
            var next = tile.nearby(rPos);
            if(next == null) return 0f;
            if(next.build != null) return moveLiquid(next.build, liquid);
            return 0f;
        }
    }
}
