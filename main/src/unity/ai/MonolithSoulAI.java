package unity.ai;

import arc.math.geom.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.entities.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import unity.entities.*;
import unity.gen.*;
import unity.util.*;
import unity.world.MonolithWorld.*;

import static unity.Unity.*;

/**
 * Monolith soul AI.
 * @author GlennFolker
 */
public class MonolithSoulAI implements UnitController{
    private static final Vec2 vec = new Vec2();
    protected MonolithSoul unit;

    protected Teamc joinTarget;
    protected Chunk formTarget;
    protected Interval timer = new Interval(2);

    @Override
    public void unit(Unit unit){
        this.unit = (MonolithSoul)unit;
    }

    @Override
    public Unit unit(){
        return unit;
    }

    @Override
    public void updateUnit(){
        // Avoid dying.
        if(timer.get(0, 5f)) contemplate();

        // If it found a fitting vessel to join, move towards it and join.
        if(joinTarget != null){
            vec.set(joinTarget).sub(unit);
            MathU.addLength(vec, -unit.type.range / 2f);
            vec.setLength(Math.min(vec.len(), unit.type.speed));

            unit.moveAt(vec);
            unit.lookAt(unit.prefRotation());
            unit.join(joinTarget);
        }else if(formTarget != null){ // Otherwise, proceed to the most fitting forming location and pick up tiles.
            vec.set(formTarget.centerX, formTarget.centerY).sub(unit);
            MathU.addLength(vec, -unit.type.range / 2f);
            vec.setLength(Math.min(vec.len(), unit.type.speed));

            unit.moveAt(vec);
            unit.lookAt(unit.prefRotation());

            if(timer.get(1, 5f)){
                Chunk in = formTarget.within(unit) ? formTarget : monolithWorld.getChunk(World.toTile(unit.x), World.toTile(unit.y));
                if(in != null){
                    Tile tile = in.monolithTiles.random();
                    if(tile != null && !unit.forms().contains(tile)) unit.form(tile);
                }
            }
        }
    }

    public void contemplate(){
        // No point in finding life support if it's already corporeal.
        if(unit.corporeal()) return;

        // Only contemplate their life choices when they're not already joining another vessel or already gaining positive
        // outcome from forming.
        float delta = unit.lifeDelta();
        if(!unit.joining() && !(unit.forming() && delta > 0f)){
            float range = unit.type.speed * (unit.health / -delta); // Maximum range.

            Unit vesselUnit = Units.closest(unit.team, unit.x, unit.y, range, this::accept);
            Building vesselBuild = Units.findAllyTile(unit.team, unit.x, unit.y, Float.MAX_VALUE, this::accept);
            joinTarget = vesselUnit == null
                ? vesselBuild : vesselBuild == null ? vesselUnit
                : Math.max(unit.dst(vesselUnit) - vesselUnit.hitSize / 2f, 0f) <= Math.max(unit.dst(vesselBuild) - vesselBuild.hitSize() / 2f, 0f)
                    ? vesselUnit : vesselBuild;

            // If it can't find any vessels to join, it'll start finding forming locations.
            if(joinTarget == null){
                float r = range * range;
                formTarget = monolithWorld.nearest(unit.x, unit.y, range, c -> Math.max(c.monolithTiles.size, 5) * (r / unit.dst2(c.centerX, c.centerY)));
            }
        }
    }

    public <T extends Teamc & Healthc> boolean accept(T other){
        Soul soul = Soul.toSoul(other);
        return soul != null && other.isValid() && soul.acceptSoul(1) >= 1;
    }
}
