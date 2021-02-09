package unity.world.blocks.defense.turrets.exp;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.world.blocks.defense.turrets.*;
import unity.entities.comp.*;

public class ExpPowerTurret extends PowerTurret{
    public ExpPowerTurret(String name){
        super(name);
    }

    public class ExpPowerTurretBuild extends PowerTurretBuild implements ExpBuildc{
        public float exp = 0f;

        @Override
        public float exp() {
            return exp;
        }

        @Override
        public void exp(float exp) {
            this.exp = exp;
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
