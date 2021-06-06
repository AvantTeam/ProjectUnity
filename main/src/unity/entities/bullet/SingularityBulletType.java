package unity.entities.bullet;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.content.*;
import unity.entities.bullet.GluonOrbBulletType.*;
import unity.graphics.*;
import unity.util.*;

public class SingularityBulletType extends BasicBulletType{
    public float force = 8f, scaledForce = 5f, tileDamage = 150f, radius = 230f, size = 5f;
    public float[] scales = {8.6f, 7f, 5.5f, 4.2f, 3.9f};
    public Color[] colors = new Color[]{Color.valueOf("4787ff80"), Pal.lancerLaser, Color.white, Pal.lancerLaser, Color.black};

    public SingularityBulletType(float damage){
        super(0.001f, damage);

        pierce = pierceBuilding = true;
        hitEffect = Fx.none;
        despawnEffect = UnityFx.singularityDespawn;
    }

    @Override
    public void init(Bullet b){
        super.init(b);

        b.data = new GluonOrbData();
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        float interp = b.fin(Interp.exp10Out);

        Effect.shake(interp, interp, b);

        if(b.timer(1, 7f)){
            Utils.trueEachBlock(b.x, b.y, radius, e -> {
                 if(e.isValid() && e.team != b.team){

                     if(e.health < tileDamage || Mathf.within(b.x, b.y, e.x, e.y, (interp * size * 3.9f) + e.block.size / 2f)){
                         e.kill();
                         if(!Vars.headless){
                             UnityFx.singularityAttraction.at(b.x, b.y, e.rotation, new SingularityAbsorbEffectData(e.block.fullIcon, e.x, e.y));
                         }
                     }

                     float dst = Math.abs(1f - (Mathf.dst(b.x, b.y, e.x, e.y) / radius));

                     e.damage(tileDamage * buildingDamageMultiplier * dst);
                 }
            });
        }

        if(b.data instanceof GluonOrbData data){
            if(b.timer(2, 2f)){
                data.units.clear();
                Units.nearbyEnemies(b.team, b.x - radius, b.y - radius, 2 * radius, 2 * radius, u -> {
                    if(u != null && Mathf.within(b.x, b.y, u.x, u.y, radius)){
                        data.units.add(u);

                        if(Mathf.within(b.x, b.y, u.x, u.y, (interp * size * 3.9f) + u.hitSize / 2f)){
                            u.damage(120);
                        }
                    }
                });

                Damage.damage(b.team, b.x, b.y, hitSize, damage);
            }

            data.units.each(u -> {
                if(!u.dead){
                    Tmp.v1.trns(u.angleTo(b), force + ((1f - u.dst(b) / radius) * scaledForce * b.fin(Interp.exp10Out) * (u.isFlying() ? 1.5f : 1f))).scl(20f * Time.delta);

                    u.impulse(Tmp.v1);
                }
            });
        }
    }

    @Override
    public void draw(Bullet b){
        float interp = b.fin(Interp.exp10Out);

        for(int i = 0; i < colors.length; i++){
            Draw.color(colors[i]);
            if(i != 0){
                float extra = (colors.length - (i + 1f)) * (11f / (colors.length - 1f));
                
                UnityDrawf.shiningCircle(b.id, Time.time - i, b.x + Mathf.range(0.5f), b.y + Mathf.range(0.5f), interp * size * scales[i], 6, 30f, 17f + (extra / 2f), (10f + extra) * interp, 60f);
            }else{
                Fill.circle(b.x + Mathf.range(0.5f), b.y + Mathf.range(0.5f), interp * size * scales[i]);
            }
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