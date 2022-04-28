package unity.entities.bullet.monolith.laser;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import unity.graphics.*;

/** @author GlennFolker */
public class HelixLaserBulletType extends LaserBulletType{
    public float laserExtTime = 0.07f, laserShrinkTime = 0.3f;

    public HelixLaserBulletType(float damage){
        super(damage);
        lifetime = 48f;
    }

    @Override
    public void draw(Bullet b){
        float
            realLength = b.fdata, scl = realLength / length,
            fin = b.fin(), fout = b.fout(), rot = b.rotation() - 90f,

            // Laser base line.
            laserLenf = Mathf.curve(fin, 0f, laserExtTime * scl), laserLen = Interp.pow2Out.apply(laserLenf) * realLength,
            laserShrinkf = Mathf.curve(fin, 1f - laserShrinkTime * scl, 1f), laserShrink = Interp.pow3In.apply(laserShrinkf) * realLength,
            cwidth = width,
            compound = 1f;

        for(Color color : colors){
            Tmp.v1.trns(rot, laserShrink);

            Draw.color(color);
            Lines.stroke((cwidth *= lengthFalloff) * Interp.pow10Out.apply(fout));
            Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, rot, laserLen - laserShrink, false);

            Tmp.v1.trns(rot, laserLen);
            UnityDrawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Lines.getStroke(), cwidth * 2f + width / 2f, rot);

            Fill.circle(b.x, b.y, 1f * cwidth * fout);
            for(int i : Mathf.signs){
                UnityDrawf.tri(b.x, b.y, sideWidth * fout * cwidth, sideLength * compound, rot + sideAngle * i);
            }

            compound *= lengthFalloff;
        }
    }
}
