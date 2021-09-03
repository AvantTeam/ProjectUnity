package unity.entities.comp;

import arc.graphics.g2d.*;
import arc.struct.*;
import mindustry.Vars;
import mindustry.entities.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.blocks.power.*;
import unity.annotations.Annotations.*;
import unity.gen.*;
import unity.type.*;

@EntityComponent
abstract class ImberComp implements Unitc {
    transient Seq<Unit> closeImberUnits = new Seq<>();
    transient Seq<Building> closeNodes = new Seq<>();
    transient float laserRange;
    transient int maxConnections;
    transient int connections;

    @Import float x, y;
    @Import Team team;

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
        closeNodes.clear();
        connections = 0;

        Units.nearby(team, x, y, laserRange, laserRange, e -> {
            if (connections < maxConnections && !e.dead && e.team == team && e.type instanceof UnityUnitType unitType && unitType.maxConnections > 0 && !((Imberc)e).closeImberUnits().contains(controller().unit())) {
                closeImberUnits.add(e);
                connections++;
            }
        });

        Vars.indexer.eachBlock(team, x, y, laserRange, e -> connections < maxConnections && e.team == team && !e.dead && e instanceof PowerNode.PowerNodeBuild, e -> {
            closeNodes.add(e);
            connections++;
        });
    }

    @Override
    public void draw() {
        float z = Draw.z();
        Draw.color(Pal.surge);
        Draw.z(Layer.flyingUnit + 1);

        Lines.stroke(3);

        for(Unit unit : closeImberUnits){
            Lines.line(x, y, unit.x, unit.y);
        }

        for(Building building : closeNodes){
            Lines.line(x, y, building.x, building.y);
        }

        Lines.stroke(1);
        Draw.z(z);
        Draw.reset();
    }
}
