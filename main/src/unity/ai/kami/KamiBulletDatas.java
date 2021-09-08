package unity.ai.kami;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import unity.ai.*;
import unity.content.*;
import unity.entities.bullet.*;
import unity.entities.bullet.KamiAltLaserBulletType.*;
import unity.gen.*;
import unity.util.func.*;

import java.util.*;

public class KamiBulletDatas{
    public static Seq<Func<Bullet, KamiBulletData>> dataSeq = new Seq<>();
    public static int accelTurn, expandShrink, turnRegular, nonDespawnable, slowDownWaitAccel,
    junkoSlowDown, junkoLaser, bounceSimple;

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
            new DataStage(-1f, (data, bl) -> {
                if(data.ai != null){
                    data.ai.createSound(UnitySounds.kamiShootChime, bl.x, bl.y, 1f);
                }
            }),
            new DataStage(3f * 60f, (data, bl) -> bl.vel.set(1f, 0f).setLength(data.fin() * bl.type.speed * 1.1f).setAngle(data.initialRotation))
        ));

        junkoSlowDown = addData(b -> new KamiBulletData(b.vel().len(),
            new DataStage(45f, (data, bl) -> {}),
            new DataStage(20f, (data, bl) -> bl.vel.setLength(Mathf.lerp(data.initialRotation, data.initialRotation / 3f, data.fin())))
        ));

        junkoLaser = addData(b -> new KamiBulletData(b.rotation(),
            new DataStage(25f, (data, bl) -> {}),
            new DataStage(-1f, (data, bl) -> {
                bl.hitSize /= 1.5f;
                KamiAltLaserBulletType lType = ((KamiAltLaserBulletType)UnityBullets.kamiVariableLaser);
                KamiLaserData dat = new KamiLaserData();
                dat.x = dat.x2 = bl.x;
                dat.y = dat.y2 = bl.y;
                dat.width = 3f;
                dat.data = bl;
                dat.update = (dt, b2) -> {
                    dt.x = ((Bullet)dt.data).x;
                    dt.y = ((Bullet)dt.data).y;
                    Tmp.v1.set(dt.x2, dt.y2).sub(dt.x, dt.y).limit(140f).add(dt.x, dt.y);
                    dt.x2 = Tmp.v1.x;
                    dt.y2 = Tmp.v1.y;
                };
                Bullet b3 = lType.create(bl.owner, ((Teamc)bl.owner).team(), dat);
                b3.lifetime = bl.lifetime;
                b3.time = bl.time;
                if(data.ai != null){
                    data.ai.createSound(UnitySounds.kamiLaser, bl.x, bl.y, 1f);
                }
            }),
            new DataStage(45f, (data, bl) -> bl.vel.set(1f, 0f).setLength(data.fin() * data.attribute).setAngle(data.initialRotation))).attribute(b.vel.len())
        );

        bounceSimple = addData(b -> new KamiBulletData(true).setBounce(true, true, false, false, 2));
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
        public Cons3<Bullet, Rect, Integer> onBounce = (b, rect, ang) -> {};
        public boolean empty = false;
        public byte bounce = 0;
        public Rect barrier;
        public KamiAI ai;
        int stage = 0;
        float time = 0f;

        public KamiBulletData(boolean despawn){
            this.despawn = despawn;
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

        public KamiBulletData setBounce(boolean left, boolean right, boolean up, boolean down, int bounces){
            bounces = Math.min(bounces, 15);
            if(left) bounce |= 1;
            if(right) bounce |= 1 << 1;
            if(up) bounce |= 1 << 2;
            if(down) bounce |= 1 << 3;
            bounce |= bounces << 4;
            return this;
        }

        public KamiBulletData onBounce(Cons3<Bullet, Rect, Integer> cons){
            onBounce = cons;
            return this;
        }

        public boolean bl(){
            return (bounce & 1) != 0;
        }

        public boolean br(){
            return (bounce & 2) != 0;
        }

        public boolean bu(){
            return (bounce & 4) != 0;
        }

        public boolean bd(){
            return (bounce & 8) != 0;
        }

        public int bounces(){
            return bounce >>> 4;
        }

        public void useBounce(){
            int b = (bounce >>> 4);
            b -= 1;
            b = b << 4;
            if(b <= 0){
                bounce = 0;
                barrier = null;
                return;
            }
            bounce &= 15;
            bounce |= b;
        }

        @Override
        public float fin(){
            return Mathf.clamp(time / stageA[stage % stageA.length].time);
        }

        public void update(Bullet b){
            if(barrier != null){
                if(((b.x <= barrier.x && bl()) || (b.x >= barrier.x + barrier.width && br())) && bounces() > 0){
                    if(b.x >= barrier.x + barrier.width){
                        b.x = barrier.x + barrier.width - 0.01f;
                        onBounce.get(b, barrier, 0);
                    }
                    if(b.x <= barrier.x){
                        b.x = barrier.x + 0.01f;
                        onBounce.get(b, barrier, 2);
                    }
                    b.vel.x *= -1f;
                    useBounce();
                }
                if(barrier != null && ((b.y <= barrier.y && bd()) || (b.y >= barrier.y + barrier.height && bu())) && bounces() > 0){
                    if(b.y >= barrier.y + barrier.height){
                        b.y = barrier.y + barrier.height - 0.01f;
                        onBounce.get(b, barrier, 3);
                    }
                    if(b.y <= barrier.y){
                        b.y = barrier.y + 0.01f;
                        onBounce.get(b, barrier, 1);
                    }
                    b.vel.y *= -1f;
                    useBounce();
                }
            }
            if(empty) return;
            DataStage a = stageA[stage % stageA.length];
            if(a.time <= 0f){
                if(a.data != null) a.data.get(this, b);
                stage++;
                a = stageA[stage % stageA.length];
            }
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
