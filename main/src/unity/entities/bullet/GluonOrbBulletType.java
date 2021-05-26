package unity.entities.bullet;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;

public class GluonOrbBulletType extends BasicBulletType{
    public float force = 8f, scaledForce = 7f, beamStroke = 0.7f, radius = 80f;

    protected TextureRegion laser, laserEnd;

    public GluonOrbBulletType(float speed, float damage){
        super(speed, damage);

        pierce = pierceBuilding = true;
        despawnEffect = hitEffect = Fx.none;
    }

    @Override
    public void load(){
        super.load();

        laser = Core.atlas.find("laser");
        laserEnd = Core.atlas.find("laser-end");
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        b.data = new GluonOrbData();
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.timer(0, 2f + b.fslope() * 1.5f)){
            UnityFx.lightHexagonTrail.at(b.x, b.y, 1f + b.fslope() * 4f);
        }

        if(b.timer(1, 2f) && b.data instanceof GluonOrbData d){
            d.units.clear();

            Units.nearbyEnemies(b.team, b.x - radius, b.y - radius, radius * 2f, radius * 2f, u -> {
                if(u != null && Mathf.within(b.x, b.y, u.x, u.y, radius)){
                    d.units.add(u);
                }
            });

            Damage.damage(b.team, b.x, b.y, hitSize, damage);
        }

        if(b.data instanceof GluonOrbData d){
            d.units.each(u -> {
                if(u.dead) return;
                float ang = u.angleTo(b);

                if(Angles.angleDist(b.rotation(), ang) < 90f){
                    Tmp.v1.trns(ang, force + ((1f - (u.dst(b) / radius)) * scaledForce * (u.isFlying() ? 1.5f : 1f))).scl(20f);

                    u.impulse(Tmp.v1);
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        Draw.color(Pal.lancerLaser);

        if(b.data instanceof GluonOrbData d){
            d.units.each(u -> {
                if(u != null){
                    Drawf.laser(b.team, laser, laserEnd, b.x, b.y, u.x, u.y, beamStroke);
                }
            });
        }

        Fill.circle(b.x, b.y, 6f + b.fout() * 1.5f);
        Draw.color(Color.white);
        Fill.circle(b.x, b.y, 4.5f + b.fout());
    }

    public static class GluonOrbData{ //Couldn't just make `b.data` a Seq<Unit> because of casting from Object issues or whatever idk Glenn could probably do this better.
        Seq<Unit> units = new Seq<>();
    }
}