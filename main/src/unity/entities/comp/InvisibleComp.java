package unity.entities.comp;

import arc.math.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.util.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class InvisibleComp implements Unitc, Unintersectablec{
    @SyncLocal @ReadOnly boolean invisible;
    @ReadOnly transient float disabledTime = 120f;
    @ReadOnly transient Interval scanInterval = new Interval(2);
    @SyncLocal @SyncField(true) float alphaLerp;

    @Import Team team;
    @Import float x, y, hitSize, health, maxHealth;
    @Import boolean isShooting;

    @Override
    public void update(){
        disabledTime = Math.max(disabledTime - Time.delta, 0f);
        
        if(scanInterval.get(0, 5f) && invisible){
            hitbox(Tmp.r1);
            Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
                if(b.team != team) disabledTime = 1.2f * 60;
            });
        }
        if(scanInterval.get(1, 30f)){
            float size = hitSize * 2.5f;
            Tmp.r1.setCentered(x, y, size * 2f);
            Groups.unit.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, u -> {
                if(u.team != team && Mathf.within(x, y, u.x, u.y, size)){
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

        invisible = alphaLerp >= 0.5f;
    }

    @Replace
    @Override
    public void damage(float amount){
        disabledTime = Math.max(disabledTime, Math.max(1.4f * 60f, amount / 25f));
    }

    @Override
    public boolean intersects(){
        return !invisible;
    }
}
