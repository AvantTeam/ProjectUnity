package unity.entities.abilities;

import mindustry.entities.abilities.*;
import mindustry.gen.*;
import unity.*;
import unity.mod.TapHandler.*;

public abstract class TapAbility extends Ability implements TapListener{
    {
        Unity.tapHandler.addListener(this);
    }

    @Override
    public void tap(Player player, float x, float y){
        Unit unit = player.unit();
        if(unit.dead){
            Unity.tapHandler.removeListener(this);
        }else{
            tapped(unit, x, y);
        }
    }

    public abstract void tapped(Unit unit, float x, float y);
}
