package unity.ai;

import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import unity.type.*;

public class LinkerAI extends FlyingAI{
    public Seq<LinkedAI> links = new Seq<LinkedAI>();
    private boolean first = true;

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void updateUnit(){
        super.updateUnit();

        links.each(e -> {
            if(e.unit().dead) unit.kill();
        });

        if(!first || (unit.x == 0 && unit.y == 0)) return;
        first = false;

        int linkCount = ((UnityUnitType) unit.type).linkCount;

        for(int i = 0; i < linkCount; i++){
            LinkedAI link = new LinkedAI();
            link.spawner = unit;
            Tmp.v1.set(0, 0).trns(360f / linkCount * i, 20);
            link.unit(((UnityUnitType) unit.type).linkType.spawn(unit.team, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y));
            links.add(link);
        }
    }
}
