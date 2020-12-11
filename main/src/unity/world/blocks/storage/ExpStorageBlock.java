package unity.world.blocks.storage;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import unity.entities.bullet.ExpOrb;
import unity.graphics.UnityPal;
import unity.world.blocks.*;

import static arc.Core.*;

public class ExpStorageBlock extends Block implements ExpBlockBase{
    protected int expCapacity = 600;
    protected float lightRadius = 25f, lightOpacity = 0.6f;
    protected TextureRegion topRegion, baseRegion, expRegion;

    public ExpStorageBlock(String name){
        super(name);
        update = sync = solid = true;
    }

    @Override
    public void load(){
        super.load();
        topRegion = atlas.find(name + "-top");
        baseRegion = atlas.find(name + "-base");
        expRegion = atlas.find(name + "-exp");
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.itemCapacity, "@", bundle.format("explib.expAmount", expCapacity));
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("exp", (ExpStorageBuild build) -> expBar(build));
    }

    @Override
    public int expCapacity(){
        return expCapacity;
    }

    public class ExpStorageBuild extends Building implements ExpBuildBase{
        protected float exp;

        @Override
        public float getMaxExp(){
            return expCapacity;
        }

        @Override
        public float totalExp(){
            return exp;
        }

        @Override
        public void setExp(float a){
            exp = a;
        }

        @Override
        public void draw(){
            Draw.rect(baseRegion, x, y);
            Draw.color(UnityPal.expColor, Color.white, Mathf.absin(Time.time, 20f, 0.6f));
            Draw.alpha(expf());
            Draw.rect(expRegion, x, y);
            Draw.color();
            Draw.rect(topRegion, x, y);
        }

        @Override
        public boolean consumesOrb(){
            return enabled && exp < expCapacity;
        }

        @Override
        public void onDestroyed(){
            ExpOrb.spreadExp(x, y, exp * 0.8f, 3f * size);
            super.onDestroyed();
        }

        @Override
        public void drawLight(){
            Drawf.light(team, this, lightRadius * (1 + expf()), UnityPal.expColor, lightOpacity * expf());
        }

        @Override
        public void write(Writes write){
            super.write(write);
            expWrite(write);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read);
            expRead(read, revision);
        }
    }
}
