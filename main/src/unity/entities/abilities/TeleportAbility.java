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

/** @author GlennFolker */
public class TeleportAbility<T extends Teamc & Hitboxc> extends BaseAbility{
    public float teleportDistance;

    public Sound teleportSound = Sounds.lasershoot;
    /** {@code e.data} is <code>{@link Position}[]{before, after}</code>  */
    public Effect teleportEffect = Fx.none;
    /** {@code e.data} is {@link Unit}. Called in each position before and after teleporting */
    public Effect teleportPosEffect = UnityFx.teleportPos;

    protected Func<Unit, T> teleportAble;
    protected @Nullable T toAvoid;

    public TeleportAbility(Func<Unit, T> teleportAble, float teleportDistance){
        super(unit -> teleportAble.get(unit) != null, true, true);
        this.teleportAble = teleportAble;
        this.teleportDistance = teleportDistance;
    }

    @Override
    public boolean able(Unit unit){
        return super.able(unit) && (toAvoid = teleportAble.get(unit)) != null;
    }

    @Override
    public void use(Unit unit, float x, float y){
        super.use(unit, x, y);

        Vec2 pos;
        if(!Float.isNaN(x) && !Float.isNaN(y)){
            pos = Tmp.v1.set(x, y).sub(unit).limit(teleportDistance);
        }else if(toAvoid != null){
            if(toAvoid instanceof Velc vel){
                int i = Mathf.randomSeed((long)(Time.time + unit.id)) > 0.5f ? 1 : -1;
                pos = Tmp.v1.trns(
                    vel.vel().angle() - 90,
                    i * Math.min(unit.dst(vel) * 2f, teleportDistance)
                );
            }else{
                pos = Tmp.v1.set(unit).sub(toAvoid)
                    .rotate(Mathf.randomSeed((long)(Time.time + unit.id), 360f))
                    .setLength(Math.min(Tmp.v1.len() * 2f, teleportDistance));
            }
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

        if(mobile && !headless && unit.isLocal()){
            Core.camera.position.set(pos.x + unit.x, pos.y + unit.y);
        }

        unit.vel.trns(pos.angle(), 4f);
    }
}
