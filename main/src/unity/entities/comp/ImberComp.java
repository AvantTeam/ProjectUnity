package unity.entities.comp;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import unity.annotations.Annotations.*;
import unity.type.*;

@EntityComponent
abstract class ImberComp implements Unitc {
    Seq<Unit> closeImberUnits = new Seq<>();
    float laserRange;
    int maxConnections;

    @Import float x, y;
    @Import Team team;
    //git work please
    @Override
    public void setType(UnitType type){
        if(type instanceof UnityUnitType t){
            laserRange = t.laserRange;
            maxConnections = t.maxConnections;
        }
    }

    @Override
    public void update() {
        closeImberUnits.clear();
        Units.nearby(team, x, y, laserRange, laserRange, e -> {
            if (e.type instanceof UnityUnitType unitType && unitType.maxConnections > 0 && closeImberUnits.size < maxConnections) {
                closeImberUnits.add(e);
            }
        });
    }

    @Override
    public void draw() {
        Draw.color(Pal.surge);
        Lines.stroke(3);

        for(Unit unit : closeImberUnits){
            Lines.line(x, y, unit.x, unit.y);
        }

        Lines.stroke(1);
        Draw.reset();
    }
}
