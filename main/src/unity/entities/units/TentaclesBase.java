package unity.entities.units;

import arc.struct.*;
import mindustry.gen.*;
import unity.entities.*;
import unity.type.*;

public interface TentaclesBase extends Unitc{
    Seq<Tentacle> tentacles();

    void tentacles(Seq<Tentacle> t);

    default void updateTentacles(){
        tentacles().each(Tentacle::update);
    }

    default void drawTentacles(){
        tentacles().each(Tentacle::draw);
    }

    default void addTentacles(){
        if(type() instanceof UnityUnitType e){
            Seq<Tentacle> t = new Seq<>();
            for(TentacleType tentacle : e.tentacles){
                t.add(new Tentacle().add(tentacle, self()));
            }
            tentacles(t);
        }
    }
}
