package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import unity.content.*;
import unity.graphics.*;
import unity.util.*;

public class SingularityBulletType extends BasicBulletType{
    public float strength = 0.94f, tileDamage = 150f, radius = 230f, size = 5f;
    public float[] scales = {8.6f, 7f, 5.5f, 4.3f, 4.1f, 3.9f};
    public Color[] colors = colors = new Color[]{Color.valueOf("4787ff80"), Pal.lancerLaser, Color.white, Pal.lancerLaser, UnityPal.lightEffect, Color.black};

    public SingularityBulletType(float damage){
        super(0.001f, damage);

        pierce = pierceBuilding = true;
        collidesTiles = false;
        despawnEffect = UnityFx.singularityDespawn;
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        float interp1 = strength * b.fin(Interp.exp10Out);
        float interp2 = b.fin(Interp.exp10Out);

        Effect.shake(interp2, interp2, b);

        if(b.timer(1, 7f)){
            Utils.trueEachBlock(b.x, b.y, radius, e -> {
                 if(e.isValid() && e.team != b.team){

                     if(e.health < tileDamage || Mathf.within(b.x, b.y, e.x, e.y, (interp2 * size * 3.9f) + e.block.size / 2f)){
                         e.kill();
                         if(!Vars.headless){
                             UnityFx.singularityAttraction.at(b.x, b.y, e.rotation, new SingularityAbsorbEffectData(e.block.icon(Cicon.full), e.x, e.y));
                         }
                     }

                     float dst = Math.abs((Mathf.dst(b.x, b.y, e.x, e.y) / radius) - 1f);

                     e.damage(tileDamage * buildingDamageMultiplier * dst);
                 }
            });
        }

        if(b.timer(2, 2f)){
            Units.nearbyEnemies(b.team, b.x - radius, b.y - radius, 2 * radius, 2 * radius, u -> {
                if(u != null && Mathf.within(b.x, b.y, u.x, u.y, radius)){
                    float dst = Math.abs((Mathf.dst(b.x, b.y, u.x, u.y) / radius) - 1f) * interp1;
                    float ang = Angles.angle(u.x, u.y, b.x, b.y);

                    Tmp.v1.trns(ang, dst * (u.isFlying() ? 1.5f : 1f));

                    u.impulse(Tmp.v1);

                    if(Mathf.within(b.x, b.y, u.x, u.y, (interp2 * size * 3.9f) + u.hitSize / 2f)){
                        u.damage(120);
                    };
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        float interp = strength * b.fin(Interp.exp10Out);

        for(int i = 0; i < colors.length; i++){
            Draw.color(colors[i]);
            Fill.circle(b.x + Mathf.range(1), b.y + Mathf.range(1), interp * size * scales[i]);
        }
        Draw.color();
    }

    public static class SingularityAbsorbEffectData{
        public TextureRegion region;
        public float x, y;

        public SingularityAbsorbEffectData(TextureRegion region, float x, float y){
            this.region = region;
            this.x = x;
            this.y = y;
        }
    }
}