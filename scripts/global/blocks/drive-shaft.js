const rotL = require("libraries/rotpowerlib");

const driveShaft = rotL.powerUser(Block, Building, "drive-shaft", {

	load(){
		this.super$load();
		this.overlaysprite = null;
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.overlaysprite = Core.atlas.find(this.name + "-overlay");
		this.moving = Core.atlas.find(this.name + "-moving");
		this.base = Core.atlas.find(this.name + "-base");
		this.setAccept([1,0,1,0]);
	},
},{

	updatePre()
	{
		this.setInertia(3);
		this.setFriction(0.02);
	},

	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		Draw.rect(driveShaft.base, this.x, this.y, fixedrot);
		//speeeeeeen
		rotL.drawRotRect(driveShaft.moving, this.x, this.y, 8, 14/4, fixedrot, this.getRotation(), this.getRotation()+90);

		Draw.rect(driveShaft.overlaysprite, this.x, this.y, fixedrot);
		Draw.rect(driveShaft.topsprite, this.x, this.y, fixedrot);
        this.drawTeamTop();
	}


});

driveShaft.rotate = true;
driveShaft.update = true;
driveShaft.solid = false;

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");