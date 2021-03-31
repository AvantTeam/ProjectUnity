package unity.entities.units;

import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import unity.content.*;
import unity.util.*;

public class EndInvisibleUnit extends UnitEntity implements AntiCheatBase{
    protected boolean isInvisible = false;
    protected float disabledTime = 0f;
    protected Interval scanInterval = new Interval(2);
    protected float invFrame = 0f;
    public float alphaLerp = 0f;
    protected float lastHealth = 0f;

    @Override
    public float lastHealth(){
        return lastHealth;
    }

    @Override
    public void lastHealth(float v){
        lastHealth = v;
    }

    @Override
    public void overrideAntiCheatDamage(float v){
        if(invFrame < 15f) return;
        invFrame = 0f;
        hitTime = 1f;
        lastHealth -= v;
        super.damage(v);
    }

    @Override
    public void add(){
        if(added) return;
        super.add();
        lastHealth = health;
    }

    @Override
    public void update(){
        if(health < lastHealth) health = lastHealth;
        lastHealth = health;

        super.update();

        invFrame += Time.delta;
        disabledTime = Math.max(disabledTime - Time.delta, 0f);

        if(scanInterval.get(10f) && isInvisible){
            hitbox(Tmp.r1);
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
                if(b.team != team) disabledTime = 1.2f * 60;
            });
        }
        if(scanInterval.get(1, 30f)){
            float size = hitSize * 3f;
            Tmp.r1.setCentered(x, y, size * 2f);
            Groups.unit.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, u -> {
                if(u.team != team && Mathf.within(x, y, u.x, u.y, hitSize * 3f)){
                    disabledTime = 1.2f * 60;
                }
            });
            if(Utils.hasBuilding(x, y, size, build -> build.team != team)) disabledTime = 1.2f * 60f;
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
            if(physref != null){
                physref.x = x;
                physref.y = y;

                physref.body.x = x;
                physref.body.y = y;
            }
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

    protected void superDamage(float amount){
        super.damage(amount);
    }

    @Override
    public void damage(float amount){
        if(invFrame < 15) return;
        invFrame = 0f;
        float trueDamage = Math.min(amount, 700f);
        disabledTime = Math.max(1.4f * 60, trueDamage / 25f);
        lastHealth -= trueDamage;
        super.damage(trueDamage);
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(4);
    }
}
