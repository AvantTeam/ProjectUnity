package unity.entities.bullet;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;

//temporary naming
public class AContinuousLaserBulletType extends ContinuousLaserBulletType{
    public AContinuousLaserBulletType(float damage){
        super(damage);
    }

    public AContinuousLaserBulletType(){
        this(0f);
    }

    @Override
    public void update(Bullet b){
        float lengthC = length;
        if(b.owner instanceof Velc){
            float valA = ((Velc) b.owner).vel().len() * 19f;
            lengthC = Mathf.clamp(valA, 0f, length);
        }
        if(b.timer(1, 5f)) Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), lengthC, largeHit);
        b.fdata = lengthC;
    }

    @Override
    public void draw(Bullet b){
        float realLength = Damage.findLaserLength(b, b.fdata());
        float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
        float baseLen = realLength * fout;
        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
        for(int s = 0; s < colors.length; s++){
            Draw.color(Tmp.c1.set(colors[s]).mul(1f + Mathf.absin(Time.time(), 1f, 0.1f)));
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * spaceMag);
                Lines.stroke((width + Mathf.absin(Time.time(), oscScl, oscMag)) * fout * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }
        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);
        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
    }
}
