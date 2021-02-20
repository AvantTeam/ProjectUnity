package unity.entities.abilities;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.content.*;

import static mindustry.Vars.*;

public class SlashAbility extends BaseAbility{
    public float slashDistance = 18f * tilesize;

    public Effect teleportEffect = Fx.lightningShoot;
    public Effect postTeleportEffect = Fx.lancerLaserShootSmoke;
    public Sound teleportSound = Sounds.spark;
    public StatusEffect boostedEffect = UnityStatusEffects.boosted;

    public BulletType slashBullet = UnityBullets.teleportLightning;
    public Effect slashEffect = UnityFx.slashEffect;

    public SlashAbility(Boolf<Unit> able){
        super(able, true, true);
    }

    @Override
    public void use(Unit unit, float x, float y){
        Teamc target = Units.closestEnemy(unit.team, unit.x, unit.y, 35f * tilesize, u -> true);
        float dir = unit.rotation;

        if(target != null){
            dir = Tmp.v1.set(target.getX() - unit.x, target.getY() - unit.y).angle();
        }

        teleportEffect.at(unit.x, unit.y, dir);

        Bullet b = slashBullet.create(unit, unit.team, unit.x, unit.y, dir, -1f, 1f, 1f, null);
        Damage.collideLine(b, unit.team, slashEffect, unit.x, unit.y, dir, 16f * tilesize, true);

        unit.apply(boostedEffect, 30f);

        Vec2 pos = Tmp.v1.set(slashDistance, 0f).setAngle(dir);
        unit.set(pos.x + unit.x, pos.y + unit.y);

        if(unit.isPlayer()){
            unit.getPlayer().snapInterpolation();
            if(headless){
                unit.getPlayer().snapSync();
            }
        }else{
            unit.snapInterpolation();
        }

        if(mobile && !headless && unit.getPlayer() == player){
            Core.camera.position.set(pos.x + unit.x, pos.y + unit.y);
        }

        if(!headless){
            teleportSound.at(pos.x + unit.x, pos.y + unit.y, 1.6f);
            postTeleportEffect.at(unit.x, unit.y, (dir + 180f) % 360f);
        }

        unit.vel.trns(dir, 4f);
    }
}
