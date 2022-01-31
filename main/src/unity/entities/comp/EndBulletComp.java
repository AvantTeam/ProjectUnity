package unity.entities.comp;

import mindustry.game.*;
import mindustry.gen.*;
import unity.annotations.Annotations.*;
import unity.gen.*;

@SuppressWarnings("unused")
@EntityDef(value = {Bulletc.class, EndBulletc.class}, serialize = false, pooled = true)
@EntityComponent
abstract class EndBulletComp implements Bulletc{
    @Import Team team;
    @Import Entityc owner;

    private Teamc trueOwner;

    void setTrueOwner(Teamc owner){
        trueOwner = owner;
    }

    @MethodPriority(-2)
    @Override
    public void update(){
        if(trueOwner != null && (owner != trueOwner || team != trueOwner.team())){
            team = trueOwner.team();
            owner = trueOwner;
        }
    }
}
