package unity.entities.comp;

import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.mod.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class FactionComp implements Entityc{
    abstract Faction faction();

    boolean isSameFaction(Entityc other){
        if(other instanceof Building build){
            return faction() == FactionMeta.map(build.block);
        }else{
            return other instanceof Factionc fac && faction() == fac.faction();
        }
    }
}
