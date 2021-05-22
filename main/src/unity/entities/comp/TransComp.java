package unity.entities.comp;

import arc.math.geom.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.type.*;

@SuppressWarnings("unused")
@EntityComponent
abstract class TransComp implements Unitc{
    @SyncLocal float transformTime;

    @Import UnitType type;
    @Import Team team;

    @Import float x, y, rotation;
    @Import Vec2 vel;

    @Override
    public void setType(UnitType type){
        if(type instanceof UnityUnitType def){
            transformTime = def.transformTime;
        }
    }

    @Override
    public void update(){
        UnityUnitType type = (UnityUnitType)this.type;
        if(type.transPred.get(self())){
            if(transformTime < 0f || transformTime > type.transformTime){
                Unit next = type.toTrans.get(self()).spawn(team, x, y);
                next.rotation = rotation;
                next.add();
                next.vel.set(vel);

                if(isPlayer()){
                    next.controller(controller());
                }

                remove();
            }else{
                transformTime -= Time.delta;
            }
        }
    }
}
