package unity.entities.bullet.exp;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;

public class GeyserLaserBulletType extends ExpLaserBulletType{
    public float widthInc = 0.05f;

    public GeyserLaserBulletType(float length, float damage){
        super(length, damage);
        width = 3f;
        hitMissed = true;
    }

    public Liquid getLiquid(Bullet b){
        return b.data instanceof Liquid l ? l : Liquids.water;
    }

    @Override
    public void init(Bullet b){
        Liquid l = getLiquid(b); //b.data is overwritten during elbt's init!
        super.init(b);
        Position dest = (Position) b.data;
        b.rotation(b.angleTo(dest));
        b.fdata = b.dst(dest);
        b.data = l;
    }

    @Override
    public void draw(Bullet b){
        Tmp.v1.trns(b.rotation(), b.fdata).add(b);

        float width = this.width + widthInc * getLevel(b);
        Liquid l = getLiquid(b);
        Draw.color(l.color, 1f);

        Draw.alpha(0.4f);
        Lines.stroke(b.fout() * width * strokes[0]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

        Draw.alpha(1);
        Lines.stroke(b.fout() * width * strokes[1]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);

        Draw.color(l.color, Color.white, 0.6f);
        Lines.stroke(b.fout() * width * strokes[2]);
        Lines.line(b.x, b.y, Tmp.v1.x, Tmp.v1.y);
        Draw.reset();

        Drawf.light(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * 10 * b.fout(), l.lightColor, l.lightColor.a);
    }

    @Override
    public void fragExp(Bullet b, float x, float y){
        if(fragBullet != null){
            for(int i = 0; i < fragBullets; i++){
                float len = Mathf.random(1f, 7f);
                float a = b.rotation() + Mathf.range(fragCone/2) + fragAngle;
                fragBullet.create(b.owner, b.team, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, -1f, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax), b.data);
            }
        }
    }
}
