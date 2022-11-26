package unity.world.blocks;

import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.storage.*;
import unity.mod.*;
import unity.world.*;

public class EndCoreBlock extends CoreBlock{
    public float buildRange = 350f, buildSpeed = 1f;
    public int tileRange = 20;
    protected UnitType buildType;

    public EndCoreBlock(String name){
        super(name);
        timers++;
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, buildRange * Vars.tilesize);
        buildType = new UnitType("builder-unit-" + name){{
            hidden = true;
            internal = true;
            speed = 0f;
            hitSize = 0f;
            health = 1;
            itemCapacity = 0;
            rotateSpeed = 360f;
            buildBeamOffset = 0f;
            buildRange = EndCoreBlock.this.buildRange;
            buildSpeed = EndCoreBlock.this.buildSpeed;
            constructor = BlockUnitUnit::create;
        }};
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        EndBuilders.drawPlace(this, x, y, tileRange, buildRange);
    }

    public class EndCoreBuilding extends CoreBuild implements EndBuilderBuilding{
        EndBuilderModule mod = new EndBuilderModule();
        Unit targetUnit;
        boolean targetActive;
        BlockUnitc buildUnit = (BlockUnitc)buildType.create(team);

        @Override
        public void updateTile(){
            super.updateTile();
            endBuilderUpdate();
            if(!targetActive && targetUnit != null){
                targetUnit = null;
            }
            targetActive = false;
        }

        @Override
        public void draw(){
            super.draw();
            drawConnections();
        }

        @Override
        public EndBuilderModule builderMod(){
            return mod;
        }

        @Override
        public float range(){
            return buildRange;
        }

        @Override
        public int tileRange(){
            return tileRange;
        }

        @Override
        public void updateUnit(Unit u){
            targetActive = true;
            targetUnit = u;
        }

        @Override
        public Unit getTargetUnit(){
            return targetUnit;
        }

        @Override
        public BlockUnitc getUnit(){
            buildUnit.team(team);
            return buildUnit;
        }

        @Override
        public void placed(){
            super.placed();
            placedBuilder();
        }

        @Override
        public void add(){
            if(!isAdded()){
                initBuilder();
            }
            super.add();
        }

        @Override
        public void remove(){
            super.remove();
            removeBuilder();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            mod.write(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            mod.read(read);
        }
    }
}
