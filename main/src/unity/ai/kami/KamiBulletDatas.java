package unity.ai.kami;

import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import java.util.*;

public class KamiBulletDatas{
    public static Seq<Func<Bullet, KamiBulletData>> dataSeq = new Seq<>();
    public static int accelTurn;

    //preset datas
    public static void load(){
        accelTurn = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(b.lifetime, (data, bl) -> bl.vel.scl(1f + (0.05f * Time.delta)).limit(bl.type.speed * 2f).rotate(data.attribute * Time.delta))
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

    public static class KamiBulletData{
        public float initialRotation, attribute = 0f;
        public boolean modulate;
        public DataStage[] stageA;
        int stage = 0;
        float time = 0f;

        public KamiBulletData(float rotation, boolean mod, DataStage... stages){
            stageA = Arrays.copyOf(stages, stages.length);
            initialRotation = rotation;
            modulate = mod;
        }

        public KamiBulletData(float rotation, DataStage... stages){
            this(rotation, false, stages);
        }

        public void update(Bullet b){
            DataStage a = stageA[stage % stageA.length];
            a.data.get(this, b);
            time += Time.delta;
            if(time >= a.time){
                stage++;
                time = 0f;
                if(!modulate && stage > stageA.length) b.data = null;
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
