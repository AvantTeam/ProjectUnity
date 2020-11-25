package unity.content;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.ctype.ContentList;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class UnityStatusEffects implements ContentList{
    public static StatusEffect radiation, reloadFatigue;

    @Override
    public void load(){
        radiation = new StatusEffect("radiation"){
            //TODO create new class?
            @Override
            public void update(Unit unit, float time){
                super.update(unit, time);
                if(Mathf.chanceDelta(0.008f * Mathf.clamp(time / 120f))) unit.damage(unit.maxHealth * 0.125f);
                for(int i = 0; i < unit.mounts.length; i++){
                    float strength = Mathf.clamp(time / 120f);
                    WeaponMount temp = unit.mounts[i];
                    if(temp == null) continue;
                    if(Mathf.chanceDelta(0.12f)) temp.reload = Math.min(temp.reload + Time.delta * 1.5f * strength, temp.weapon.reload);
                    temp.rotation += Mathf.range(12f * strength);
                }
            }

            {
                damage = 1.6f;
            }
        };
        reloadFatigue = new StatusEffect("reload-fatigue"){
            {
                reloadMultiplier = 0.75f;
            }
        };
    }
}
