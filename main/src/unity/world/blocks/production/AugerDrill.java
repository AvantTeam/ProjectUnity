package unity.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.world.blocks.production.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.modules.*;

import static arc.Core.*;

public class AugerDrill extends Drill implements GraphBlockBase{
    protected final Graphs graphs = new Graphs();

    public final TextureRegion[] bottomRegions = new TextureRegion[2], topRegions = new TextureRegion[2], oreRegions = new TextureRegion[2];
    public TextureRegion rotorRegion, rotorRotateRegion, mbaseRegion, wormDrive, gearRegion, rotateRegion, overlayRegion;

    public AugerDrill(String name){
        super(name);
        rotate = true;
        hasLiquids = false;
        liquidBoostIntensity = 1f;
    }

    @Override
    public void load(){
        super.load();

        rotorRegion = atlas.find(name + "-rotor");
        rotorRotateRegion = atlas.find(name + "-rotor-rotate");
        mbaseRegion = atlas.find(name + "-mbase");

        gearRegion = atlas.find(name + "-gear");
        overlayRegion = atlas.find(name + "-overlay");
        rotateRegion = atlas.find(name + "-moving");
        wormDrive = atlas.find(name + "-rotate");

        for(int i = 0; i < 2; i++){
            bottomRegions[i] = atlas.find(name + "-bottom" + (i + 1));
            topRegions[i] = atlas.find(name + "-top" + (i + 1));
            oreRegions[i] = atlas.find(name + "-ore" + (i + 1));
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        graphs.setStats(stats);
        setStatsExt(stats);
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{atlas.find(name)};
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

    public class ArguerDrillBuild extends DrillBuild implements GraphBuildBase{
        protected GraphModules gms;

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

        @Override
        public void updatePre(){
            warmup = Math.min(1f, warmup);
        }

        @Override
        public void draw(){
            float rot = torque().getRotation();
            float fixedRot = (rotdeg() + 90f) % 180f - 90f;

            int variant = rotation % 2;

            float deg = rotation == 0 || rotation == 3 ? rot : -rot;
            float shaftRot = rot * 2f;

            Point2 offset = Geometry.d4(rotation + 1);

            Draw.rect(bottomRegions[variant], x, y);

            //target region
            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(oreRegions[variant], x, y);
                Draw.color();
            }

            //bottom rotor
            Draw.rect(rotorRegion, x, y, 360f - rot * 0.25f);

            UnityDrawf.drawRotRect(rotorRotateRegion, x, y, 24f, 3.5f, 3.5f, 450f - rot * 0.25f, shaftRot, shaftRot + 180f);
            UnityDrawf.drawRotRect(rotorRotateRegion, x, y, 24f, 3.5f, 3.5f, 450f - rot * 0.25f, shaftRot + 180f, shaftRot + 360f);

            //shaft
            Draw.rect(mbaseRegion, x, y, fixedRot);

            UnityDrawf.drawRotRect(wormDrive, x, y, 24f, 3.5f, 3.5f, fixedRot, rot, rot + 180f);
            UnityDrawf.drawRotRect(wormDrive, x, y, 24f, 3.5f, 3.5f, fixedRot, rot + 180f, rot + 360f);
            UnityDrawf.drawRotRect(rotateRegion, x, y, 24f, 3.5f, 3.5f, fixedRot, rot, rot + 180f);

            Draw.rect(overlayRegion, x, y, fixedRot);

            //gears
            Draw.rect(gearRegion, x + offset.x * 4f, y + offset.y * 4f, -deg/2);
            Draw.rect(gearRegion, x - offset.x * 4f, y - offset.y * 4f, deg/2);

            Draw.rect(topRegions[variant], x, y);
            drawTeamTop();
        }
    }
}
