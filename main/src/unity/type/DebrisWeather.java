package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.weather.*;

import static mindustry.Vars.*;

public class DebrisWeather extends ParticleWeather{
    public float spawnChance = 0.5f, minSplashRadius = 2f, maxSplashRadius = 10f, minDamage = 50f, maxDamage = 300f;
    public float knockbackChance = 0.005f, minKnockback = 5f, maxKnockback = 15f, knockbackDamageMin = 100f, knockbackDamageMax = 500f;

    public DebrisWeather(String name){
        super(name);
        particleRegion = "unity-debris";
    }

    @Override
    public void update(WeatherState state){
        if(Mathf.chanceDelta(state.intensity * spawnChance)){
            float x = Mathf.random(world.unitWidth()), y = Mathf.random(world.unitHeight());
            Fx.blockExplosionSmoke.at(x, y);
            Fx.blockExplosion.at(x, y);
            Damage.damage(x, y, Mathf.random(minSplashRadius, maxSplashRadius), Mathf.random(minDamage, maxDamage));
        }
        Groups.unit.each(u -> {
            if(Mathf.chanceDelta(state.intensity * knockbackChance)){
                u.impulse(Tmp.v1.trns(Mathf.angle(xspeed, yspeed), Mathf.random(minKnockback, maxKnockback) * 80f));
                u.damage(Mathf.random(knockbackDamageMin, knockbackDamageMax));
            }
        });
        super.update(state);
    }

    @Override
    public void drawParticles(TextureRegion region, Color color,
                              float sizeMin, float sizeMax,
                              float density, float intensity, float opacity,
                              float windx, float windy,
                              float minAlpha, float maxAlpha,
                              float sinSclMin, float sinSclMax, float sinMagMin, float sinMagMax){
        rand.setSeed(0);
        Tmp.r1.setCentered(Core.camera.position.x, Core.camera.position.y, Core.graphics.getWidth() / renderer.minScale(), Core.graphics.getHeight() / renderer.minScale());
        Tmp.r1.grow(sizeMax * 1.5f);
        Core.camera.bounds(Tmp.r2);
        int total = (int)(Tmp.r1.area() / density * intensity);
        Draw.color(color, opacity);

        for(int i = 0; i < total; i++){
            float scl = rand.random(0.5f, 1f);
            float scl2 = rand.random(0.5f, 1f);
            float size = rand.random(sizeMin, sizeMax);
            float x = (rand.random(0f, world.unitWidth()) + Time.time * windx * scl2);
            float y = (rand.random(0f, world.unitHeight()) + Time.time * windy * scl);
            float alpha = rand.random(minAlpha, maxAlpha);
            float rotation = rand.random(0f, 360f);

            x -= Tmp.r1.x;
            y -= Tmp.r1.y;
            x = Mathf.mod(x, Tmp.r1.width);
            y = Mathf.mod(y, Tmp.r1.height);
            x += Tmp.r1.x;
            y += Tmp.r1.y;

            if(Tmp.r3.setCentered(x, y, size).overlaps(Tmp.r2)){
                Draw.alpha(alpha * opacity);
                Draw.rect(region, x, y, size, size, rotation);
            }
        }
    }
}
