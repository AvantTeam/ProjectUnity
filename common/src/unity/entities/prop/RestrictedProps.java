package unity.entities.prop;

import arc.func.*;
import mindustry.entities.units.*;
import mindustry.game.*;
import mindustry.gen.*;
import unity.entities.type.PUUnitTypeCommon.*;

public class RestrictedProps extends Props{
    public static Boolf<Unit> limit;
    public static Boolf2<BuildPlan, Team> planValid;
    public float disconnectionTime = 60f;
}
