package unity.entities.units;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.content.*;

public class EndInvisibleUnit extends UnitEntity{
    protected boolean isInvisible = false;
    public float invFrame = 0f;
    public float alphaLerp = 0f;

    @Override
    public void update(){
        super.update();

        invFrame += Time.delta;

        if(!isShooting && health > maxHealth / 2f){
            alphaLerp = Mathf.lerpDelta(alphaLerp, 1f, 0.1f);
        }else{
            alphaLerp = Mathf.lerpDelta(alphaLerp, 0f, 0.1f);
        }

        if(alphaLerp < 0.5f){
            setVisible();
        }else{
            setInvisible();
        }
    }

    void setInvisible(){
        if(!isInvisible){
            Groups.unit.remove(this);
            isInvisible = true;
        }
    }

    void setVisible(){
        if(isInvisible){
            Groups.unit.add(this);
            isInvisible = false;
        }
    }

    @Override
    public void damage(float amount){
        if(invFrame < 15) return;
        invFrame = 0f;
        float trueDamage = Math.min(amount, 700f);
        super.damage(trueDamage);
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(4);
    }
}
