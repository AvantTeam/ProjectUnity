package unity.entities.legs;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.world.blocks.environment.*;
import unity.entities.legs.CLegType.*;

public class CLegGroup{
    public float totalLength, moveSpace, baseRotation;
    public int deep = 0;
    public CLeg[] legs;
    public ClegGroupType type;
    public Floor lastFloor;

    public void init(ClegGroupType type){
        this.type = type;

        float l = 9000f;
        for(CLegType<? extends CLeg> leg : type.legs){
            l = Math.min(l, leg.length());
        }
        int div = Math.max((type.legs.length * 2) / type.legGroupSize, 2);
        moveSpace = l / 1.6f / div * type.moveSpacing;

        int len = type.legs.length;
        legs = new CLeg[len * 2];
        for(int i = 0; i < len * 2; i++){
            //int id = i < len ? i : len - ((i - len) + 1);
            int s = i / 2;
            boolean flip = i % 2 == 0;
            //CLeg leg = type.legs[id].create();
            CLeg leg = type.legs[s].create();
            leg.side = flip;
            leg.id = (i % 2 == 0) ? s : ((len * 2) - 1) - s;
            legs[i] = leg;
        }
    }

    public void reset(Unit unit){
        baseRotation = unit.rotation;
        for(CLeg leg : legs){
            leg.reset(this, unit);
        }
    }

    public void update(Unit unit){
        if(unit.deltaLen() > 0.001f){
            baseRotation = Angles.moveToward(baseRotation, Angles.angle(unit.deltaX, unit.deltaY), type.baseRotateSpeed);
        }
        totalLength += unit.deltaLen() / moveSpace;
        deep = 0;
        for(CLeg leg : legs){
            leg.update(unit, baseRotation, unit.moving(), this);
        }
        if(!sinking()){
            lastFloor = null;
        }
    }

    public void draw(Unit unit){
        unit.type.applyColor(unit);
        Tmp.c3.set(Draw.getMixColor());
        for(CLeg leg : legs){
            leg.drawShadow(unit, this);
        }
        for(CLeg leg : legs){
            leg.draw(unit, this);
        }
        if(type.baseRegion.found()) Draw.rect(type.baseRegion, unit.x, unit.y, baseRotation - 90f);
        Draw.reset();
    }

    public boolean sinking(){
        return deep >= legs.length;
    }
}
