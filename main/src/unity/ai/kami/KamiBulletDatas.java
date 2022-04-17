package unity.ai.kami;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;
import unity.gen.*;

public class KamiBulletDatas{
    public static Seq<KamiBulletDataBase<?>> all = new Seq<>();
    public static KamiBulletData turnDelay1 = new KamiBulletData(){
        @Override
        public void update(KamiBullet b){
            if(b.time >= 1.8f * 60f){
                b.turn = b.fdata;
            }
        }
    },

    suddenTurnDelay1 = new KamiBulletData(){
        @Override
        public void update(KamiBullet b){
            if(b.time >= 1.8f * 60f && b.fdata != 0f){
                b.rotation(b.rotation() + b.fdata);
                b.fdata = 0f;
            }
        }
    },

    stopChangeDirection = new KamiBulletData(){
        @Override
        public void update(KamiBullet b){
            if(b.time >= 1.8f * 60f && b.fdata > 0f){
                //float fout = 1f - Mathf.clamp((b.time - 1.8f * 60f) / 40f);
                //b.rotation(b.rotation() + b.fdata);
                //b.fdata = 0f;
                b.vel.scl(1f - 0.15f * Time.delta);
                if(b.time >= b.fdata){
                    b.rotation(b.fdata2);
                    b.vel.trns(b.rotation(), 4f);
                    b.fdata = 0f;
                    //fdata2
                }
            }
        }
    },

    positionLock = new KamiBulletData(){
        @Override
        public void update(KamiBullet b){
            if(b.owner instanceof Position){
                b.set((Position)b.owner);
            }
        }
    };

    public static KamiLaserData hyperSpeedLaser1 = new KamiLaserData(){
        @Override
        public void update(KamiLaser b){
            if(b.data instanceof Position){
                Position p = (Position)b.data;
                if(b.time <= 7f){
                    b.x = p.getX();
                    b.y = p.getY();
                    b.fdata = Mathf.dst(b.x, b.y, b.x2, b.y2);
                }else{
                    Tmp.v1.set(b.x2, b.y2).approachDelta(Tmp.v2.set(b.x, b.y), b.fdata / (16f - 7f));
                    b.x2 = Tmp.v1.x;
                    b.y2 = Tmp.v1.y;
                    if(b.within(b.x2, b.y2, 2f)){
                        b.remove();
                    }
                }
            }
        }
    };

    public static class KamiBulletData extends KamiBulletDataBase<KamiBullet>{

    }

    public static class KamiLaserData extends KamiBulletDataBase<KamiLaser>{

    }

    private static abstract class KamiBulletDataBase<T extends Bullet>{
        public int id;

        KamiBulletDataBase(){
            id = all.size;
            all.add(this);
        }

        public void update(T b){

        }

        public void removed(T b){

        }
    }
}
