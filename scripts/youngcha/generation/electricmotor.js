const rotL = require("libraries/rotpowerlib");


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
		
		
	},
	
},{
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(25);
		this.getGraphConnector("torque graph").setFriction(0.1);
		this.getGraphConnector("torque graph").setMotorForceMult(this.power.graph.getSatisfaction());
	},
	
	draw() {
		let torquegraph = this.getGraphConnector("torque graph");
		let fixedrot = ((this.rotdeg() + 90) % 180) - 90;
		let variant = ((this.rotation + 1) % 4>=2) ? 1 : 0;
		let rotVar = (this.rotation == 1 || this.rotation == 3) ? 1 : 0;
		let shaftrotog = torquegraph.getRotation();
		let shaftRot = variant==1?360-shaftrotog:shaftrotog;
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

electricMotor.rotate = true;
electricMotor.update = true;
electricMotor.solid = true;
electricMotor.consumes.power(4.5);
electricMotor.getGraphConnectorBlock("torque graph").setAccept([0,1,0,
																0,0,0,
																0,1,0,
																0,0,0]);
electricMotor.getGraphConnectorBlock("torque graph").setMaxSpeed(10);
electricMotor.getGraphConnectorBlock("torque graph").setMaxTorque(20);
electricMotor.getGraphConnectorBlock("torque graph").setBaseFriction(0.1);
electricMotor.getGraphConnectorBlock("torque graph").setBaseInertia(25);