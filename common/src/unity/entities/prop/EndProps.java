package unity.entities.prop;

import arc.func.*;
import mindustry.gen.*;
import unity.entities.type.PUUnitTypeCommon.*;

public class EndProps extends Props{
    public static Cons<Unit> add, remove;
    public boolean shouldAdd = false;
    public float invincibilityFrames = 15f, invincibilityTrigger = 35f,
            maxDamage = 10000f, maxDamageCurve = 5000f;

    public float aggroDamage = 1000000f;
    public float maxAggroTime = 5f * 60f;
    public float aggroTimePerDamage = 3f;
    public float aggroMultiplier = 5f;
}
