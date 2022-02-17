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
    float turn, width, length, resetTime, fdata2;
    KamiBulletData bdata;

    @Import IntSeq collided;
    @Import float x, y;
    @Import BulletType type;
    @Import Team team;

    @Override
    public void update(){
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
        boolean base = type.collides && (other instanceof Teamc && ((Teamc)other).team() != team)
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
