package unity.content;

import mindustry.type.*;
import unity.entities.prop.*;
import unity.entities.type.*;
import unity.gen.entities.*;
import unity.mod.*;
import unity.parts.*;

import static unity.gen.entities.EntityRegistry.content;
import static unity.mod.FactionRegistry.register;

public class YoungchaUnitTypes{
    public static UnitType modularUnitSmall;

    private YoungchaUnitTypes(){
        throw new AssertionError();
    }

    public static void load(){
        modularUnitSmall = register(Faction.youngcha, content("modularUnit", ModularUnit.class, n -> new PUUnitType(n){{
            faceTarget = false;
            omniMovement = false;
            weapons.add(new Weapon("gun")); //blank weapon so mobile doesn't die
            //stats? what stats? :D
            prop(new ModularProps(this, data -> new ModularUnitBlueprint(data).construct(), "g4SFgICEgIGEgIKFgIOBgYCDgYGAgYOFgoCFgoM=", "g4OFgICCgIGBgYCEgYGFgYKFgoCCgoE=",
            "g4SFgICEgIGEgIKCgIOBgYCIgYGFgYOFgoCCgoM=", "gYSFgICBgIGCgIKFgIM=", "g4SFgICEgIGEgIKFgIOBgYCVgYGFgYOFgoCFgoM=",
            "g4OFgICAgIGFgIKBgYCEgYGCgYKFgoCAgoGFgoI="
            ));
        }}));
    }
}
