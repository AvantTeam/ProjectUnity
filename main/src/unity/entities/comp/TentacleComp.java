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
    transient Seq<NewTentacle> tentacles = new Seq<>();

    @Import UnitType type;

    @Override
    public void add(){
        UnityUnitType uType = (UnityUnitType)type;

        if(tentacles.isEmpty() && !uType.tentacles.isEmpty()){
            for(TentacleType tentacle : uType.tentacles){
                tentacles.add(new NewTentacle(tentacle, self()));
            }
        }
    }

    void drawTentacles(){
        tentacles.each(NewTentacle::draw);
    }

    @Override
    public void update(){
        tentacles.each(NewTentacle::update);
    }
}
