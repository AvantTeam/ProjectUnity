package unity.entities.bullet;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;

public class KamiAltLaserBulletType extends BulletType{
    public float fadeOut = 20f;
    public float fadeIn = 20f;
    private static final Ellipse tElpse = new Ellipse();
    private static TextureRegion circleRegion;

    public KamiAltLaserBulletType(float damage){
        this.damage = damage;
        speed = 0.01f;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        keepVelocity = false;
        collides = false;
        pierce = true;
        hittable = false;
        absorbable = false;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        if(b.data instanceof KamiLaserData data){
            data.init.get(data, b);
        }
    }

    @Override
    public void update(Bullet b){
        if(b.data instanceof KamiLaserData data){
            data.update.get(data, b);
            Tmp.v1.set(data.x, data.y).add(data.x2, data.y2).scl(0.5f);
            b.x = Tmp.v1.x;
            b.y = Tmp.v1.y;
            if(b.timer(1, 5f)){
                float fout = Mathf.clamp(b.time > b.lifetime - fadeOut ? 1f - (b.time - (b.lifetime - fadeOut)) / fadeOut : 1f) * Mathf.clamp(b.time / fadeIn) * data.width;
                float ang = Tmp.v2.set(data.x2, data.y2).sub(data.x, data.y).angle();
                float dst = Tmp.v2.len();
                Tmp.r1.setCentered(data.x, data.y, fout * 2f);
                Tmp.r2.setCentered(data.x2, data.y2, fout * 2f);
                Tmp.r1.merge(Tmp.r2);

                Units.nearby(Tmp.r1, e -> {
                    if(e.team != b.team){
                        Tmp.v2.set(e).sub(Tmp.v1).rotate(-ang);
                        tElpse.set(0f, 0f, dst, fout * 2f);
                        if(tElpse.contains(Tmp.v2)){
                            b.collision(e, e.x, e.y);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void draw(Bullet b){
        if(b.data instanceof KamiLaserData data){
            if(circleRegion == null) circleRegion = Core.atlas.find("circle");
            float fout = Mathf.clamp(b.time > b.lifetime - fadeOut ? 1f - (b.time - (b.lifetime - fadeOut)) / fadeOut : 1f) * Mathf.clamp(b.time / fadeIn);
            Draw.color(Tmp.c1.set(Color.red).shiftHue(Time.time * 3f));
            Lines.stroke( (data.width + 3f) * fout * 2f);
            Lines.line(circleRegion, data.x, data.y, data.x2, data.y2, false);
            Draw.color();
            Lines.stroke( data.width * fout * 2f);
            Lines.line(circleRegion, data.x, data.y, data.x2, data.y2, false);
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    public Bullet create(Entityc owner, Team team, KamiLaserData data){
        Tmp.v1.set(data.x, data.y).add(data.x2, data.y2).scl(0.5f);
        Tmp.v2.set(data.x2, data.y2).sub(data.x, data.y);
        return create(owner, team, Tmp.v1.x, Tmp.v1.y, Tmp.v2.angle(), -1f, 1f, 1f, data);
    }

    public static class KamiLaserData{
        public Cons2<KamiLaserData, Bullet> update = (data, b) -> {}, init = (data, bullet) -> {};
        public float x, y, x2, y2;
        public float width;
    }
}
