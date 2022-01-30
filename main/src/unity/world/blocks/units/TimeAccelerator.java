package unity.world.blocks.units;

import arc.scene.ui.layout.Table;
import arc.util.Log;
import arc.util.io.*;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.Ranged;
import mindustry.ui.Styles;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class TimeAccelerator extends Block {
    public float accelTime = 300f;
    public float range = 100f;
    public float reload = 360f;
    public float boost = 3.5f;

    public TimeAccelerator(String name){
        super(name);

        configurable = update = sync = solid = hasPower = hasLiquids = true;
        hasItems = hasLiquids = noUpdateDisabled = rotate = logicConfigurable = false;
        size = 3;
        timers = 2;

        config(Integer.class, (TimeAcceleratorBuild entity, Integer value) -> {
            entity.setTarget();
        });
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
    }

    public class TimeAcceleratorBuild extends Building implements Ranged{
        public Teamc boostTarget;
        public int first = 1;
        public boolean isBoosted = false;

        @Override
        public float range(){
            return range;
        }

        @Override
        public void created(){
            super.created();

            boostTarget = setTarget();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            /* closest unit or building */
            if (boostTarget == null || !boostTarget.within(this, range) || targetDead(boostTarget)) setTarget();

            if (boostTarget != null){
                if (timer(0, reload) || isBoosted){
                    isBoosted = true;
                    timer.reset(0,0);
                }else{
                    timer.reset(1, 0);
                }

                if (isBoosted){
                    if (timer(1, accelTime)){
                        isBoosted = false;
                        resetBoost(boostTarget);
                    }else{
                        if (boostTarget instanceof Unit) ((Unit) boostTarget).speedMultiplier = boost;
                        else if (boostTarget instanceof Building) ((Building) boostTarget).applyBoost(boost, 2f);
                    }
                }
            }else{
                timer.reset(0,0);
                timer.reset(1,0);
            }
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.refresh, Styles.cleari, 40f, () -> configure(0))
                    .size(60f).disabled(b -> isBoosted);
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(first);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            first = read.i();
        }

        public void resetBoost(Teamc e){
            if (e instanceof Unit) ((Unit) e).speedMultiplier = 1f;
        }

        public boolean targetDead(Teamc e){
            if (e instanceof Unit){
                return ((Unit) e).dead();
            } else if (e instanceof Building){
                return ((Building) e).dead();
            }else{
                return false;
            }
        }

        public Teamc setTarget(){
            return Units.bestTarget(null, x, y, range, u -> !u.dead() && u.speedMultiplier < 3.5f && !u.isPlayer(), b -> !b.proximity.contains(this) && b != this && !b.dead() && b.block.canOverdrive, (e, x, y) -> e.dst(this));
        }
    }
}