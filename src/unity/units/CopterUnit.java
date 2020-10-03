package unity.units;

import arc.math.Mathf;
import mindustry.gen.*;
import unity.content.UnityUnitTypes;

public class CopterUnit extends UnitEntity{
	private int classID;

	@Override
	public void update(){
		super.update();
		if (dead) rotation += copterType().fallRotateSpeed * Mathf.signs[id % 2];
		customUpdate();
	}

	public void customUpdate(){}

	@Override
	public int classId(){ return UnityUnitTypes.getClassId(0); }

	protected CopterUnitType copterType(){ return (CopterUnitType) type; }
}
