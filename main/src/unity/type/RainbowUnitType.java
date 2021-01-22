package unity.type;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.gen.*;

public class RainbowUnitType extends UnityUnitType{
    private static final Color tmpColor = new Color();

    public int segments = 5;
    public float offset = 15f;
    public TextureRegion[] rainbowRegions;

    public RainbowUnitType(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();

        rainbowRegions = new TextureRegion[segments];
        for(int i = 0; i < segments; i++){
            rainbowRegions[i] = Core.atlas.find(name + "-rainbow-" + (i + 1));
        }
    }

    @Override
    public void drawBody(Unit unit){
        super.drawBody(unit);
        for(int i = 0; i < segments; i++){
            Draw.color(tmpColor.set(1f, 0f, 0f, 1f).shiftHue(Time.time + (offset * i)));
            Draw.rect(rainbowRegions[i], unit.x, unit.y, unit.rotation - 90f);
        }
        Draw.reset();
    }
}
