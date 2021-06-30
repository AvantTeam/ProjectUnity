package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.entities.bullet.EndCutterLaserBulletType.*;
import unity.util.*;

public class AcceleratingLaserBulletType extends BulletType{
    public float maxLength = 1000f;
    public float laserSpeed = 15f;
    public float accel = 25f;
    public float width = 12f, collisionWidth = 8f;
    public float fadeTime = 60f;
    public float fadeInTime = 8f;
    public float oscOffset = 1.4f, oscScl = 1.1f;
    public Color[] colors = {Color.valueOf("ec745855"), Color.valueOf("ec7458aa"), Color.valueOf("ff9c5a"), Color.white};

    public AcceleratingLaserBulletType(float damage){
        super(0f, damage);
        despawnEffect = Fx.none;
        collides = false;
        pierce = true;
        impact = true;
        keepVelocity = false;
        hittable = false;
        absorbable = false;
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
        return maxRange > 0 ? maxRange : maxLength / 1.5f;
    }

    @Override
    public void init(){
        super.init();
        drawSize = maxLength * 2f;
    }

    @Override
    public void draw(Bullet b){
        float fade = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f) * Mathf.clamp(b.time / fadeInTime);
        float tipHeight = width / 2f;

        Lines.lineAngle(b.x, b.y, b.rotation(), b.fdata);
        for(int i = 0; i < colors.length; i++){
            float f = ((float)(colors.length - i) / colors.length);
            float w = f * (width + Mathf.absin(Time.time + (i * oscOffset), oscScl, width / 4)) * fade;

            Tmp.v2.trns(b.rotation(), b.fdata - tipHeight).add(b);
            Tmp.v1.trns(b.rotation(), width * 2f).add(Tmp.v2);
            Draw.color(colors[i]);
            Fill.circle(b.x, b.y, w / 2f);
            Lines.stroke(w);
            Lines.line(b.x, b.y, Tmp.v2.x, Tmp.v2.y, false);
            for(int s : Mathf.signs){
                Tmp.v3.trns(b.rotation(), w * -0.7f, w * s);
                Fill.tri(Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x + Tmp.v3.x, Tmp.v2.y + Tmp.v3.y);
            }
        }
        Tmp.v2.trns(b.rotation(), b.fdata + tipHeight).add(b);
        Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, width * 2f, colors[0], 0.5f);
        Draw.reset();
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.data = new LaserData();
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof LaserData){
            LaserData vec = (LaserData)b.data;
            if(vec.restartTime >= 5f){
                vec.velocity = Mathf.clamp((vec.velocityTime / accel) + vec.velocity, 0f, laserSpeed);
                b.fdata = Mathf.clamp(b.fdata + (vec.velocity * Time.delta), 0f, maxLength);
                vec.velocityTime += Time.delta;
            }else{
                vec.restartTime += Time.delta;
            }
        }

        if(b.timer(0, 5f)){
            Tmp.v1.trns(b.rotation(), b.fdata).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, collisionWidth, collisionWidth, (building, direct) -> {
                boolean h = building.block.absorbLasers || building.health > (damage * buildingDamageMultiplier) / 2f;
                if(direct){
                    if(h){
                        Tmp.v2.trns(b.rotation(), maxLength * 1.5f).add(b);
                        float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, building.x, building.y);
                        b.fdata = ((b.dst(building) - (building.block.size * Vars.tilesize / 2f)) + dst) + 4f;
                        if(b.data instanceof LaserData){
                            LaserData data = (LaserData)b.data;
                            data.velocity = 0f;
                            data.restartTime = 0f;
                            data.velocityTime = 0f;
                        }
                    }
                    building.damage(damage * buildingDamageMultiplier);
                }
                return h;
            }, unit -> {
                boolean h = unit.health > damage / 2f && unit.hitSize > width * 1.5f;
                if(h){
                    Tmp.v2.trns(b.rotation(), maxLength * 1.5f).add(b);
                    float dst = Intersector.distanceLinePoint(b.x, b.y, Tmp.v2.x, Tmp.v2.y, unit.x, unit.y);
                    b.fdata = ((b.dst(unit) - (unit.hitSize / 2f)) + dst) + 4f;
                    if(b.data instanceof LaserData){
                        LaserData data = (LaserData)b.data;
                        data.velocity = 0f;
                        data.restartTime = 0f;
                        data.velocityTime = 0f;
                    }
                }
                hitEntity(b, unit, unit.health);
                return h;
            }, (ex, ey) -> hitEffect.at(ex, ey, b.rotation()), true);
        }
    }
}
