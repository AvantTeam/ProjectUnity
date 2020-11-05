const rotL = require("libraries/rotpowerlib");
//thanks to steelblue8 for feedback and adjustments to the base sprite.
const electricMotor = rotL.torqueExtend(Block, Building, "electric-motor", rotL.baseTypes.torqueGenerator,{
	
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
			Core.atlas.find(this.name + "-base2")
		];
		this.coil = [
			Core.atlas.find(this.name + "-coil1"),
			Core.atlas.find(this.name + "-coil2")
		];
		this.mbase = Core.atlas.find(this.name + "-mbase");
		this.setAccept([0,1,0,
						0,0,0,
						0,1,0,
						0,0,0]);
		this.setMaxSpeed(20);
		this.setMaxTorque(60);
	},
	
},{
	
	updatePre()
	{
		this.setInertia(10);
		this.setFriction(0.3);
		let mul = ((this.power.graph == null ? 0 : (this.power.graph.powerBalance <= 30) ? this.power.graph.powerBalance : 30));
		if(mul < 0){
			mul = 0;
		}
		if(mul > 30){
			mul = 30;
		}
		this.setForce(mul*2);
	},
	
	draw() {
		let fixedrot = ((this.rotdeg() + 90) % 180) - 90;
		let variant = ((this.rotation + 1) % 4>=2) ? 1 : 0;
		let rotVar = (this.rotation == 1 || this.rotation == 3) ? 1 : 0;
		let shaftRot = variant==1?360-this.getRotation():this.getRotation();
		Draw.rect(electricMotor.bottom, this.x, this.y, 0);
		Draw.rect(electricMotor.base[rotVar], this.x, this.y, 0);
		Draw.rect(electricMotor.coil[rotVar], this.x, this.y, 0);
		Draw.rect(electricMotor.mbase, this.x, this.y, this.rotdeg());
		//speeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeen
		rotL.drawRotRect(electricMotor.moving, this.x, this.y, 24, 14/4, 24, this.rotdeg(), shaftRot, shaftRot+180);
		Draw.rect(electricMotor.overlaysprite[variant], this.x, this.y, this.rotdeg());
		
		Draw.rect(electricMotor.topsprite, this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});

/*driveShaft.buildType= ()=>{
	
	
}*/
electricMotor.rotate = true;
electricMotor.update = true;
electricMotor.solid = true;
electricMotor.consumes.power(0.8);

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");