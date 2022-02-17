package unity.ai.kami;

import arc.struct.*;
import arc.util.*;
import unity.gen.*;

public class KamiBulletDatas{
    public static Seq<KamiBulletData> all = new Seq<>();
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
    };

    public static class KamiBulletData{
        public int id;

        public KamiBulletData(){
            id = all.size;
            all.add(this);
        }

        public void update(KamiBullet b){

        }

        public void removed(KamiBullet b){

        }
    }
}
