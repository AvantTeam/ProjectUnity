package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import unity.ai.kami.KamiBulletDatas.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {Bulletc.class, KamiLaserc.class}, serialize = false, pooled = true)
@EntityComponent
abstract class KamiLaserComp implements Bulletc{
    private final static Vec2 v = new Vec2();
    private final static Ellipse ep = new Ellipse();

    float x2, y2, width, lastTime, collidedTime;
    boolean intervalCollision = true, ellipseCollision = true;
    KamiLaserData bdata;

    @Import float x, y;
    @Import IntSeq collided;
    @Import BulletType type;
    @Import Team team;

    @Insert(value = "update()", block = Timedc.class, after = false)
    private void updateLastTime(){
        lastTime = time();
    }

    @Override
    public void update(){
        if(bdata != null){
            bdata.update(self());
        }

        if(!intervalCollision || timer(1, 5f)){
            updateCollision();
        }

        if((collidedTime += Time.delta) >= 60f){
            collided.clear();
            collidedTime = 0f;
        }
    }

    void updateCollision(){
        float mx = (x + x2) / 2f, my = (y + y2) / 2f;
        float ang = Angles.angle(x, y, x2, y2);
        Tmp.r1.setCentered(x, y, width * 2f);
        Tmp.r2.setCentered(x2, y2, width * 2f);
        Tmp.r1.merge(Tmp.r2);

        for(TeamData data : Vars.state.teams.present){
            if(data.team != team && data.tree != null){
                data.tree.intersect(Tmp.r1, e -> {
                    if(collided.contains(e.id)) return;
                    float size = e.hitSize / 2f;
                    if(ellipseCollision){
                        v.set(e).sub(mx, my).rotate(-ang);
                        ep.set(0f, 0f, Mathf.dst(x, y, x2, y2) * 2f + size, width * 2f + size);
                        if(ep.contains(v)){
                            type.hitEntity(self(), e, e.health);
                            type.hit(self(), e.x, e.y);
                            collided.add(e.id);
                        }
                    }else{
                        float dst = Intersector.distanceSegmentPoint(x, y, x2, y2, e.x, e.y);
                        if(dst < width + size){
                            type.hitEntity(self(), e, e.health);
                            type.hit(self(), e.x, e.y);
                            collided.add(e.id);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void remove(){
        if(bdata != null) bdata.removed(self());
    }

    @Replace(2)
    @Override
    public boolean collides(Hitboxc hitboxc){
        return false;
    }

    @Override
    public void hitbox(Rect rect){
        rect.setCentered(x, y, 0f);
    }

    @Replace(2)
    @Override
    public float clipSize(){
        return Float.MAX_VALUE;
    }
}
