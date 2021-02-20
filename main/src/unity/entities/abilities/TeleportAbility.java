package unity.entities.abilities;

import arc.*;
import arc.audio.*;
import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import unity.content.*;

import static mindustry.Vars.*;

public class TeleportAbility extends BaseAbility{
    public float teleportDistance = 10f * tilesize;

    public Sound teleportSound = Sounds.lasershoot;
    /** {@code e.data} is <code>{@link Position}[]{before, after}</code>  */
    public Effect teleportEffect = Fx.none;
    /** {@code e.data} is {@link Unit}. Called in each position before and after teleporting */
    public Effect teleportPosEffect = UnityFx.teleportPos;

    public TeleportAbility(Boolf<Unit> able){
        super(able, true, true);
    }

    @Override
    public void use(Unit unit, float x, float y){
        Vec2 pos;
        if(!Float.isNaN(x) && !Float.isNaN(y)){
            pos = Tmp.v1.set(x, y).sub(unit).limit(teleportDistance);
        }else{
            pos = Tmp.v1.trns(
                Mathf.randomSeed((long)(Time.time + unit.id), 360f),
                Mathf.randomSeed((long)(Time.time + 1f + unit.id), unit.hitSize() * 2f, teleportDistance)
            );
        }

        teleportEffect.at(unit.x, unit.y, 0f, new Position[]{new Vec2(unit.x, unit.y), new Vec2(pos).add(unit)});
        teleportPosEffect.at(unit.x, unit.y, unit.rotation, unit.type);
        teleportSound.at(unit);

        unit.trns(pos);
        teleportPosEffect.at(unit.x, unit.y, unit.rotation, unit.type);
        teleportSound.at(unit);

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

        unit.vel.trns(pos.angle(), 4f);
    }
}
