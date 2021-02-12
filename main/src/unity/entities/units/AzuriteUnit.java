package unity.entities.units;

import arc.math.*;
import arc.math.geom.*;
import mindustry.gen.*;
import unity.graphics.*;

public class AzuriteUnit extends UnitEntity{
    public final FixedTrail[] trails = {new FixedTrail(6), new FixedTrail(10), new FixedTrail(6)};
    public final Position[] trailOffsets = {new Vec2(-12f, -18f), new Vec2(0f, 3f), new Vec2(12f, -18f)};

    @Override
    public void update(){
        super.update();

        for(int i = 0; i < 3; i++){
            trails[i].update(
                x + Angles.trnsx(rotation, trailOffsets[i].getX(), trailOffsets[i].getY()),
                y + Angles.trnsy(rotation, trailOffsets[i].getY(), trailOffsets[i].getY()),
                rotation
            );
        }
    }
}
