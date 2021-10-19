package unity.world.blocks.power;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.power.*;
import unity.graphics.*;
import unity.world.blocks.*;
import unity.world.graphs.*;
import unity.world.modules.*;

import static arc.Core.*;

public class RotorBlock extends PowerGenerator implements GraphBlockBase{
    protected final Graphs graphs = new Graphs();
    protected float baseTopSpeed = 20f, baseTorque = 5f, torqueEfficiency = 1f, fluxEfficiency = 1f, rotPowerEfficiency = 1f;
    protected boolean big;

    public final TextureRegion[] topRegions = new TextureRegion[4];
    public TextureRegion overlayRegion, rotorRegion, bottomRegion, topRegion, overRegion, spinRegion;

    public RotorBlock(String name){
        super(name);

        rotate = consumesPower = outputsPower = true;
    }

    @Override
    public void load(){
        super.load();

        if(big){
            for(int i = 0; i < 4; i++) topRegions[i] = atlas.find(name + "-top" + (i + 1));

            overlayRegion = atlas.find(name + "-overlay");
            rotorRegion = atlas.find(name + "-rotor");
            bottomRegion = atlas.find(name + "-bottom");
        }else{
            topRegion = atlas.find(name + "-top");
            overRegion = atlas.find(name + "-over");
            spinRegion = atlas.find(name + "-spin");
        }
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

    public class RotorBuild extends GeneratorBuild implements GraphBuildBase{
        protected GraphModules gms;
        float topSpeed;

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
        public void displayBarsExt(Table table){
            GraphTorqueModule<?> tGraph = torque();
            float mTorque = flux().getNetwork().flux() * torqueEfficiency * baseTorque;

            table.add(new Bar(
                () -> bundle.format("bar.poweroutput", Strings.fixed((getPowerProduction() - consumes.getPower().usage) * 60f * timeScale, 1)),
                () -> Pal.powerBar,
                () -> productionEfficiency
            )).growX().row();
            table.add(new Bar(
                () -> bundle.get("stat.unity.torque") + ": " + Strings.fixed(tGraph.force, 1) + "/" + Strings.fixed(mTorque, 1),
                () -> Pal.darkishGray,
                () -> tGraph.force / mTorque
            )).growX().row();
            table.add(new Bar(
                () -> bundle.format("stat.unity.maxspeed") + ":" + Strings.fixed(topSpeed / 6f, 1) + "r/s",
                () -> Pal.darkishGray,
                () -> topSpeed / baseTopSpeed
            )).growX().row();
        }

        @Override
        public void updatePre(){
            float flux = flux().getNetwork().flux();
            topSpeed = baseTopSpeed / (1f + flux / fluxEfficiency);
            float breakEven = consumes.getPower().usage / powerProduction;

            GraphTorqueModule<?> tGraph = torque();
            float rotNeg = Mathf.clamp(tGraph.getNetwork().lastVelocity / topSpeed, 0f, 2f / breakEven);

            productionEfficiency = Mathf.clamp(rotNeg * breakEven, 0f, 2f);
            productionEfficiency *= rotPowerEfficiency;

            tGraph.force = flux * baseTorque * (efficiency() - rotNeg) * delta();
        }

        @Override
        public void draw(){
            float fixedRot = (rotdeg() + 90f) % 180f - 90f;
            float shaftRot = (rotation + 1) % 4 >= 2 ? 360f - torque().getRotation() : torque().getRotation();

            if(big){
                Draw.rect(bottomRegion, x, y, fixedRot);

                UnityDrawf.drawRotRect(rotorRegion, x, y, 24f, 15f, 24f, rotdeg(), shaftRot, shaftRot + 90f);
                UnityDrawf.drawRotRect(rotorRegion, x, y, 24f, 15f, 24f, rotdeg(), shaftRot + 120f, shaftRot + 210f);
                UnityDrawf.drawRotRect(rotorRegion, x, y, 24f, 15f, 24f, rotdeg(), shaftRot + 240f, shaftRot + 330f);

                Draw.rect(overlayRegion, x, y, fixedRot);
                Draw.rect(topRegions[rotation], x, y);
            }else{
                UnityDrawf.drawRotRect(spinRegion, x, y, 8f, 3.5f, 8f, rotdeg(), shaftRot, shaftRot + 180f);
                UnityDrawf.drawRotRect(spinRegion, x, y, 8f, 3.5f, 8f, rotdeg(), shaftRot + 180f, shaftRot + 360f);

                Draw.rect(overRegion, x, y, fixedRot);
                Draw.rect(topRegion, x, y, fixedRot);
            }

            drawTeamTop();
        }
    }
}
