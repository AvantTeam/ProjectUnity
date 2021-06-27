package unity.ai;

import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import unity.type.*;

public class LinkerAI extends FlyingAI{
    public Seq<LinkedAI> links = new Seq<LinkedAI>();
    private boolean first = true;
    public float angle = 0f;
    public Vec2 center;

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void updateUnit(){
        super.updateUnit();

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
        center = new Vec2(unit.x, unit.y);

        Tmp.v1.set(0, 0).trns(360f, 20);
        unit.set(unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
    }

    @Override
    public void updateMovement(){
        super.updateMovement();

        unit.rotation = angle;

        angle += (((UnityUnitType)unit.type).rotationSpeed / 60f) * Time.delta;
    }
}
