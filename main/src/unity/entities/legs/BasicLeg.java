package unity.entities.legs;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static arc.Core.*;

public class BasicLeg extends CLeg{
    float jointX;
    float jointY;

    @Override
    public void reset(CLegGroup g, Unit unit){
        super.reset(g, unit);
        BasicLegType type = (BasicLegType)this.type;
        int side = Mathf.sign(this.side);

        float scl = type.baseLength / (type.baseLength + type.endLength);
        v1.trns(g.baseRotation - 90f, (type.targetX + type.x) * side, (type.targetY + type.y)).scl(scl).add(unit);
        jointX = v1.x;
        jointY = v1.y;
    }

    @Override
    void updateLeg(Unit unit, CLegGroup legGroup, boolean moving, float baseX, float baseY, float targetX, float targetY){
        float stageF = legGroup.totalLength;
        BasicLegType type = (BasicLegType)this.type;
        Vec2 j = Tmp.v1.set(jointX, jointY).sub(baseX, baseY).limit(type.baseLength * legGroup.type.maxStretch).add(baseX, baseY);
        jointX = j.x;
        jointY = j.y;

        foot.sub(baseX, baseY).limit(type.length() * legGroup.type.maxStretch).add(baseX, baseY);

        Vec2 jDest = Tmp.v2, end = Tmp.v3;
        InverseKinematics.solve(type.baseLength, type.endLength, end.set(foot).sub(baseX, baseY), side == type.flipped, jDest);
        jDest.add(baseX, baseY);

        if(moving){
            float fract = stageF % 1f;

            foot.lerpDelta(targetX, targetY, fract);
            Tmp.v1.set(jointX, jointY).lerpDelta(jDest, fract / 2f);
            jointX = Tmp.v1.x;
            jointY = Tmp.v1.y;
        }
        Tmp.v1.set(jointX, jointY).lerpDelta(jDest, unit.type.legSpeed / 4f);
        jointX = Tmp.v1.x;
        jointY = Tmp.v1.y;
    }

    @Override
    void draw(Unit unit, CLegGroup legGroup){
        BasicLegType type = (BasicLegType)this.type;
        Vec2 base = setBase(unit, legGroup),
        off = Tmp.v2.setZero();
        int flips = Mathf.sign(side == type.flipped);

        if(type.endOffset != 0f){
            off.set(foot).sub(jointX, jointY).setLength(type.endOffset);
        }

        float ang = base.angleTo(foot);

        Draw.mixcol(Tmp.c3, Tmp.c3.a);

        Draw.rect(type.footRegion, foot.x, foot.y, ang);

        Lines.stroke(type.baseRegion.height * Draw.scl * flips);
        Lines.line(type.baseRegion, base.x, base.y, jointX, jointY, false);

        Lines.stroke(type.endRegion.height * Draw.scl * flips);
        Lines.line(type.endRegion, jointX + off.x, jointY + off.y, foot.x, foot.y, false);

        if(type.kneeJoint.found()){
            Draw.rect(type.kneeJoint, jointX, jointY);
        }

        if(type.baseJoint.found()){
            Draw.rect(type.baseRegion, base.x, base.y, legGroup.baseRotation - 90f);
        }
    }

    public static class BasicLegType extends CLegType<BasicLeg>{
        public float baseLength = 10f, endLength = 10f;
        public float endOffset = 0f;

        TextureRegion baseRegion, endRegion, baseJoint, kneeJoint;

        public BasicLegType(String name){
            super(BasicLeg::new, name);
        }

        @Override
        public void load(){
            super.load();
            baseRegion = atlas.find(name + "-base");
            endRegion = atlas.find(name + "-end");
            baseJoint = atlas.find(name + "-base-joint");
            kneeJoint = atlas.find(name + "-joint");
        }

        @Override
        public float length(){
            return baseLength + endLength;
        }
    }
}
