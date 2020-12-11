package unity.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.distribution.Conveyor.*;
import mindustry.world.meta.*;
import unity.graphics.*;
import unity.world.blocks.*;

import static arc.Core.bundle;
import static unity.entities.bullet.ExpOrb.*;

public class ExpOutput extends ExpUnloader implements ExpBlockBase{
    protected int expCapacitiy = 100;

    public ExpOutput(String name){
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, "@", bundle.format("explib.expAmount", expCapacitiy));
        stats.add(Stat.output, "@", bundle.format("explib.hubPercent", unloadAmount * 100f));
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("exp", (ExpOutputBuild build) -> expBar(build));
    }

    @Override
    public int expCapacity(){
        return expCapacitiy;
    }

    public class ExpOutputBuild extends ExpUnloaderBuild implements ExpBuildBase{
        protected float exp, warmup;
        protected int conv = -1;

        @Override
        public float getMaxExp(){
            return expCapacitiy;
        }

        @Override
        public float totalExp(){
            return exp;
        }

        @Override
        public void setExp(float a){
            exp = a;
        }

        public boolean isFull(){
            return !enabled || !consValid() || exp >= expCapacitiy;
        }

        public float getPercent(){
            return unloadAmount;
        }

        @Override
        public boolean consumesOrb(){
            return false;
        }

        @Override
        public void write(Writes write){
            super.write(write);
            expWrite(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            expRead(read, revision);
        }

        @Override
        public void expUnloaderOnProximity(){
            conv = -1;
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                if(build instanceof ExpBuildFrame/*TODO*/ && build.interactable(team)){
                    //TODO
                    join[i] = true;
                }else join[i] = false;
                if(build instanceof ConveyorBuild && build.isValid() && build.interactable(team)) conv = i;
            }
        }

        @Override
        protected void expUnloaderDraw(){
            if(!consValid()) return;
            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(Mathf.absin(Time.time, 20f, 0.4f));
            Draw.rect(topRegion, x, y);
            Draw.blend();
            if(warmup > 0.001f){
                Draw.color(UnityPal.expColor);
                Draw.z(Layer.effect);
                Lines.stroke(warmup * Mathf.absin(Time.time, 20f, 1.2f));
                for(int i = 0; i < 4; i++){
                    if(join[i]) Lines.lineAngle(x + 4f * d4x[i], y, 4f * d4y[i], 90f * (i + 1), 8f);
                }
            }
            Draw.reset();
        }

        @Override
        protected void expUnloaderUpdate(){
            warmup = Mathf.lerpDelta(warmup, consValid() ? 1f : 0, 0.05f);
            if(conv != -1 && consValid() && enabled && exp >= 10f && timer.get(0, 20f)){
                spewExp(x, y, 1, conv * 90f, unloadAmount * expAmount);
                incExp(-expAmount);
            }
        }
    }
}
