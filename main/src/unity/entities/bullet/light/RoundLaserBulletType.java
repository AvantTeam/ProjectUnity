package unity.entities.bullet.light;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class RoundLaserBulletType extends LaserBulletType{
    public float lightStroke = 40f;
    public float spaceMag = 45f;
    public float[] tscales = {1f, 0.7f, 0.5f, 0.24f};
    public float[] strokes = {2.8f, 2.4f, 1.9f, 1.3f};
    public float[] lenscales = {1f, 1.13f, 1.16f, 1.17f};

    public RoundLaserBulletType(float damage){
        super(damage);

        lifetime = 14f;
        colors = new Color[]{Color.valueOf("4787ff55"), Color.valueOf("4787ffaa"), Pal.lancerLaser, Color.white};
    }

    @Override
    public void draw(Bullet b){
        float realLength = b.fdata;
        float baseLen = realLength * b.fout();

        Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
        for(int s = 0; s < colors.length; s++){
            Draw.color(Tmp.c1.set(colors[s]));
            for(int i = 0; i < tscales.length; i++){
                Tmp.v1.trns(b.rotation() + 180f, (lenscales[i] - 1f) * spaceMag);
                Lines.stroke(width * b.fout() * strokes[s] * tscales[i]);
                Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * lenscales[i], false);
            }
        }

        Tmp.v1.trns(b.rotation(), baseLen * 1.1f);

        Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);
        Draw.reset();
    }
}
