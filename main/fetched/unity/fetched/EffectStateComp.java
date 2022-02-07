package unity.fetched;

import arc.graphics.*;
import unity.annotations.Annotations.*;
import mindustry.entities.*;
import mindustry.gen.*;

@EntityComponent(base = true, write = false)
abstract class EffectStateComp implements Posc, Drawc, Timedc, Rotc, Childc{
    @Import float time, lifetime, rotation, x, y;
    @Import int id;

    Color color = new Color(Color.white);
    Effect effect;
    Object data;

    @Override
    public void draw(){
        lifetime = effect.render(id, color, time, lifetime, rotation, x, y, data);
    }

    @Replace
    public float clipSize(){
        return effect.clip;
    }
}
