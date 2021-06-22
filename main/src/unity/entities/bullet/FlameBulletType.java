package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.graphics.*;

import java.util.*;

public class FlameBulletType extends BulletType{
    public Color[] colors = {Pal.lightFlame, Pal.darkFlame, Color.gray};
    public float particleSpread = 10f, particleSizeScl = 1.5f;
    public int particleAmount = 8;
    private final Color tc = new Color();
    private Color[] hitColors;

    public FlameBulletType(float speed, float damage){
        super(speed, damage);
        pierce = true;
        lifetime = 12f;
        despawnEffect = Fx.none;
        status = StatusEffects.burning;
        statusDuration = 60f * 4f;
        hitSize = 7f;
        collidesAir = false;
        keepVelocity = false;
        hittable = false;
    }

    @Override
    public void init(){
        super.init();
        hitColors = Arrays.copyOf(colors, Math.max(1, colors.length - 1));
        shootEffect = new Effect(lifetime, range() * 2f, e -> {
            Draw.color(tc.lerp(colors, e.fin()));

            Angles.randLenVectors(e.id, particleAmount, e.finpow() * range() + 15f, e.rotation, particleSpread, (x, y) ->
            Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * particleSizeScl));
        });
        hitEffect = new Effect(14f, e -> {
            Draw.color(tc.lerp(hitColors, e.fin()));
            Lines.stroke(0.5f + e.fout());

            Angles.randLenVectors(e.id, particleAmount / 3, e.fin() * 15f, e.rotation, 50f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f);
            });
        });
    }
}
