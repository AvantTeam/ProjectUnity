const rotL = require("libraries/rotpowerlib");

const torqueinfi = rotL.torqueExtend(Block, Building, "torque-infi", rotL.baseTypes.torqueGenerator, {

	load(){
		this.super$load();
		this.base = Core.atlas.find(this.name);

	},
},{

	updatePre()
	{
		
		this.getGraphConnector("torque graph").setInertia(1);
		this.getGraphConnector("torque graph").setFriction(0.001);
		this.getGraphConnector("torque graph").setMotorForceMult(1);
	},

	draw() {
		Draw.rect(torqueinfi.base, this.x, this.y, 0);
        this.drawTeamTop();
	}


});

torqueinfi.rotate = true;
torqueinfi.update = true;
torqueinfi.solid = false;
torqueinfi.getGraphConnectorBlock("torque graph").setAccept([1,1,1,1]);
torqueinfi.getGraphConnectorBlock("torque graph").setMaxTorque(9999);
torqueinfi.getGraphConnectorBlock("torque graph").setMaxSpeed(999999);
torqueinfi.getGraphConnectorBlock("torque graph").setBaseFriction(0.001);
torqueinfi.getGraphConnectorBlock("torque graph").setBaseInertia(1);