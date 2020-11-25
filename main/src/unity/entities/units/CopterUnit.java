package unity.entities.units;

import arc.math.Mathf;
import mindustry.gen.*;
import unity.content.UnityUnitTypes;

public class CopterUnit extends UnitEntity{
    @Override
    public void update(){
        super.update();

        if(dead){
            rotation += ((CopterUnitType)type).fallRotateSpeed * Mathf.signs[id() % 2];
        }
    }

    @Override
    public int classId(){
        return UnityUnitTypes.getClassId(0);
    }
}
