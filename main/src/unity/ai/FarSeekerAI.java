package unity.ai;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class FarSeekerAI implements UnitController{
    protected Unit unit;
    protected Teamc target;

    protected int phase;
    protected int subphase;
    protected float phaseTime;
    protected static final float[][] phases = {
        {10f * Time.toSeconds, 14f * Time.toSeconds, 12f * Time.toSeconds, 5f * Time.toSeconds},
        {8f * Time.toSeconds, 10f * Time.toSeconds, 5f * Time.toSeconds, 5f * Time.toSeconds},
        {16f * Time.toSeconds, 6f * Time.toSeconds, 1f * Time.toSeconds}
    };
    protected static final float[] phaseThreshold = {1f, 0.7f, 0.3f};

    protected boolean waiting;
    protected float waitTime;
    protected static final float[] waitTimes = {0.5f * Time.toSeconds, 1f * Time.toSeconds, 2.5f * Time.toSeconds};
    protected final boolean[] waited = {false, false, false};

    protected static FarSeekerMove[][] movements;

    protected float scale;

    protected static int timers = 0;

    protected static final int timerTarget = timers++;
    protected static final float targetInterval = 0.5f * Time.toSeconds;

    protected static final int timerSwitchDir = timers++;
    protected static final float switchDirInterval = 1f * Time.toSeconds;
    protected int direction;

    protected float initRotation;

    protected static final float rangeThreshold = 25f * tilesize;

    protected static final int timerCharge = timers++;
    protected static final float chargeInterval = 0.98f * Time.toSeconds;
    protected static final float chargeRange = (25f * tilesize) / 60f;
    protected float chargeTime;
    protected Vec2 chargeTarget = new Vec2();

    protected Interval timer = new Interval(timers);

    static{
        movements = new FarSeekerMove[phases.length][];
        for(int i = 0; i < phases.length; i++){
            movements[i] = new FarSeekerMove[phases[i].length];
        }

        FarSeekerMove aggressiveFollow = new FarSeekerMove(ai -> {
            if(ai.timer.get(timerSwitchDir, switchDirInterval * ai.scale(0.5f, 1.2f))){
                ai.direction += Mathf.sign(Mathf.randomBoolean());
            }

            Tmp.v1
                .trns(ai.direction * 90f, (20f * tilesize) * (0.6f + ai.phaseTime(true) * 0.4f) * ai.scale(0.9f, 1f))
                .rotate(Angles.angleDist(ai.unit.rotation, ai.unit.angleTo(ai.target)) + (ai.phaseTime / 3f) * ai.scale(1f, 1.2f, true))
                .rotate(Mathf.sin(Time.time, 4f + ai.phaseTime() * 3f, 24f * ai.phaseTime(true) + 4f));

            Tmp.v2.set(Tmp.v1).add(ai.target).sub(ai.unit);

            ai.unit.moveAt(Tmp.v2, ai.unit.type.accel * (1f + ai.scale(0f, 0.5f, true)));
            ai.unit.lookAt(ai.target);
        }).init(ai -> {
            ai.timer.reset(timerSwitchDir, 0f);
        });

        FarSeekerMove passiveFollow = new FarSeekerMove(ai -> {
            Tmp.v1.trns(ai.initRotation + Angles.angleDist(ai.unit.rotation, ai.unit.angleTo(ai.target)) + ai.phaseTime / 3f, (20f * tilesize) * ai.scale(1f, 1.2f, true));
            Tmp.v2.set(Tmp.v1).add(ai.target).sub(ai.unit);

            ai.unit.moveAt(Tmp.v2, ai.unit.type.accel * (0.7f + ai.scale(0f, 0.3f, true)));
            ai.unit.lookAt(ai.target);
        }).init(ai -> {
            ai.initRotation = ai.unit.angleTo(ai.target);
        });

        FarSeekerMove maintainRange = new FarSeekerMove(ai -> {
            float range = rangeThreshold - ai.scale(0f, 10f) * tilesize;
            if(ai.unit.dst(ai.target) < range){
                Tmp.v1.trns(ai.target.angleTo(ai.unit), range * 1.2f);
                Tmp.v2.set(Tmp.v1).add(ai.target).sub(ai.unit);

                ai.unit.moveAt(Tmp.v2);
            }

            ai.unit.lookAt(ai.target);
        });

        FarSeekerMove normalCharge = new FarSeekerMove(ai -> {
            float interval = chargeInterval - ai.scale(0f, 24f, true);
            if(ai.timer.get(timerCharge, interval)){
                ai.chargeTarget.trns(ai.unit.rotation, chargeRange + ai.scale(0f, 40f, true));
                ai.chargeTime = 0f;
            }

            if(ai.chargeTime < interval * 0.5f){
                ai.chargeTime += Time.delta;
                ai.chargeTarget.setLength2(ai.chargeTarget.len2() * (0.5f + (1f - (ai.chargeTime / (interval * 0.5f))) * 0.5f));
                ai.unit.vel.add(ai.chargeTarget);
            }else{
                ai.unit.lookAt(ai.target);
            }
        }).init(ai -> {
            ai.timer.reset(timerCharge, 0f);
        });

        FarSeekerMove aggressiveCharge = new FarSeekerMove(ai -> {
            float interval = (chargeInterval - ai.scale(0f, 24f, true)) / 2f;
            if(ai.timer.get(timerCharge, interval)){
                ai.chargeTarget.trns(ai.unit.rotation, (chargeRange * 0.7f) + ai.scale(1f, 24f, true));
                ai.chargeTime = 0f;
            }

            if(ai.chargeTime < interval * 0.3f){
                ai.chargeTime += Time.delta;
                ai.chargeTarget.setLength2(ai.chargeTarget.len2() * (0.6f + (1f - (ai.chargeTime / (interval * 0.3f))) * 0.4f));
                ai.unit.vel.add(ai.chargeTarget);
            }else{
                ai.unit.moveAt(Tmp.v1.set(ai.target).sub(ai.unit), ai.unit.type.accel * 0.7f);
                ai.unit.lookAt(ai.target);
            }
        }).init(ai -> {
            ai.timer.reset(timerCharge, 0f);
        });

        FarSeekerMove bulletSwarm = new FarSeekerMove(ai -> {
            
        });

        movements[0][0] = aggressiveFollow;
        movements[0][1] = passiveFollow;
        movements[0][2] = maintainRange;
        movements[0][3] = normalCharge;

        movements[1][0] = aggressiveFollow;
        movements[1][1] = maintainRange;
        movements[1][2] = normalCharge;
        movements[1][3] = aggressiveCharge;

        movements[2][0] = normalCharge;
        movements[2][1] = aggressiveCharge;
        movements[2][2] = bulletSwarm;
    }

    @Override
    public void updateUnit(){
        updatePhase();
    }

    protected void updatePhase(){
        if(!waiting){
            if(timer.get(timerTarget, targetInterval)){
                findTarget();
            }

            if(target == null || !target.isAdded()) return;

            for(int i = 0; i < phaseThreshold.length; i++){
                if(unit.health / unit.maxHealth <= phaseThreshold[i]){
                    phase = i;
                    if(!waited[i]){
                        waited[i] = true;

                        waiting = true;
                        waitTime = 0f;

                        return;
                    }
                }
            }

            phaseTime += Time.delta;
            if(phaseTime >= phases[phase][subphase]){
                phaseTime = 0f;
                subphase++;
            }

            if(subphase >= phases[phase].length){
                subphase %= phases[phase].length;
            }

            scale = unit.health / unit.maxHealth;
            updateMovement();
        }else{
            waitTime += Time.delta;
            if(waitTime >= waitTimes[phase]){
                waiting = false;

                subphase = 0;
                phaseTime = 0f;
            }
        }
    }

    protected void updateMovement(){
        movements[phase][subphase].get(this);
    }

    protected void findTarget(){
        Teamc next = Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, Unit::isPlayer);
        if(next == null){
            next = Units.closestEnemy(unit.team, unit.x, unit.y, Float.MAX_VALUE, Unit::isValid);
        }
        if(next == null){
            next = indexer.findEnemyTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, Building::isValid);
        }

        target = next;
    }

    protected float scale(float from, float to){
        return scale(from, to, false);
    }

    protected float scale(float from, float to, boolean inv){
        return from + (Mathf.num(inv) - scale) * (from - to);
    }

    protected float phaseTime(){
        return phaseTime(false);
    }

    protected float phaseTime(boolean inv){
        return Mathf.num(inv) - (phaseTime / phases[phase][subphase]);
    }

    @Override
    public void unit(Unit unit){
        if(this.unit == unit) return;
        this.unit = unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    protected static class FarSeekerMove implements Cons<FarSeekerAI>{
        protected Cons<FarSeekerAI> update = ai -> {};
        protected Cons<FarSeekerAI> init = ai -> {};

        protected FarSeekerMove(Cons<FarSeekerAI> update){
            this.update = update;
        }

        protected FarSeekerMove init(Cons<FarSeekerAI> init){
            this.init = init;
            return this;
        }

        @Override
        public void get(FarSeekerAI ai){
            update.get(ai);
        }
    }
}
