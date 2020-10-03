package unity.units;

import arc.math.Mathf;
import mindustry.gen.*;

public class CopterUnit extends UnitEntity{
	@Override
	public void update(){
		super.update();
		if (dead) rotation += copterType().fallRotateSpeed * Mathf.signs[id % 2];
		customUpdate();
	}

	public void customUpdate(){}

	protected CopterUnitType copterType(){ return (CopterUnitType) type; }
}
