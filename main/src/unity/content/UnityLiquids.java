package unity.content;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.ctype.ContentList;
import mindustry.game.EventType.Trigger;
import mindustry.type.Liquid;
import unity.graphics.UnityPal;

public class UnityLiquids implements ContentList{
    private static final Color temp = new Color(0f, 0f, 0f, 1f), temp2 = temp.cpy();
    public static Liquid lava;

    @Override
    public void load(){
        lava = new Liquid("lava", UnityPal.lavaColor){
            {
                heatCapacity = 0f;
                viscosity = 0.7f;
                temperature = 1.5f;
                effect = UnityStatusEffects.molten;
                lightColor = UnityPal.lavaColor2.cpy().mul(1f, 1f, 1f, 0.55f);
            }
        };

        //endregion

        Events.run(Trigger.update, () -> {
            lava.color = temp.set(UnityPal.lavaColor).lerp(UnityPal.lavaColor2, Mathf.absin(Time.globalTime(), 25f, 1f));
            lava.lightColor = temp2.set(temp).mul(1, 1, 1, 0.55f);
        });
    }
}
