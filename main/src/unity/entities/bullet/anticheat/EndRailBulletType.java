package unity.entities.bullet.anticheat;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.effects.*;
import unity.graphics.*;
import unity.mod.*;
import unity.util.*;

public class EndRailBulletType extends AntiCheatBulletTypeBase{
    public Color[] colors = new Color[]{UnityPal.scarColorAlpha, UnityPal.scarColor, UnityPal.endColor, Color.black};
    public float length = 340f;
    public float collisionWidth = 4f;
    public Effect updateEffect = TrailFx.endRailTrail;
    public float pointEffectSpace = 20f;
    public float pierceDamageFactor = 1f;

    static float len, dam;

    public EndRailBulletType(){
        speed = 0f;
        pierceBuilding = true;
        pierce = true;
        reflectable = false;
        absorbable = false;
        hittable = false;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        collides = false;
        lifetime = 20f;
    }

    @Override
    public float calculateRange(){
        return length;
    }

    @Override
    public void init(){
        super.init();
        drawSize = length * 2f;
    }

    @Override
    public void init(Bullet b){
        super.init(b);
        Tmp.v1.trns(b.rotation(), length).add(b);

        dam = damage;
        len = length;

        Utils.collideLineRawEnemy(b.team, b.x, b.y, Tmp.v1.x, Tmp.v1.y, collisionWidth, (build, direct) -> {
            if(direct && dam > 0f){
                float lh = build.health;
                hitBuildingAntiCheat(b, build, dam - damage);
                dam -= lh * pierceDamageFactor;
                if(dam <= 0f) len = b.dst(build);
            }
            return dam <= 0f;
        }, unit -> {
            if(dam > 0f){
                float lh = unit.health;
                boolean wasAdded = unit.isAdded();
                hitUnitAntiCheat(b, unit, dam - damage);
                if(unit.dead && wasAdded){
                    if(Vars.renderer.animateShields){
                        SpecialFx.fragmentation.at(unit.x, unit.y, unit.angleTo(b), unit);
                    }else{
                        unit.type.deathExplosionEffect.at(unit.x, unit.y, unit.bounds() / 2f / 8f);
                        unit.type.deathSound.at(unit);
                    }
                    if(unit.isAdded()) AntiCheat.annihilateEntity(unit, false);
                }
                dam -= lh * pierceDamageFactor;
                if(dam <= 0f) len = b.dst(unit);
            }
            return dam <= 0f;
        }, (ex, ey) -> hit(b, ex, ey), true);

        Vec2 nor = Tmp.v1.trns(b.rotation(), 1f).nor();
        for(float i = 0; i <= len; i += pointEffectSpace){
            updateEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation());
        }
        b.fdata = len;
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void draw(Bullet b){
        float stroke = 2f * 1.5f * b.fout();
        Vec2 v = Tmp.v1.trns(b.rotation(), b.fdata).add(b);

        for(Color c : colors){
            Draw.color(c);
            Drawf.tri(b.x, b.y, stroke * collisionWidth, stroke * 1.22f * length * 0.02f, b.rotation() + 180f);
            Lines.stroke(stroke * collisionWidth);
            Lines.line(b.x, b.y, v.x, v.y);
            Drawf.tri(v.x, v.y, stroke * collisionWidth, stroke * 1.22f * length * 0.07f, b.rotation());
            stroke /= 1.5f;
        }
        Draw.reset();
    }
}
