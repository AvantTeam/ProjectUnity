package unity.entities.units;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.content.*;

public class EndInvisibleUnit extends UnitEntity{
    protected boolean isInvisible = false;
    protected float disabledTime = 0f;
    protected Interval scanInterval = new Interval();
    public float invFrame = 0f;
    public float alphaLerp = 0f;

    @Override
    public void update(){
        super.update();

        invFrame += Time.delta;
        disabledTime = Math.max(disabledTime - Time.delta, 0f);

        if(scanInterval.get(5f) && isInvisible){
            hitbox(Tmp.r1);
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
                if(b.team != team) invFrame = 1.2f * 60;
            });
        }

        if(!isShooting && health > maxHealth / 2f && disabledTime <= 0f){
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
        disabledTime = Math.max(1.4f * 60, trueDamage / 25f);
        super.damage(trueDamage);
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(4);
    }
}
