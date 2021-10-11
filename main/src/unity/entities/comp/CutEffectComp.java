package unity.entities.comp;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.entities.effects.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {CutEffectc.class}, serialize = false)
@EntityComponent(base = true)
abstract class CutEffectComp implements Drawc, Rotc, Hitboxc, Timedc{
    Drawc other;
    Seq<CutEffects> stencils;
    Vec2 velocity = new Vec2();
    float originX, originY, angularVel;
    float drag = 0.01f;
    boolean removed = false;

    @Import float x, y, rotation;

    float z(){
        if(other instanceof Unit u){
            UnitType type = u.type;
            return u.elevation > 0.5f ? (type.lowAltitude ? Layer.flyingUnitLow : Layer.flyingUnit) : type.groundLayer + Mathf.clamp(u.hitSize / 4000f, 0, 0.01f);
        }
        return Layer.blockBuilding;
    }

    @Override
    public void update(){
        x += velocity.x * Time.delta;
        y += velocity.y * Time.delta;
        rotation += angularVel;
        velocity.scl(1f - (drag * Time.delta));
        angularVel *= (1f - (drag * Time.delta));
    }

    @Override
    public void draw(){
        CutEffects.draw(self());
    }

    @Override
    @Replace
    public float clipSize(){
        return other.clipSize() * 1.5f;
    }

    @Override
    public void add(){
        CutEffects.group.add(self());
        originX = x;
        originY = y;
    }

    @Override
    public void remove(){
        CutEffects.group.remove(self());
    }

    @Insert(value = "update()", block = Timedc.class)
    void despawn(){
        if(other instanceof Unit unit){
            unit.type.deathExplosionEffect.at(x, y, hitSize() / 2f);
        }else{
            Fx.dynamicExplosion.at(x, y, hitSize() / 2f);
        }
    }
}
