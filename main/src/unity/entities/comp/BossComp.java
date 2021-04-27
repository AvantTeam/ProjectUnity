package unity.entities.comp;

import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.sync.*;

/** @author GlennFolker */
@EntityComponent
abstract class BossComp implements Unitc{
    @Import UnitType type;

    @Override
    public void add(){
        apply(StatusEffects.boss);
        UnityCall.bossMusic(type.name, true);
    }

    @Override
    public void remove(){
        UnityCall.bossMusic(type.name, false);
    }
}
