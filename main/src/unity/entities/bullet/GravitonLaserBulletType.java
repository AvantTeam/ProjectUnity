package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.util.*;

public class GravitonLaserBulletType extends ContinuousLaserBulletType{
    public int max = 6;
    private int len;

    public GravitonLaserBulletType(float damage){
        super(damage);
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        b.fdata = length;
    }

    @Override
    public void update(Bullet b){
        if(b.timer(1, 5f)){
            len = 0;
            b.fdata = length;
            Tmp.v1.trns(b.rotation(), length).add(b);
            Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, (build, direct) -> {
                if(direct){
                    build.damage(damage);
                    len++;
                }
                if(build.block.absorbLasers || (direct && len >= max)){
                    b.fdata = b.dst(build);
                }
                return build.block.absorbLasers || (direct && len >= max);
            }, unit -> {
                unit.damage(damage);
                if(knockback != 0f) unit.impulse(Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f));
                if(statusDuration > 0f) unit.apply(status, statusDuration);
                if(len >= max) b.fdata = b.dst(unit);
                return len >= max;
            }, (ex, ey) -> hit(b, ex, ey), true);
        }
    }

    @Override
    public void draw(Bullet b){
        float realLength = b.fdata;
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float baseLen = realLength * fout;

        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
        for(int s = 0; s < colors.length; s++){
            Draw.color(Tmp.c1.set(colors[s]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * spaceMag);
                Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);

        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
    }
}
