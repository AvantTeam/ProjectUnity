package unity.world.blocks.defense.turrets.exp;

import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;
import unity.entities.comp.*;

public class ExpLiquidTurret extends LiquidTurret{
    public ExpLiquidTurret(String name){
        super(name);
        configurable = true;
        shootCone = 1;
        inaccuracy = 0;
        loopSound = Sounds.none;
        shootSound = Sounds.splash;
    }

    public class ExpLiquidTurrettBuild extends LiquidTurretBuild implements ExpBuildc{
        public float exp = 0f;

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return ammoTypes.get(liquid) != null;
        }

        @Override
        public float exp() {
            return exp;
        }

        @Override
        public void exp(float exp) {
            this.exp = exp;
        }

        @Override
        public boolean consumesOrb(){
            return exp() < expType().maxExp;
        }

        @Override
        public void killed() {
            super.killed();
            ExpBuildc.super.killed();
        }

        @Override
        public void update(){
            ExpBuildc.super.update();
            super.update();
        }

        @Override
        public void write(Writes write){
            super.write(write);
            ExpBuildc.super.write(write);
        }

        @Override
        public void read(Reads read){
            super.read(read);
            ExpBuildc.super.read(read);
        }

        @Override
        public String toString(){
            return getClass().getSimpleName() + "#" + id;
        }
    }
}
