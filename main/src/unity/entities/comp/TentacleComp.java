package unity.entities.comp;

import arc.struct.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.entities.*;
import unity.type.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class TentacleComp implements Unitc{
    transient Seq<Tentacle> tentacles = new Seq<>();

    @Import UnitType type;

    @Override
    public void add(){
        if(tentacles.isEmpty()){
            for(TentacleType tentacle : ((UnityUnitType)type).tentacles){
                tentacles.add(new Tentacle().add(tentacle, self()));
            }
        }
    }

    public void drawTentacles(){
        tentacles.each(Tentacle::draw);
    }

    @Override
    public void update(){
        tentacles.each(Tentacle::update);
    }
}
