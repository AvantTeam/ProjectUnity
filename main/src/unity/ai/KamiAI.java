package unity.ai;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.content.*;
import unity.type.*;

import java.util.*;

public class KamiAI implements UnitController{
    public static int difficulty = 1;
    private static final Vec2 tmpVec = new Vec2();
    private static final Vec2 tmpVec2 = new Vec2();
    private static final KamiShootType[] types = {
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[0] > 6){
                for(int i = 0; i < 6 + difficulty; i++){
                    float angle = (i * (360f / (6f + difficulty))) + kamiAI.reloads[1];
                    tmpVec2.trns(angle, 20f).add(kamiAI.unit);
                    UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec2.x, tmpVec2.y, angle).vel.scl(0.8f);
                }
                kamiAI.reloads[0] = 0f;
            }
            kamiAI.reloads[1] += Time.delta * 1.875f;
            kamiAI.reloads[0] += Time.delta;
        }, 5f * 60f),
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[0] > 3){
                for(int i = 0; i < 16; i++){
                    float angle = (i * (360f / 16));
                    tmpVec2.trns(angle, 20f).add(kamiAI.unit);
                    for(int s : Mathf.signs){
                        Cons<Bullet> data = b -> b.rotation(b.rotation() + (s * Time.delta));
                        UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec2.x, tmpVec2.y, angle).data(data);
                    }
                }
                kamiAI.reloads[0] = 0f;
            }
            kamiAI.reloads[0] += Time.delta;
        }, 3f * 60f),
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[1] < 2f){
                for(int i = 0; i < 6; i++){
                    float angle = (i * (360f / 6));
                    Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                    bullet.hitSize = 100f;
                    bullet.vel.scl(0.3f);
                    bullet.lifetime *= 1 / 0.3f;
                }
                kamiAI.reloads[1] = 3f;
            }
            if(kamiAI.reloads[0] >= 2f){
                for(int i = 0; i < 3; i++){
                    float angle = (i * (360f / 3)) + (kamiAI.reloads[2] * kamiAI.reloads[3] * 5f);
                    for(int j = 0; j < 3; j++){
                        Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle + Mathf.range(1f));
                        bullet.vel.scl(Mathf.random(0.8f, 1.2f) * 0.7f);
                        bullet.lifetime *= 1 / 0.7f;
                    }
                }
                if(kamiAI.reloads[2] >= 60f * 4.5f){
                    kamiAI.reloads[1] = 0;
                    kamiAI.reloads[0] = -80f;
                    kamiAI.reloads[3] *= -1f;
                    kamiAI.reloads[2] = 0f;
                }else{
                    kamiAI.reloads[0] = 0f;
                }
            }
            kamiAI.reloads[2] += Time.delta;
            kamiAI.reloads[0] += Time.delta;
        }, kamiAI -> {
            kamiAI.reloads[0] = -80f;
            kamiAI.reloads[3] = 1f;
        }, 15 * 60f),
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[0] >= 2.5f * 60f){
                kamiAI.reloads[6] += Time.delta;
                kamiAI.reloads[1] += Time.delta;
                kamiAI.reloads[8] += Time.delta;
                kamiAI.reloads[9] += Time.delta;
                if(kamiAI.reloads[9] >= 20 && kamiAI.tmpBullets[0] != null){
                    int diff = 4 + difficulty;
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + (kamiAI.reloads[8] * kamiAI.reloads[7] * -2f);
                        tmpVec.trns(angle, 30f).add(kamiAI.unit);
                        for(int j = 0; j < 2; j++){
                            Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec.x, tmpVec.y, angle);
                            bullet.hitSize = 15f;
                            bullet.vel.scl(j >= 1 ? 0.75f : 0.33f);
                            bullet.lifetime *= 1f / (j >= 1 ? 0.75f : 0.33f);
                        }
                    }
                    kamiAI.reloads[9] = 0f;
                }
                if(kamiAI.reloads[6] >= 6 && kamiAI.tmpBullets[0] != null){
                    Cons<Bullet> data = b -> {
                        if(b.time < 1f * 50){
                            b.vel.setLength(Math.max((1f - (b.time / (1f * 50))) * b.type.speed, 0.02f));
                        }else if(b.time >= 2.5f * 60){
                            b.vel.setLength(Math.min(Math.max((b.time - (2.5f * 60)) / 80f, 0.02f), b.type.speed));
                        }
                    };
                    int diff = 7 + difficulty;
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + (kamiAI.reloads[8] * kamiAI.reloads[7] * 4f);
                        Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                        bullet.hitSize = 8f;
                        bullet.lifetime = 7f * 60f;
                        bullet.data = data;

                    }
                    kamiAI.reloads[6] = 0f;
                }
                if(kamiAI.reloads[2] != 1){
                    tmpVec.trns(kamiAI.reloads[3], 670f).add(kamiAI.unit);
                    UnityFx.kamiWarningLine.at(kamiAI.unit.x, kamiAI.unit.y, 0f, new Position[]{new Vec2(kamiAI.unit.x, kamiAI.unit.y), new Vec2(tmpVec)});
                    kamiAI.reloads[2] = 1;
                }
                if(kamiAI.reloads[1] >= 100){
                    if(kamiAI.reloads[4] != 1){
                        tmpVec.trns(kamiAI.reloads[3], 80f).add(kamiAI.unit);
                        kamiAI.tmpBullets[0] = UnityBullets.kamiLaser.create(kamiAI.unit, tmpVec.x, tmpVec.y, kamiAI.reloads[4]);
                        kamiAI.reloads[4] = 1f;
                    }
                    if(kamiAI.tmpBullets[0] != null){
                        kamiAI.reloads[3] = Angles.moveToward(kamiAI.reloads[3], kamiAI.unit.angleTo(kamiAI.target), 0.2f * Time.delta);
                        tmpVec.trns(kamiAI.reloads[3], 80f).add(kamiAI.unit);
                        kamiAI.tmpBullets[0].rotation(kamiAI.reloads[3]);
                        kamiAI.tmpBullets[0].set(tmpVec);
                    }
                    kamiAI.reloads[5] += Time.delta;
                    if(kamiAI.reloads[5] >= 4.2f * 60f){
                        kamiAI.tmpBullets[0] = null;
                        for(int i = 0; i < 6; i++) kamiAI.reloads[i] = 0f;
                        kamiAI.reloads[8] = 0f;
                        kamiAI.reloads[9] = 0f;
                        kamiAI.reloads[7] *= -1f;
                    }
                }
            }else if(kamiAI.target != null){
                tmpVec.trns(kamiAI.relativeRotation, 0, 210).add(kamiAI.target).sub(kamiAI.unit).scl(1 / 20f);
                kamiAI.reloads[3] = kamiAI.unit.angleTo(kamiAI.target);
                kamiAI.unit.move(tmpVec.x, tmpVec.y);
                kamiAI.reloads[0] += Time.delta;
            }
        }, kamiAI -> {
            kamiAI.reloads[0] = 1.50f * 60f;
            kamiAI.reloads[7] = 1f;
        }, 15 * 60f),
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[1] >= 12f){
                int diff = 7 + difficulty;
                for(int i = 0; i < diff; i++){
                    float angle = (i * (360f / diff)) + Mathf.sin(kamiAI.reloads[2], 120f, 30f) + kamiAI.relativeRotation;
                    Cons<Bullet> data1 = b -> {
                        if(b.time > 1.1f * 17f){
                            float spacing = 20f;
                            for(int j = 0; j < 5; j++){
                                float angleB = (j * spacing - (5 - 1) * spacing / 2f);
                                Cons<Bullet> data2 = a -> {
                                    if(a.time >= 1.1f * 17){
                                        a.vel.scl(0.9f);
                                        a.rotation(a.rotation() + (angleB / 2f));
                                        a.data = null;
                                    }
                                };
                                Bullet c = UnityBullets.kamiBullet1.create((Teamc)b.owner, b.x, b.y, b.rotation() + angleB);
                                c.vel.scl(0.7f);
                                c.data = data2;
                            }
                            b.time = b.lifetime + 1f;
                        }
                    };
                    Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                    b.data = data1;
                    b.vel.scl(0.8f);
                }
                kamiAI.reloads[1] = 0f;
            }
            if(kamiAI.reloads[0] >= 4f * 60){
                kamiAI.reloads[2] += Time.delta;
                if(kamiAI.reloads[3] >= 7f){
                    kamiAI.reloads[3] = 0f;
                    for(int i = 0; i < 24 + difficulty; i++){
                        float angle = (i * (360f / (24 + difficulty))) + (kamiAI.reloads[4] * 9f * kamiAI.reloads[5]);
                        Cons<Bullet> data = b -> {
                            if((b.time - 40f) < 1.2f * 17){
                                b.vel.setLength(Math.max((1f - Mathf.clamp((b.time - 40f) / (1.2f * 17))) * b.type.speed, 0.02f));
                            }else if((b.time - b.fdata) >= 2f * 60){
                                b.vel.setLength(Math.max(Mathf.clamp(((b.time - b.fdata) - (1.75f * 60)) / 30f) * b.type.speed, 0.02f));
                            }
                        };
                        Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                        b.time = (kamiAI.reloads[4] * 3.333f);
                        b.data = data;
                        b.fdata = (kamiAI.reloads[4] * 6.666f);
                    }
                    kamiAI.reloads[4] += 1f;
                }
                if(kamiAI.reloads[4] >= 6){
                    kamiAI.reloads[3] = -(4f * 60);
                    kamiAI.reloads[4] = 0f;
                    kamiAI.reloads[5] *= -1f;
                }
                kamiAI.reloads[3] += Time.delta;
            }
            kamiAI.reloads[0] += Time.delta;
            kamiAI.reloads[1] += Time.delta;
        }, kamiAI -> kamiAI.reloads[5] = 1f, 20 * 60f)
    };

    protected Interval timer = new Interval(2);
    protected Unit unit;
    protected Teamc target;
    protected IntSeq iSeq = new IntSeq();
    protected float[] reloads = new float[16];
    protected Bullet[] tmpBullets = new Bullet[8];
    protected float time = 0f;
    protected float waitTime = 40f;
    protected float moveTime = 0f;
    protected float autoShootTime = 0f;
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
        if(timer.get(1, 10f) && unit.deltaLen() >= 0.25f){
            UnityFx.rainbowTextureTrail.at(unit.x, unit.y, unit.rotation, ((RainbowUnitType)unit.type).trailRegion);
        }
        if(waitTime >= 40f){
            updateBulletHell();
        }else{
            waitTime += Time.delta;
        }
    }

    public void updateBulletHell(){
        if(target == null || unit == null) return;
        unit.lookAt(relativeRotation - 90f);
        if(!unit.within(target, 650f)) moveTime = 150f;
        if(moveTime > 0.001f){
            tmpVec.trns(relativeRotation, 0, 210).add(target).sub(unit).scl(1 / 20f);
            unit.move(tmpVec.x, tmpVec.y);
            moveTime -= Time.delta;
        }
        if(!changed){
            tmpVec.trns(relativeRotation, 0, 210).add(target).sub(unit).scl(1 / 20f);
            unit.move(tmpVec.x, tmpVec.y);
            autoShootTime += Time.delta;
            if(tmpVec.trns(relativeRotation, 0, 210).add(target).epsilonEquals(unit.x, unit.y, 12f) || autoShootTime >= 90f){
                int rand = Mathf.random(0, iSeq.size - 1);
                int t = iSeq.get(rand);
                shooterType = types[t];
                iSeq.removeIndex(rand);
                //shooterType = types[Mathf.random(0, types.length - 1)];
                
                shooterType.init(this);
                autoShootTime = 0f;
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
        Arrays.fill(tmpBullets, null);
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
        float maxTime;

        KamiShootType(Cons<KamiAI> type, float time){
            this.type = type;
            maxTime = time;
            init = null;
        }

        KamiShootType(Cons<KamiAI> type, Cons<KamiAI> init, float time){
            this.type = type;
            maxTime = time;
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
                ai.relativeRotation += Mathf.range(180f);
                if(ai.relativeRotation < 0f) ai.relativeRotation += 360f;
                ai.changeType();
            }
        }
    }
}
