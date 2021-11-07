package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.graphics.*;
import unity.util.*;

public class EndSweepLaser extends AntiCheatBulletTypeBase{
    public float length = 300f;
    public float collisionWidth = 3f;
    public float widthLoss = 0.7f;
    public float width = 9f, oscScl = 0.8f, oscMag = 1.5f;
    public float distance = 150f;
    public Color[] colors = {UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
    public BulletType hitBullet;

    private static float len;
    private static int pierceIdx;

    public EndSweepLaser(float damage){
        super(0f, damage);
        despawnEffect = Fx.none;
        hittable = collides = absorbable = keepVelocity = false;
        impact = true;
        pierceShields = true;
    }

    @Override
    public float estimateDPS(){
        return damage * (lifetime / 2f) / 5f * 3f;
    }

    @Override
    public float continuousDamage(){
        return damage / 5f * 60f;
    }

    @Override
    public float range(){
        return length;
    }

    @Override
    public void init(){
        super.init();
        despawnHit = false;
        drawSize = length * 2f + 20f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new Vec2();
    }

    @Override
    public void hit(Bullet b, float x, float y){
        super.hit(b, x, y);
        if(b.data instanceof Vec2){
            Vec2 v = (Vec2)b.data;
            if(v.dst(x, y) > distance){
                v.set(x, y);
                hitBullet.create(b.owner, b.team, x, y, b.rotation());
            }
        }
    }

    @Override
    public void update(Bullet b){
        if(b.timer(1, 5f)){
            len = length;
            pierceIdx = pierce ? pierceCap : 0;
            Vec2 v = Tmp.v1.trns(b.rotation(), len).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, v.x, v.y, collisionWidth, (building, direct) -> {
                if(direct){
                    if(pierceIdx <= 0) pierceIdx--;
                    len = b.dst(building);
                    hitBuildingAntiCheat(b, building);
                }
                return pierceIdx <= 0;
            }, unit -> {
                pierceIdx--;
                if(pierceIdx <= 0) len = b.dst(unit);
                hitUnitAntiCheat(b, unit);
                return pierceIdx <= 0;
            }, (ex, ey) -> hit(b, ex, ey), true);
            b.fdata = len;
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void draw(Bullet b){
        Vec2 v =Tmp.v1.trns(b.rotation(), b.fdata).add(b);
        float fin = Mathf.clamp(b.time / 16f) * Mathf.clamp(b.time > b.lifetime - 16f ? 1f - (b.time - (b.lifetime - 16f)) / 16f : 1f);

        float w = (width + Mathf.absin(oscScl, oscMag)) * fin;
        for(Color c : colors){
            Draw.color(c);
            Lines.stroke(w);
            Lines.line(b.x, b.y, v.x, v.y, false);
            Drawf.tri(b.x, b.y, w * 1.22f, (width * 2f), b.rotation() + 180f);
            Drawf.tri(v.x, v.y, w * 1.22f, (width * 3f) + (w * 2f), b.rotation());
            w *= widthLoss;
        }
    }
}
