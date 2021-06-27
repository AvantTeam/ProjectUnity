package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.util.*;

public class NoPierceLaserBulletType extends ContinuousLaserBulletType{
    public NoPierceLaserBulletType(float damage){
        super(damage);
    }

    @Override
    public void update(Bullet b){

        //damage every 5 ticks
        if(b.timer(1, 5f)){
            Healthc t = Utils.linecast(b, b.x, b.y, b.rotation(), length);
            float realLength = t != null ? b.dst(t) : length;
            Damage.collideLine(b, b.team, hitEffect, b.x, b.y, b.rotation(), realLength, largeHit);
        }

        if(shake > 0){
            Effect.shake(shake, shake, b);
        }
    }

    @Override
    public void draw(Bullet b){
        Healthc t = Utils.linecast(b, b.x, b.y, b.rotation(), length);
        float realLength = t != null ? b.dst(t) : length;
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
