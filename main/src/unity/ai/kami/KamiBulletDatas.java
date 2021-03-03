package unity.ai.kami;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import java.util.*;

public class KamiBulletDatas{
    public static Seq<Func<Bullet, KamiBulletData>> dataSeq = new Seq<>();
    public static int accelTurn, expandShrink, turnRegular, nonDespawnable, slowDownWaitAccel;

    //preset datas
    public static void load(){
        accelTurn = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(b.lifetime, (data, bl) -> bl.vel.scl(1f + (0.05f * Time.delta)).limit(bl.type.speed * 2f).rotate(data.attribute * Time.delta))
        ));

        expandShrink = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(10f, (data, bl) -> bl.hitSize = (Mathf.clamp(data.time / 10f) * 110f) + 10f),
            new DataStage(210f, (data, bl) -> {
                float fout = Mathf.clamp(1f - (data.time / 210f));
                bl.hitSize = (fout * 110f) + 10f;
            })
        ));

        turnRegular = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(b.lifetime, (data, bl) -> bl.vel.rotate(data.attribute * Time.delta))
        ));

        nonDespawnable = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(b.lifetime, (data, bl) -> data.despawn = bl.time >= bl.lifetime)
        ));

        slowDownWaitAccel = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(40f, (data, bl) -> bl.vel.setLength(data.fout() * bl.type.speed * 0.75f)),
            new DataStage(2f * 60f, null),
            new DataStage(3f * 60f, (data, bl) -> bl.vel.set(1f, 0f).setLength(data.fin() * bl.type.speed * 1.1f).setAngle(data.initialRotation))
        ));
    }

    public static KamiBulletData get(Bullet bullet, int type){
        KamiBulletData tmp = dataSeq.get(type).get(bullet);
        bullet.data = tmp;
        return tmp;
    }

    public static int addData(Func<Bullet, KamiBulletData> data){
        dataSeq.add(data);
        return dataSeq.size - 1;
    }

    public static class KamiBulletData implements Scaled{
        public float initialRotation, attribute = 0f;
        public boolean modulate, despawn = true;
        public DataStage[] stageA;
        public boolean empty = false;
        int stage = 0;
        float time = 0f;

        public KamiBulletData(){
            despawn = false;
            empty = true;
        }

        public KamiBulletData(float rotation, boolean mod, DataStage... stages){
            stageA = Arrays.copyOf(stages, stages.length);
            initialRotation = rotation;
            modulate = mod;
        }

        public KamiBulletData(float rotation, DataStage... stages){
            this(rotation, false, stages);
        }

        public KamiBulletData attribute(float val){
            attribute = val;
            return this;
        }

        @Override
        public float fin(){
            return Mathf.clamp(time / stageA[stage % stageA.length].time);
        }

        public void update(Bullet b){
            if(empty) return;
            DataStage a = stageA[stage % stageA.length];
            if(a.data != null) a.data.get(this, b);
            time += Time.delta;
            if(time >= a.time){
                stage++;
                time = 0f;
                if(!modulate && stage >= stageA.length) b.data = null;
            }
        }
    }

    public static class DataStage{
        public Cons2<KamiBulletData, Bullet> data;
        public float time;

        public DataStage(float time, Cons2<KamiBulletData, Bullet> data){
            this.time = time;
            this.data = data;
        }
    }
}
