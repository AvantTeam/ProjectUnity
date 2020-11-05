const rotL = require("libraries/rotpowerlib");

const handCrank = rotL.torqueExtend(Block, Building, "torque-infi", rotL.baseTypes.torqueGenerator, {

	load(){
		this.super$load();
		this.handle = Core.atlas.find(this.name);
		this.setAccept([1,1,1,1]);
		this.setMaxTorque(9999);
		this.setMaxSpeed(999999);
	},
},{

	updatePre()
	{
		
		this.setInertia(1);
		this.setFriction(0.001);
		this.setMotorForceMult(1);
	},

	draw() {
		Draw.rect(handCrank.handle, this.x, this.y, 0);
        this.drawTeamTop();
	}


});

handCrank.rotate = true;
handCrank.update = true;
handCrank.solid = false;

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");