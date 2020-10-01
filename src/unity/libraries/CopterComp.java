package unity.libraries;

import mindustry.gen.*;
import mindustry.annotations.Annotations.*;
//what
@Component
abstract class CopterComp implements Copterc,Unitc,Healthc,Rotc{
	@Import boolean dead;
	@Import float rotation;
	@Import CopterUnitType type;
	@Import int id;
	@Override
	public void update(){
		if(dead) rotation+=type.fallRotateSpeed*Mathf.signs[id%2];
		customUpdate();
	}
	protected void customUpdate() {}
}
