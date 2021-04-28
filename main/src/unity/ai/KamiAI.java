package unity.ai;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import unity.content.*;
import unity.gen.*;
import unity.sync.*;
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
                    //UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec2.x, tmpVec2.y, angle).vel.scl(0.8f);
                    UnityCall.createKamiBullet(
                        kamiAI.unit, UnityBullets.kamiBullet1,
                        tmpVec2.x, tmpVec2.y, angle,
                        0.8f, 1f, -1f,
                        -1, 0f, 0f
                    );
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

                    for(int j = 0; j < 2; j++){
                        //Cons<Bullet> data = b -> b.rotation(b.rotation() + (s * Time.delta));
                        //UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec2.x, tmpVec2.y, angle).data(data);
                        UnityCall.createKamiBullet(
                            kamiAI.unit, UnityBullets.kamiBullet1,
                            tmpVec2.x, tmpVec2.y, angle,
                            1f, 1f, -1f,
                            j, 0f, 0f
                        );
                    }
                }
                kamiAI.reloads[0] = 0f;
            }
            kamiAI.reloads[0] += Time.delta;
        }, 3f * 60f),
        //Atomic Fire "Nuclear Fusion"
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[1] < 2f){
                float randRot = Mathf.random(360f);
                for(int i = 0; i < 6; i++){
                    float angle = (i * (360f / 6));
                    //Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                    //bullet.hitSize = 100f;
                    //bullet.vel.scl(0.3f);
                    //bullet.lifetime *= 1 / 0.3f;
                    UnityCall.createKamiBullet(
                        kamiAI.unit, UnityBullets.kamiBullet1,
                        kamiAI.unit.x, kamiAI.unit.y, angle,
                        0.3f + Mathf.range(0.1f), 1f / 0.3f, 100f,
                        -1, 0f, 0f
                    );
                }
                kamiAI.reloads[1] = 3f;
            }
            if(kamiAI.reloads[0] >= 2f){
                for(int i = 0; i < 3; i++){
                    float angle = (i * (360f / 3)) + (kamiAI.reloads[2] * kamiAI.kami().laserRotation * 5f);
                    for(int j = 0; j < 3; j++){
                        //Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle + Mathf.range(1f));
                        //bullet.vel.scl(Mathf.random(0.8f, 1.2f) * 0.7f);
                        //bullet.lifetime *= 1 / 0.7f;
                        UnityCall.createKamiBullet(
                            kamiAI.unit, UnityBullets.kamiBullet1,
                            kamiAI.unit.x, kamiAI.unit.y, angle + Mathf.randomSeedRange(kamiAI.unit.id + (long)Time.time, 1f),
                            Mathf.randomSeed(kamiAI.unit.id + (long)(Time.time + 1f), 0.8f, 1.2f) * 0.7f, 1 / 0.7f, -1f,
                            -1, 0f, 0f
                        );
                    }
                }
                if(kamiAI.reloads[2] >= 60f * 4.5f){
                    kamiAI.reloads[1] = 0;
                    kamiAI.reloads[0] = -80f;
                    kamiAI.kami().laserRotation *= -1f;
                    kamiAI.reloads[2] = 0f;
                }else{
                    kamiAI.reloads[0] = 0f;
                }
            }
            kamiAI.reloads[2] += Time.delta;
            kamiAI.reloads[0] += Time.delta;
        }, kamiAI -> {
            kamiAI.reloads[0] = -80f;
            kamiAI.kami().laserRotation = 1f;
        }, 15 * 60f),
        //Magicannon "Final Spark"/Love Sign "Master Spark" mix
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[0] >= 2.5f * 60f){
                kamiAI.reloads[6] += Time.delta;
                kamiAI.reloads[1] += Time.delta;
                kamiAI.reloads[9] += Time.delta;
                if(kamiAI.reloads[9] >= 20 && kamiAI.kami().laser != null){
                    int diff = 4 + difficulty;
                    kamiAI.reloads[8] -= 17f;
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + (kamiAI.reloads[8] * kamiAI.reloads[7]);
                        tmpVec.trns(angle, 30f).add(kamiAI.unit);
                        for(int j = 0; j < 2; j++){
                            //Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, tmpVec.x, tmpVec.y, angle);
                            //bullet.hitSize = 15f;
                            //bullet.vel.scl(j >= 1 ? 0.75f : 0.33f);
                            //bullet.lifetime *= 1f / (j >= 1 ? 0.75f : 0.33f);
                            UnityCall.createKamiBullet(
                                kamiAI.unit, UnityBullets.kamiBullet1,
                                tmpVec.x, tmpVec.y, angle,
                                j >= 1 ? 0.75f : 0.33f, 1f / (j >= 1 ? 0.75f : 0.33f), 15f,
                                -1, 0f, 0f
                            );
                        }
                    }
                    kamiAI.reloads[9] = 0f;
                }
                if(kamiAI.reloads[6] >= 4 && kamiAI.kami().laser != null){
                    Cons<Bullet> data = b -> {
                        if(b.time < 1f * 50){
                            b.vel.setLength(Math.max((1f - (b.time / (1f * 50))) * b.type.speed, 0.02f));
                        }else if(b.time >= 2.5f * 60){
                            b.vel.setLength(Math.min(Math.max((b.time - (2.5f * 60)) / 80f, 0.02f), b.type.speed * 1.5f));
                        }
                    };
                    int diff = 7 + difficulty;
                    kamiAI.reloads[10] += 6f;
                    for(int i = 0; i < diff; i++){
                        float angle = (i * (360f / diff)) + (kamiAI.reloads[10] * kamiAI.reloads[7]);
                        //Bullet bullet = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                        //bullet.hitSize = 8f;
                        //bullet.lifetime = 7f * 60f;
                        //bullet.data = data;
                        UnityCall.createKamiBullet(
                            kamiAI.unit, UnityBullets.kamiBullet1,
                            kamiAI.unit.x, kamiAI.unit.y, angle,
                            1f, (7f * 60f) / UnityBullets.kamiBullet1.lifetime, 8f,
                            2, 0f, 0f
                        );
                    }
                    kamiAI.reloads[6] = 0f;
                }
                if(kamiAI.reloads[2] != 1){
                    tmpVec.trns(kamiAI.kami().laserRotation, 670f).add(kamiAI.unit);
                    //UnityFx.kamiWarningLine.at(kamiAI.unit.x, kamiAI.unit.y, 0f, new Position[]{new Vec2(kamiAI.unit.x, kamiAI.unit.y), new Vec2(tmpVec)});
                    UnityCall.effect(UnityFx.kamiWarningLine, kamiAI.unit.x, kamiAI.unit.y, 0f, new Position[]{new Vec2(kamiAI.unit.x, kamiAI.unit.y), new Vec2(tmpVec)});

                    kamiAI.reloads[2] = 1;
                }
                if(kamiAI.reloads[1] >= 100){
                    if(kamiAI.reloads[4] != 1){
                        tmpVec.trns(kamiAI.kami().laserRotation, 80f).add(kamiAI.unit);
                        //kamiAI.kami().laser = UnityBullets.kamiLaser.create(kamiAI.unit, tmpVec.x, tmpVec.y, kamiAI.reloads[4]);
                        UnityCall.createKamiBullet(
                            kamiAI.unit, UnityBullets.kamiLaser,
                            tmpVec.x, tmpVec.y, kamiAI.reloads[4],
                            1f, 1f, -1f,
                            -1, 0f, 0f
                        );
                        kamiAI.reloads[4] = 1f;
                    }
                    if(kamiAI.kami().laser != null){
                        kamiAI.kami().laserRotation = Angles.moveToward(kamiAI.kami().laserRotation, kamiAI.unit.angleTo(kamiAI.target), 0.2f * Time.delta);
                        tmpVec.trns(kamiAI.kami().laserRotation, 80f).add(kamiAI.unit);
                        //kamiAI.kami().laser.rotation(kamiAI.kami().laserRotation);
                        kamiAI.kami().laser.set(tmpVec);
                    }
                    //kamiAI.reloads[5] += Time.delta;
                    //if(kamiAI.reloads[5] >= 4.2f * 60f){
                    if(kamiAI.kami().laser == null){
                        //kamiAI.kami().laser = null;
                        for(int i = 0; i < 6; i++) kamiAI.reloads[i] = 0f;
                        kamiAI.reloads[8] = 0f;
                        kamiAI.reloads[9] = 0f;
                        kamiAI.reloads[10] = 0f;
                        kamiAI.reloads[7] *= -1f;
                    }
                }
            }else if(kamiAI.target != null){
                tmpVec.trns(kamiAI.relativeRotation, 0, 210).add(kamiAI.target).sub(kamiAI.unit).scl(1 / 20f);
                kamiAI.kami().laserRotation = kamiAI.unit.angleTo(kamiAI.target);
                kamiAI.unit.move(tmpVec.x, tmpVec.y);
                kamiAI.reloads[0] += Time.delta;
            }
        }, kamiAI -> {
            kamiAI.reloads[0] = 1.50f * 60f;
            kamiAI.reloads[7] = 1f;
        }, 15 * 60f),
        //Dream Sign "Evil-Sealing Circle"
        new KamiShootType(kamiAI -> {
            if(kamiAI.reloads[1] >= 12f){
                int diff = 8;
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
                                //Bullet c = UnityBullets.kamiBullet1.create((Teamc)b.owner, b.x, b.y, b.rotation() + angleB);
                                //c.vel.scl(0.7f);
                                //c.data = data2;
                                UnityCall.createKamiBullet(
                                    (Teamc)b.owner, UnityBullets.kamiBullet1,
                                    b.x, b.y, b.rotation() + angleB,
                                    0.7f, 1f, -1f,
                                    j + 3, 0f, 0f
                                );
                            }
                            b.time = b.lifetime + 1f;
                        }
                    };
                    //Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                    //b.data = data1;
                    //b.vel.scl(0.8f);
                    UnityCall.createKamiBullet(
                        kamiAI.unit, UnityBullets.kamiBullet1,
                        kamiAI.unit.x, kamiAI.unit.y, angle,
                        0.8f, 1f, -1f,
                        8, 0f, 0f
                    );
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
                        //Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                        //b.time = (kamiAI.reloads[4] * 3.333f);
                        //b.data = data;
                        //b.fdata = (kamiAI.reloads[4] * 6.666f);
                        UnityCall.createKamiBullet(
                            kamiAI.unit, UnityBullets.kamiBullet1,
                            kamiAI.unit.x, kamiAI.unit.y, angle,
                            1f, 1f, -1f,
                            9, (kamiAI.reloads[4] * 3.333f), (kamiAI.reloads[4] * 6.666f)
                        );
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
        }, kamiAI -> kamiAI.reloads[5] = 1f, 20 * 60f),
        //EoL Dash/"St Nikou's Air Scroll" mix
        new MultiStageShootType(kamiAI -> {
            //UnityFx.kamiEoLCharge.at(kamiAI.unit.x, kamiAI.unit.y, 0f, kamiAI.unit);
            UnityCall.effect(UnityFx.kamiEoLCharge, kamiAI.unit.x, kamiAI.unit.y, 0f, kamiAI.unit);
            kamiAI.charging = true;
        }, 5){{
            stages = new KamiShootType[]{
                new KamiShootType(kamiAI -> {}, kamiAI -> {
                    kamiAI.targetPoint.trns(kamiAI.relativeRotation - 90f, 360f).add(kamiAI.unit);
                    kamiAI.lastPoint.set(kamiAI.unit);
                    //UnityFx.kamiWarningLine.at(kamiAI.unit.x, kamiAI.unit.y, 0f, new Position[]{new Vec2(kamiAI.unit.x, kamiAI.unit.y), new Vec2(kamiAI.targetPoint)});
                    UnityCall.effect(UnityFx.kamiWarningLine, kamiAI.unit.x, kamiAI.unit.y, 0f, new Position[]{new Vec2(kamiAI.unit.x, kamiAI.unit.y), new Vec2(kamiAI.targetPoint)});
                }, kamiAI -> kamiAI.stage <= 0 ? 80f : 5f).setStage(),
                new KamiShootType(kamiAI -> {
                    tmpVec.set(kamiAI.unit);
                    tmpVec.approachDelta(kamiAI.targetPoint, 25f);
                    kamiAI.unit.rotation(kamiAI.relativeRotation - 90f);
                    kamiAI.unit.set(tmpVec);
                    if(kamiAI.targetPoint.epsilonEquals(kamiAI.unit.x, kamiAI.unit.y, 16f)){
                        int diff = 65 + ((difficulty - 1) * 2);
                        for(int i = 0; i < diff; i++){
                            float fin = ((float)i / diff);
                            tmpVec2.set(kamiAI.lastPoint).lerp(kamiAI.targetPoint, fin);
                            float angle = i * 15f;
                            /*Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.team, tmpVec2.x, tmpVec2.y, angle, 0f);
                            b.fdata = i / (diff / 20f);
                            b.lifetime *= 2f;
                            b.data = new KamiBulletData<>(angle, (bullet, kamiBulletData) -> {
                                float time = (2f * 60f) + bullet.fdata;
                                if(bullet.time >= time) bullet.vel.trns(kamiBulletData.initialRotation, Mathf.clamp((bullet.time - time) / 15f, 0f, bullet.type.speed));
                            });*/
                            UnityCall.createKamiBullet(
                                kamiAI.unit, UnityBullets.kamiBullet1,
                                tmpVec2.x, tmpVec2.y, angle,
                                0f, 2f, -1f,
                                i + 11, i / diff / 20f, 0f
                            );
                        }
                        diff = 10 + (difficulty - 1);
                        for(int i = 0; i < diff; i++){
                            float angle = i * (360f / diff);
                            /*Bullet b = UnityBullets.kamiBullet1.create(kamiAI.unit, kamiAI.unit.x, kamiAI.unit.y, angle);
                            b.lifetime *= 2f;
                            b.vel.scl(1.1f);*/
                            UnityCall.createKamiBullet(
                                kamiAI.unit, UnityBullets.kamiBullet1,
                                kamiAI.unit.x, kamiAI.unit.y, angle,
                                1.1f, 2f, -1f,
                                -1, 0f, 0f
                            );
                        }
                        kamiAI.relativeRotation = Mathf.mod((((Mathf.round(kamiAI.relativeRotation / 60f) * 60f) + 90f) * 2f) - kamiAI.relativeRotation, 360f);
                        kamiAI.altTime = 3f * 60f;
                    }
                }, 3f * 60f).setStage()
            };
        }}
    };

    protected Vec2 targetPoint = new Vec2();
    protected Vec2 lastPoint = new Vec2();
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
    protected boolean charging = false;
    protected KamiShootType shooterType;
    protected float relativeRotation = 0f;
    protected float altTime = 0f;
    protected int stage = 0;

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
            //UnityFx.rainbowTextureTrail.at(unit.x, unit.y, unit.rotation, ((RainbowUnitType)unit.type).trailRegion);
            UnityCall.effect(UnityFx.rainbowTextureTrail, unit.x, unit.y, unit.rotation, (RainbowUnitType)unit.type);
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
        if(moveTime > 0.001f && !charging){
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
                //shooterType = types[types.length - 1];
                iSeq.removeIndex(rand);
                
                shooterType.init(this);
                autoShootTime = 0f;
                changed = true;
            }
        }else{
            shooterType.update(this);
        }
    }

    public void resetTypes(){
        Arrays.fill(reloads, 0f);
        Arrays.fill(tmpBullets, null);
    }

    public void changeType(){
        targetPoint.setZero();
        lastPoint.setZero();
        changed = false;
        charging = false;
        stage = 0;
        time = 0f;
        altTime = 0f;
        waitTime = 0f;
        relativeRotation = Mathf.random(360f);
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

    protected Kami kami(){
        return unit.as();
    }

    public static class KamiBulletData<T extends Bullet> implements Cons<T>{
        float initialRotation = 0f;
        Cons2<T, KamiBulletData<T>>[] stages;
        Cons2<T, KamiBulletData<T>> drawer = null;
        int currentStage = 0;

        @SafeVarargs
        KamiBulletData(Cons2<T, KamiBulletData<T>>... stages){
            this.stages = stages;
        }

        @SafeVarargs
        KamiBulletData(float rotation, Cons2<T, KamiBulletData<T>>... stages){
            initialRotation = rotation;
            this.stages = stages;
        }

        public boolean hasDrawer(){
            return drawer != null;
        }

        public void draw(T bullet){
            drawer.get(bullet, this);
        }

        @Override
        public void get(T bullet){
            stages[currentStage].get(bullet, this);
        }

        void nextStage(){
            currentStage = Math.min(currentStage + 1, stages.length - 1);
        }

        void moveStage(int index){
            currentStage = Mathf.clamp(index, 0, stages.length - 1);
        }
    }

    private static class MultiStageShootType extends KamiShootType{
        KamiShootType[] stages;
        int loop;

        MultiStageShootType(int loop){
            super(null, 0f);
            this.loop = loop;
        }

        MultiStageShootType(Cons<KamiAI> init, int loop){
            super(null, init, 0f);
            this.loop = loop;
        }

        @Override
        protected void init(KamiAI ai){
            super.init(ai);
            stages[0].init(ai);
        }

        @Override
        protected void update(KamiAI ai){
            ai.time += Time.delta;
            ai.altTime += Time.delta;
            stages[ai.stage % stages.length].update(ai);
            float f = stages[ai.stage % stages.length].liveMaxTime == null ? stages[ai.stage % stages.length].maxTime : stages[ai.stage % stages.length].liveMaxTime.get(ai);
            if(ai.altTime >= f){
                ai.stage++;
                ai.altTime = 0;
                ai.resetTypes();
                if(!(loop > 0 && (ai.stage) / stages.length >= loop)) stages[ai.stage % stages.length].init(ai);
            }
            if((ai.time >= maxTime && loop <= 0) || (loop > 0 && (ai.stage) / stages.length >= loop)){
                ai.changeType();
            }
        }
    }

    private static class KamiShootType{
        Cons<KamiAI> type;
        Cons<KamiAI> init;
        Floatf<KamiAI> liveMaxTime;
        float maxTime;
        boolean stage = false;

        KamiShootType(Cons<KamiAI> type, float time){
            this.type = type;
            maxTime = time;
            init = null;
        }

        KamiShootType(Cons<KamiAI> type, Cons<KamiAI> init, Floatf<KamiAI> time){
            this.type = type;
            liveMaxTime = time;
            this.init = init;
        }

        KamiShootType(Cons<KamiAI> type, Cons<KamiAI> init, float time){
            this.type = type;
            maxTime = time;
            this.init = init;
        }

        KamiShootType setStage(){
            stage = true;
            return this;
        }

        protected void init(KamiAI ai){
            if(init != null) init.get(ai);
        }

        protected void update(KamiAI ai){
            type.get(ai);
            if(stage) return;
            ai.time += Time.delta;
            if((ai.time >= maxTime && liveMaxTime == null) || (liveMaxTime != null && ai.time >= liveMaxTime.get(ai))){
                ai.changeType();
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static class KamiBulletDatas{
        private static Cons<Bullet>[] consDatas;
        private static KamiBulletData[] bulletDatas;

        static{
            consDatas = new Cons[11];
            consDatas[0] = b -> b.rotation(b.rotation() + (-1f * Time.delta));
            consDatas[1] = b -> b.rotation(b.rotation() + (Time.delta));

            consDatas[2] = b -> {
                if(b.time < 1f * 50){
                    b.vel.setLength(Math.max((1f - (b.time / (1f * 50))) * b.type.speed, 0.02f));
                }else if(b.time >= 2.5f * 60){
                    b.vel.setLength(Math.min(Math.max((b.time - (2.5f * 60)) / 80f, 0.02f), b.type.speed * 1.5f));
                }
            };

            float spacing = 20f;
            for(int i = 0; i < 5; i++){
                float angle = (i * spacing - (5 - 1) * spacing / 2f);
                consDatas[i + 3] = b -> {
                    if(b.time >= 1.1f * 17){
                        b.vel.scl(0.9f);
                        b.rotation(b.rotation() + (angle / 2f));
                        b.data = null;
                    }
                };
            }

            consDatas[8] = b -> {
                if(b.time > 1.1f * 17f){
                    float spacingB = 20f;
                    for(int j = 0; j < 5; j++){
                        float angleB = (j * spacingB - (5 - 1) * spacingB / 2f);
                        UnityCall.createKamiBullet(
                            (Teamc)b.owner, UnityBullets.kamiBullet1,
                            b.x, b.y, b.rotation() + angleB,
                            0.7f, 1f, -1f,
                            j + 3, 0f, 0f
                        );
                    }
                    b.time = b.lifetime + 1f;
                }
            };

            consDatas[9] = b -> {
                if((b.time - 40f) < 1.2f * 17){
                    b.vel.setLength(Math.max((1f - Mathf.clamp((b.time - 40f) / (1.2f * 17))) * b.type.speed, 0.02f));
                }else if((b.time - b.fdata) >= 2f * 60){
                    b.vel.setLength(Math.max(Mathf.clamp(((b.time - b.fdata) - (1.75f * 60)) / 30f) * b.type.speed, 0.02f));
                }
            };

            int diff = 65 + ((difficulty - 1) * 2);
            bulletDatas = new KamiBulletData[diff];

            for(int i = 0; i < diff; i++){
                float angle = i * 15f;
                bulletDatas[i] = new KamiBulletData<>(angle, (bullet, kamiBulletData) -> {
                    float time = (2f * 60f) + bullet.fdata;
                    if(bullet.time >= time) bullet.vel.trns(kamiBulletData.initialRotation, Mathf.clamp((bullet.time - time) / 15f, 0f, bullet.type.speed));
                });
            }
        }

        public static Object get(int i){
            if(i < 0) return null;
            return i < consDatas.length ? consDatas[i] : bulletDatas[i - consDatas.length];
        }
    }
}
