package unity.entities.bullet.anticheat;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import unity.graphics.*;

public class VoidAreaBulletType extends AntiCheatBulletTypeBase{
    public float fadeInTime = 15f, fadeOutTime = 15f;
    public float radius = 150f;

    public VoidAreaBulletType(float damage){
        super(0f, damage);
        collides = false;
        collidesTiles = false;
        despawnEffect = hitEffect = Fx.none;
        layer = Layer.flyingUnit + 1f;
        keepVelocity = false;
    }

    @Override
    public void update(Bullet b){
        if(b.timer(1, 5f)){
            float fin = Mathf.clamp(b.time / fadeInTime) * Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (lifetime - fadeOutTime)) / fadeOutTime : 1f);
            Units.nearbyEnemies(b.team, b.x, b.y, radius * fin, u -> hitUnitAntiCheat(b, u));
            Vars.indexer.allBuildings(b.x, b.y, radius * fin, building -> {
                if(building.team != b.team){
                    hitBuildingAntiCheat(b, building);
                }
            });
        }
    }

    @Override
    public void drawLight(Bullet b){

    }

    @Override
    public void draw(Bullet b){
        float fin = Mathf.clamp(b.time / fadeInTime) * Mathf.clamp(b.time > b.lifetime - fadeOutTime ? 1f - (b.time - (lifetime - fadeOutTime)) / fadeOutTime : 1f);
        float osc = Mathf.absin(b.time, 8f, 1f);
        Tmp.c1.set(UnityPal.scarColor).lerp(UnityPal.endColor, osc);

        Draw.color(Tmp.c1);
        Draw.blend(UnityBlending.shadowRealm);
        Fill.circle(b.x, b.y, fin * radius);
        Draw.blend();
        Lines.stroke(4f - osc * 1.5f);
        Lines.circle(b.x, b.y, fin * radius);
        Draw.reset();
    }
}
