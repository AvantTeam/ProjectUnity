const rotL = require("libraries/rotpowerlib");
//thanks to steelblue8 for feedback and adjustments to the base sprite.
const windTurbine = rotL.torqueExtend(Block, Building, "wind-turbine", rotL.baseTypes.torqueGenerator,{
	
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
		

	},
	
},{
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(20);
		this.getGraphConnector("torque graph").setFriction(0.03);
		let x = Time.time*0.001;
		let mul = 0.4*Math.max(0,
				Mathf.sin(x) + 
				0.5*Mathf.sin(2*x+50)+ 
				0.2*Mathf.sin(7*x+90)+
				0.1*Mathf.sin(23*x+10)+
				0.55
				)+ 0.15;
		this.getGraphConnector("torque graph").setMotorForceMult(mul);
	},
	
	draw() {
		let torquegraph = this.getGraphConnector("torque graph");
		let shaftrotog = torquegraph.getRotation();
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		let shaftRot = variant==1?360-shaftrotog:shaftrotog;
		Draw.rect(windTurbine.bottom, this.x, this.y, 0);
		Draw.rect(windTurbine.base[this.rotation], this.x, this.y, 0);
		Draw.rect(windTurbine.mbase, this.x, this.y, this.rotdeg());
		//speeeeeeen
		rotL.drawRotRect(windTurbine.moving, this.x, this.y, 24, 14/4, 24, this.rotdeg(), shaftRot, shaftRot+180);
		Draw.rect(windTurbine.overlaysprite[variant], this.x, this.y, this.rotdeg());
		
		Drawf.shadow(windTurbine.rotor2, this.x -(this.block.size / 2), this.y - (this.block.size / 2), shaftrotog);
		Draw.rect(windTurbine.rotor2, this.x, this.y, shaftrotog);
		Draw.rect(windTurbine.rotor1, this.x, this.y, shaftrotog*2);
		
		Draw.rect(windTurbine.topsprite, this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});

windTurbine.rotate = true;
windTurbine.update = true;
windTurbine.solid = true;
windTurbine.getGraphConnectorBlock("torque graph").setAccept([0,1,0,
															   0,0,0,
															   0,0,0,
															   0,0,0]);
windTurbine.getGraphConnectorBlock("torque graph").setMaxSpeed(5);
windTurbine.getGraphConnectorBlock("torque graph").setMaxTorque(5);
windTurbine.getGraphConnectorBlock("torque graph").setBaseFriction(0.03);
windTurbine.getGraphConnectorBlock("torque graph").setBaseInertia(20);
