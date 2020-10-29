const rotL = require("libraries/rotpowerlib");

const windTurbine = rotL.torqueGenerator(Block, Building, "wind-turbine", {
	//fuck?
	
	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.overlaysprite = [
			Core.atlas.find(this.name + "-overlay1"),
			Core.atlas.find(this.name + "-overlay2")];
		this.moving = Core.atlas.find(this.name + "-moving");
		this.bottom = Core.atlas.find(this.name + "-bottom");
		this.base = [
			Core.atlas.find(this.name + "-base1"),
			Core.atlas.find(this.name + "-base2"),
			Core.atlas.find(this.name + "-base3"),
			Core.atlas.find(this.name + "-base4")
		];
		this.mbase = Core.atlas.find(this.name + "-mbase");
		this.rotor1 = Core.atlas.find(this.name + "-rotor1");
		this.rotor2 = Core.atlas.find(this.name + "-rotor2");
		this.setAccept([0,1,0,
						0,0,0,
						0,0,0,
						0,0,0]);
		this.setMaxSpeed(50);
		this.setMaxTorque(3);
	},
	
},{
	
	updatePre()
	{
		this.setInertia(35);
		this.setFriction(0.03);
		let x = Time.time()*0.1;
		let mul = 0.4*Math.max(0,Mathf.sin(x) + 0.5*Mathf.sin(2*x+50) + Mathf.sin(7*x+90)+0.45) + 0.2;
		this.setMotorForceMult(mul);
	},
	
	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		let shaftRot = variant==1?360-this.getRotation():this.getRotation();
		Draw.rect(windTurbine.bottom, this.x, this.y, 0);
		Draw.rect(windTurbine.base[this.rotation], this.x, this.y, 0);
		Draw.rect(windTurbine.mbase, this.x, this.y, this.rotdeg());
		//speeeeeeen
		rotL.drawRotRect(windTurbine.moving, this.x, this.y, 24, 14/4, this.rotdeg(), shaftRot, shaftRot+180);
		Draw.rect(windTurbine.overlaysprite[variant], this.x, this.y, this.rotdeg());
		
		Draw.rect(windTurbine.rotor2, this.x, this.y, this.getRotation());
		Draw.rect(windTurbine.rotor1, this.x, this.y, this.getRotation()*2);
		
		Draw.rect(windTurbine.topsprite, this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});

/*driveShaft.buildType= ()=>{
	
	
}*/
windTurbine.rotate = true;
windTurbine.update = true;
windTurbine.solid = true;

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");