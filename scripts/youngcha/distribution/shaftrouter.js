const rotL = require("libraries/rotpowerlib");

const shaftRouter = rotL.torqueExtend(Block, Building, "shaft-router", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.base = Core.atlas.find(this.name);
		
	},
},{

	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(5);
		this.getGraphConnector("torque graph").setFriction(0.05);
	},

	draw() {
		Draw.rect(shaftRouter.base, this.x, this.y, 0);
        this.drawTeamTop();
	}


});

shaftRouter.rotate = false;
shaftRouter.update = true;
shaftRouter.solid = false;
shaftRouter.getGraphConnectorBlock("torque graph").setAccept([1,1,1,1]);
shaftRouter.getGraphConnectorBlock("torque graph").setBaseFriction(0.05);
shaftRouter.getGraphConnectorBlock("torque graph").setBaseInertia(5);