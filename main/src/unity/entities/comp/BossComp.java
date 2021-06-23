package unity.entities.comp;

import mindustry.content.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.*;
import unity.annotations.Annotations.*;

/** @author GlennFolker */
@SuppressWarnings("unused")
@EntityComponent
abstract class BossComp implements Unitc{
    @Import UnitType type;

    @Override
    public void add(){
        apply(StatusEffects.boss);
        Unity.music.play(type.name, this::isValid);
    }
}
