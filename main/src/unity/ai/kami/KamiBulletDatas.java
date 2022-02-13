package unity.ai.kami;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;

public class KamiBulletDatas{
    public static Seq<NewBulletData> all = new Seq<>();

    public static class NewBulletData{
        public int id;

        public NewBulletData(){
            id = all.size;
            all.add(this);
        }

        public void update(Bullet b){

        }

        public void removed(Bullet b){

        }
    }
}
