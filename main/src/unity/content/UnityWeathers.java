package unity.content;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weather.*;
import unity.type.*;

import static mindustry.Vars.*;

public class UnityWeathers{
    public static Weather
    timeStorm,
    debrisStorm;

    public static void load(){
        timeStorm = new ParticleWeather("time-anomoly"){ // I wonder who spelt this
            public final float spawnChance = 0.02f;
            public final float minDistSize = 2f * tilesize;
            public final float maxDistSize = 6f * tilesize;

            {
                duration = 1.5f * Time.toMinutes;
                noiseLayerSclM = 0.8f;
                noiseLayerAlphaM = 0.7f;
                noiseLayerSpeedM = 2f;
                noiseLayerSclM = 0.6f;
                baseSpeed = 0.05f;
                color = noiseColor = Pal.lancerLaser;
                noiseScale = 1100f;
                noisePath = "fog";
                drawParticles = false;
                drawNoise = true;
                useWindVector = false;
                xspeed = 2f;
                yspeed = -0.5f;
                opacityMultiplier = 0.47f;
            }

            @Override
            public void update(WeatherState state){
                if(Mathf.chanceDelta(state.intensity * spawnChance)){
                    UnityBullets.distField.create(null, Team.derelict, Mathf.random(world.unitWidth()), Mathf.random(world.unitHeight()), 0f, 1f, 1f, 1f, new Float[]{Mathf.random(minDistSize, maxDistSize), 2f});
                }
                super.update(state);
            }
        };

        debrisStorm = new DebrisWeather("debris-storm"){{
            sizeMax = 32f;
            sizeMin = 10f;
            density = 100000f;
            xspeed = 18f;
            yspeed = -12f;
            color = Color.darkGray;
            opacityMultiplier = 2f;

            sound = Sounds.windhowl;
            soundVol = 0f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.02f;
        }};
    }
}
