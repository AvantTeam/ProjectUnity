package unity.units;

import mindustry.gen.*;

public class WormDefaultUnit extends UnitEntity{
	protected final WormSegmentUnit[] segmentUnits = new WormSegmentUnit[((WormUnitType) type).segmentLength];

	public WormSegmentUnit[] getSegments(){ return segmentUnits; }

	public void handleCollision(Hitboxc originUnit, Hitboxc other, float x, float y){}
}
