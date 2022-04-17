package unity.entities.comp;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.ai.kami.KamiBulletDatas.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {Bulletc.class, KamiBulletc.class}, serialize = false, pooled = true)
@EntityComponent
abstract class KamiBulletComp implements Bulletc{
    private static final Ellipse e = new Ellipse();
    private static final Vec2 vec = new Vec2();
    private static float lastDelta;
    float turn, width, length, resetTime, lastTime, fdata2;
    int telegraph;
    FloatSeq lastPositions;
    KamiBulletData bdata;

    @Import IntSeq collided;
    @Import float x, y;
    @Import BulletType type;
    @Import Team team;
    @Import Entityc owner;

    @Insert(value = "update()", after = false)
    private void updatePre(){
        if(isTelegraph()){
            lastDelta = Time.delta;
            Time.delta = 3f * lastDelta;
        }
    }

    @Insert(value = "update()")
    private void updatePost(){
        if(isTelegraph()) Time.delta = lastDelta;
    }

    @Insert(value = "update()", block = Timedc.class, after = false)
    private void updateLastTime(){
        lastTime = time();
    }

    @Override
    public void update(){
        if(lastPositions != null){
            lastPositions.add(x, y);
            if(lastPositions.size > telegraph * 4){
                lastPositions.removeRange(0, 1);
            }
        }
        if(telegraph > 0 && lastPositions == null){
            KamiBullet b = KamiBullet.create();
            b.x = x;
            b.y = y;
            b.type = type;
            b.team = team;
            b.owner = owner;
            b.vel.set(vel());
            b.lifetime = lifetime();
            b.time = time();
            b.drag = drag();
            b.fdata = fdata();
            b.fdata2 = fdata2;
            b.bdata = bdata;
            b.width = width;
            b.length = length;
            b.hitSize = hitSize();
            b.lastPositions = new FloatSeq();
            b.telegraph = telegraph;
            b.add();
            telegraph = -1;
        }
        if(resetTime >= 60f){
            collided.clear();
            resetTime = 0f;
        }
        resetTime += Time.delta;
        if(bdata != null){
            bdata.update(self());
        }
        if(turn != 0f){
            rotation(rotation() + turn * Time.delta);
        }
    }

    @Override
    public void remove(){
        if(bdata != null) bdata.removed(self());
    }

    boolean isTelegraph(){
        return lastPositions != null;
    }

    /*
    @Replace
    @Override
    @Combine
    public boolean collides(Hitboxc other){
        @Resolve(Method.and)
        boolean result = false;

        //boolean c;
        if(width == length){
            result = within(other, (other.hitSize() / 2f) + width);
        }else{
            float h = other.hitSize() / 2f;
            vec.set(other).sub(x, y).rotate(-rotation());
            e.set(0, 0, h + length * 2f, h + width * 2f);
            result = e.contains(vec);
        }
        return result;
    }
     */

    @Replace(2)
    @Override
    public boolean collides(Hitboxc other){
        boolean base = lastPositions == null && type.collides && (other instanceof Teamc && ((Teamc)other).team() != team)
        && !(other instanceof Flyingc && !((Flyingc)other).checkTarget(type.collidesAir, type.collidesGround))
        && !(type.pierce && hasCollided(other.id()));

        boolean result;

        if(width == length){
            result = within(other, (other.hitSize() / 2f) + width);
        }else{
            float h = other.hitSize() / 2f;
            vec.set(other).sub(x, y).rotate(-rotation());
            e.set(0, 0, h + length * 2f, h + width * 2f);
            result = e.contains(vec);
        }

        return base && result;
    }

    @Replace
    @Override
    public void hitbox(Rect rect){
        if(width == length){
            float size = width * 2f;
            rect.setCentered(x, y, size, size);
        }else{
            Vec2 v = Tmp.v1.trns(rotation(), (length * 2f) - (width * 2f));
            rect.setCentered(v.x + x, v.y + y, width * 2f);
            rect.merge(Tmp.r1.setCentered(-v.x + x, -v.y + y, width * 2f));
        }
    }

    @Override
    public float clipSize(){
        return Math.max(width, length) * 2.5f;
    }
}
