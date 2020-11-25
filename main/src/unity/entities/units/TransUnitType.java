package unity.entities.units;

import arc.func.Prov;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class TransUnitType extends UnitType{
    protected float transformTime;
    protected Prov<UnitType> toTrans;

    public TransUnitType(String name){
        super(name);
    }

    @Override
    public Unit create(Team team){
        Unit ret = super.create(team);
        ((TransformerBase)ret).setTimeTrans(transformTime);
        return ret;
    }
}
