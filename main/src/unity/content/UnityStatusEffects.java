package unity.content;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import unity.graphics.UnityPal;

public class UnityStatusEffects implements ContentList{
    public static StatusEffect radiation, reloadFatigue, blueBurn, molten, tpCoolDown;

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

        blueBurn = new StatusEffect("blue-burn"){{
            damage = 0.14f;
            effect = UnityFx.blueBurnEffect;
            init(() -> {
                opposite(StatusEffects.wet, StatusEffects.freezing);
                trans(StatusEffects.tarred, (unit, time, newTime, result) -> {
                    unit.damagePierce(8f);
                    effect.at(unit.x() + Mathf.range(unit.bounds() / 2), unit.y() + Mathf.range(unit.bounds() / 2));
                    result.set(this, Math.min(time + newTime, 400f));
                });
            });
        }};

        reloadFatigue = new StatusEffect("reload-fatigue"){{
            reloadMultiplier = 0.75f;
        }};

        molten = new StatusEffect("molten"){{
            color = UnityPal.lavaColor;
            speedMultiplier = 0.6f;
            healthMultiplier = 0.5f;
            damage = 1f;
            effect = UnityFx.ahhimaLiquidNow;
        }};

        tpCoolDown = new StatusEffect("tpcooldonw"){{
            color = UnityPal.diriumColor2;
            effect = Fx.none;
        }};
    }
}
