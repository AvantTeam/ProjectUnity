package unity.entities.legs;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.environment.*;

abstract class CLeg{
    protected static Vec2 v1 = new Vec2(), v2 = new Vec2();

    public int id, group;
    public boolean moving;
    public float stage;

    public CLegType<?> type;
    public boolean side;

    protected Vec2 foot = new Vec2();

    public void reset(CLegGroup g, Unit unit){
        int side = Mathf.sign(this.side);
        v1.trns(g.baseRotation - 90f, (type.x + type.targetX) * side, type.y + type.targetY).add(unit);
        foot.set(v1);
    }

    public void update(Unit unit, float baseRotation, boolean uMoving, CLegGroup g){
        int side = Mathf.sign(this.side);
        int div = Math.max(2, g.legs.length / g.type.legGroupSize);
        v1.trns(baseRotation - 90f, type.x * side, type.y).add(unit);

        float stageF = g.totalLength;
        int stage = (int)stageF;
        int group = stage % div;

        float trns = g.moveSpace * 0.85f * type.legForwardScl;

        moving = id % div == group;
        this.stage = uMoving ? stageF % 1f : Mathf.lerpDelta(stage, 0f, 0.1f);

        v2.trns(baseRotation - 90f, (type.x + type.targetX) * side, type.y + type.targetY + trns).add(unit);

        updateLeg(unit, g, moving, v1.x, v1.y, v2.x, v2.y);

        Floor floor = Vars.world.floorWorld(foot.x, foot.y);
        if(floor.isDeep()){
            g.deep++;
            g.lastFloor = floor;
        }
        if(this.group != group){
            if(!moving && id % div == this.group){
                step(unit, floor);
            }
            this.group = group;
        }
    }

    abstract void updateLeg(Unit unit, CLegGroup legGroup, boolean moving, float baseX, float baseY, float targetX, float targetY);

    public void step(Unit unit, Floor floor){
        if(floor.isLiquid){
            floor.walkEffect.at(foot.x, foot.y, unit.type.rippleScale, floor.mapColor);
            floor.walkSound.at(foot.x, foot.y, 1f, floor.walkSoundVolume);
        }else{
            Fx.unitLandSmall.at(foot.x, foot.y, unit.type.rippleScale, floor.mapColor);
        }

        if(unit.type.stepShake > 0f){
            Effect.shake(unit.type.stepShake, unit.type.stepShake, foot);
        }
    }

    protected Vec2 setBase(Unit unit, CLegGroup legGroup){
        int side = Mathf.sign(this.side);
        return v1.trns(legGroup.baseRotation - 90f, type.x * side, type.y).add(unit);
    }

    abstract void draw(Unit unit, CLegGroup legGroup);

    void drawShadow(Unit unit, CLegGroup legGroup){
        Vec2 base = setBase(unit, legGroup);

        float ssize = type.footRegion.width * Draw.scl * 1.5f;
        float invDrown = 1f - unit.drownTime;
        float ang = base.angleTo(foot);

        Drawf.shadow(foot.x, foot.y, ssize, invDrown);

        if(moving && unit.type.shadowElevation > 0){
            float scl = unit.type.shadowElevation * invDrown;
            float elev = Mathf.slope(1f - Mathf.clamp(stage)) * scl;
            Draw.color(Pal.shadow);
            Draw.rect(type.footRegion, foot.x + UnitType.shadowTX * elev, foot.y + UnitType.shadowTY * elev, ang);
            Draw.color();
        }
    }
}
