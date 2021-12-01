package unity.entities.effects;

import arc.func.*;
import arc.graphics.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class SlowLightningType{
    public Color colorFrom = Color.white, colorTo = Pal.lancerLaser;
    public float damage = 12;
    public float colorTime = 32f;
    public float splitChance = 0.035f;
    public float nodeLength = 50f, nodeTime = 2f, range = 150f;
    public float randSpacing = 20f, splitRandSpacing = 60f;
}
