package unity.world.blocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.world.*;
import unity.graphics.*;
import unity.mod.*;
import unity.world.*;

public class EndConstructorBlock extends Block{
    public float buildRange = 220f, buildSpeed = 0.4f;
    public int tileRange = 16;
    public TextureRegion topRegion;
    protected UnitType buildType;

    public EndConstructorBlock(String name){
        super(name);
        timers++;
        allowDiagonal = false;
        underBullets = true;
        solid = true;
        update = true;
    }

    @Override
    public void init(){
        super.init();
        clipSize = Math.max(clipSize, buildRange * Vars.tilesize);
        EndBuilderModule.maxRange = Math.max(EndBuilderModule.maxRange, tileRange);
        buildType = new UnitType("builder-unit-" + name){{
            hidden = true;
            internal = true;
            speed = 0f;
            hitSize = 0f;
            health = 1;
            itemCapacity = 0;
            rotateSpeed = 360f;
            buildBeamOffset = 0f;
            buildRange = EndConstructorBlock.this.buildRange;
            buildSpeed = EndConstructorBlock.this.buildSpeed;
            constructor = BlockUnitUnit::create;
        }};
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        EndBuilders.drawPlace(this, x, y, tileRange, buildRange);
    }

    @Override
    public void changePlacementPath(Seq<Point2> points, int rotation, boolean diagonalOn){
        if(!diagonalOn){
            Placement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= tileRange);
        }
    }

    public class EndConstructorBuilding extends Building implements EndBuilderBuilding{
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
            float ef = mod.efficiency * potentialEfficiency * (builderValid() ? 1f : 0f);
            if(ef > 0){
                Tmp.c1.set(EndPal.endMid).mul(1f, Mathf.sin(15f, 1f), Mathf.cos(Time.time, 30f, 1f), ef);
                Draw.blend(Blending.additive);
                Draw.color(Tmp.c1);
                Draw.rect(topRegion, x, y);
                Draw.reset();
                Draw.blend();
            }
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
