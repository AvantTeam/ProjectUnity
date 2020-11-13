package unity.content;

import mindustry.ctype.ContentList;
import mindustry.type.StatusEffect;

public class UnityStatusEffects implements ContentList{
    public static StatusEffect reloadFatigue;

    @Override
    public void load(){
        reloadFatigue = new StatusEffect("reload-fatigue"){
            {
                reloadMultiplier = 0.75f;
            }
        };
    }
}
