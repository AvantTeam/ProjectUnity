const rotL = require("libraries/rotpowerlib");

const shaftRouter = rotL.torqueExtend(Block, Building, "shaft-router", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.base = Core.atlas.find(this.name);
		
	},
},{

	updatePre()
	{
		this.setInertia(5);
		this.setFriction(0.05);
	},

	draw() {
		Draw.rect(shaftRouter.base, this.x, this.y, 0);
        this.drawTeamTop();
	}


});

shaftRouter.rotate = false;
shaftRouter.update = true;
shaftRouter.solid = false;
shaftRouter.setAccept([1,1,1,1]);
///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");