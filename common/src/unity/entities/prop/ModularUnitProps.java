package unity.entities.prop;

import arc.func.*;
import mindustry.type.*;
import unity.entities.type.PUUnitTypeCommon.*;
import unity.parts.Blueprint.*;
import unity.parts.PartType.*;

public class ModularUnitProps extends Props{
    public final Func<byte[], Construct<? extends Part>> decoder;

    public ModularUnitProps(UnitType parent, Func<byte[], Construct<? extends Part>> decoder){
        super(true);
        this.decoder = decoder;
    }
}
