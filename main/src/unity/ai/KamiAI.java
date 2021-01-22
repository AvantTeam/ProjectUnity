package unity.ai;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.content.*;

import java.util.*;

public class KamiAI implements UnitController{
    public static int difficulty = 1;
    private static final Vec2 tmpVec = new Vec2();
    private static final KamiShootType[] types = {
    new KamiShootType(kamiAI -> {
        if(kamiAI.reloads[0] > 6){
            for(int i = 0; i < 6 + difficulty; i++){
                float angle = (i * (360f / (6f + difficulty))) + kamiAI.reloads[1];
                UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
            }
            kamiAI.reloads[0] = 0f;
        }
        kamiAI.reloads[1] += Time.delta * 1.875f;
        kamiAI.reloads[0] += Time.delta;
    }){{
        maxTime = 5f * 60f;
    }},
    new KamiShootType(kamiAI -> {
        if(kamiAI.reloads[0] > 3){
            for(int i = 0; i < 16; i++){
                float angle = (i * (360f / 16));
                for(int s : Mathf.signs){
                    Cons<Bullet> data = b -> b.rotation(b.rotation() + (s * 1f));
                    UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle).data(data);
                }
            }
            kamiAI.reloads[0] = 0f;
        }
        kamiAI.reloads[0] += Time.delta;
    }){{
        maxTime = 3f * 60;
    }}
    };

    protected Interval timer = new Interval(1);
    protected Unit unit;
    protected Teamc target;
    protected IntSeq iSeq = new IntSeq();
    protected float[] reloads = new float[16];
    protected float time = 0f;
    protected float waitTime = 40f;
    protected boolean changed = false;
    protected KamiShootType shooterType;
    protected float relativeRotation = 0f;

    @Override
    public void updateUnit(){
        if(timer.get(40f) && target == null && unit != null){
            Unit next = null;
            float dst = 0f;
            for(Unit e : Groups.unit){
                if(e.controller() instanceof Player && (next == null || dst < unit.dst(e))){
                    next = e;
                    target = e;
                    dst = unit.dst(e);
                }
            }
        }
        if(target instanceof Unit e && !(e.controller() instanceof Player)) target = null;
        if(iSeq.isEmpty()){
            for(int i = 0; i < types.length; i++){
                iSeq.add(i);
            }
        }
        if(waitTime >= 40f){
            updateBulletHell();
        }else{
            waitTime += Time.delta;
        }
    }

    public void updateBulletHell(){
        if(target == null || unit == null) return;
        if(!changed){
            tmpVec.trns(relativeRotation, 0, 210).add(target).sub(unit).scl(1 / 20f);
            unit.move(tmpVec.x, tmpVec.y);
            if(tmpVec.trns(relativeRotation, 0, 210).add(target).epsilonEquals(unit.x, unit.y, 8f)){
                int rand = Mathf.random(0, iSeq.size - 1);
                int t = iSeq.get(rand);
                shooterType = types[t];
                iSeq.removeIndex(rand);
                //shooterType = types[Mathf.random(0, types.length - 1)];

                shooterType.init(this);
                changed = true;
            }
        }else{
            shooterType.update(this);
        }
    }

    public void changeType(){
        changed = false;
        waitTime = 0f;
        Arrays.fill(reloads, 0f);
    }

    @Override
    public void unit(Unit unit){
        this.unit = unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    private static class KamiShootType{
        Cons<KamiAI> type;
        Cons<KamiAI> init;
        float maxTime = 0f;

        KamiShootType(Cons<KamiAI> type){
            this.type = type;
            init = null;
        }

        KamiShootType(Cons<KamiAI> type, Cons<KamiAI> init){
            this.type = type;
            this.init = init;
        }

        protected void init(KamiAI ai){
            if(init != null) init.get(ai);
        }

        protected void update(KamiAI ai){
            type.get(ai);
            ai.time += Time.delta;
            if(ai.time >= maxTime){
                ai.time = 0f;
                ai.changeType();
            }
        }
    }
}
