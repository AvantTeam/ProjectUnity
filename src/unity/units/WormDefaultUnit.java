package unity.units;

import arc.math.geom.Vec2;
import mindustry.gen.*;


public class WormDefaultUnit extends UnitEntity{
	protected final WormSegmentUnit[] segmentUnits = new WormSegmentUnit[((WormUnitType) type).segmentLength];
	protected final Vec2[] segments = new Vec2[((WormUnitType) type).segmentLength];
	protected final Vec2[] segmentVelocities = new Vec2[((WormUnitType) type).segmentLength];

	public WormSegmentUnit[] getSegments(){ return segmentUnits; }

	public void handleCollision(Hitboxc originUnit, Hitboxc other, float x, float y){}
}
