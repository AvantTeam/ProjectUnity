package unity.content;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.graphics.*;

import static unity.content.UnityFx.distortFx;

public class UnityStatusEffects implements ContentList{
    public static StatusEffect disabled, plasmaed, radiation, reloadFatigue, blueBurn, molten, tpCoolDown, teamConverted, boosted, distort, plated;

    @Override
    public void load(){
        disabled = new StatusEffect("diabled"){{
            reloadMultiplier = 0f;
            speedMultiplier = 0f;
        }};

        plasmaed = new StatusEffect("plasmaed"){{
            effectChance = 0.15f;
            damage = 0.5f;
            reloadMultiplier = 0.8f;
            healthMultiplier = 0.9f;
            damageMultiplier = 0.8f;
            effect = UnityFx.plasmaedEffect;
        }};

        radiation = new StatusEffect("radiation"){
            {
                damage = 1.6f;
            }

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

        teamConverted = new StatusEffect("team-converted"){{
            healthMultiplier = 0.35f;
            damageMultiplier = 0.4f;
            permanent = true;
            effect = UnityFx.teamConvertedEffect;
            color = Color.valueOf("a3e3ff");
        }};

        boosted = new StatusEffect("boosted"){{
            color = Pal.lancerLaser;
            effect = Fx.none;
            speedMultiplier = 2f;
        }};

        distort = new StatusEffect("distort"){
        {
            speedMultiplier = 0.35f;
            color = Pal.lancerLaser;
            effect = distortFx;
        }

        public void update(Unit unit, float time){
            if(damage > 0){
                unit.damageContinuousPierce(damage);
            }else if(damage < 0){ //heal unit
                unit.heal(-1f * damage * Time.delta);
            }

            if(effect != Fx.none && Mathf.chanceDelta(effectChance)){
                Tmp.v1.rnd(unit.type.hitSize /2f);
                effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, 45f);
            }
        }
        };

        plated = new StatusEffect("plated"){{
            speedMultiplier = 0.75f;
            damageMultiplier = 1.5f;
            healthMultiplier = 2f;
            reloadMultiplier = 1.2f;
            permanent = true;
            effect = UnityFx.plated;
            effectChance = 0.4f;
        }

        public void update(Unit unit, float time){
            if(Mathf.chanceDelta(effectChance) && (!unit.isFlying() || unit.moving())){
                Tmp.v1.rnd(unit.type.hitSize / 2f);
                effect.at(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y, 0, Mathf.chance(0.5f) ? Pal.thoriumPink : Items.titanium.color, Mathf.random() + 0.1f);
            }
        }};
    }
}
