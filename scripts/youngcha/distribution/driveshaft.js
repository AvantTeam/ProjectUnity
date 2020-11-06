const rotL = require("libraries/rotpowerlib");

const driveShaft = rotL.torqueExtend(Block, Building, "drive-shaft", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.overlaysprite = null;
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.overlaysprite = Core.atlas.find(this.name + "-overlay");
		this.moving = Core.atlas.find(this.name + "-moving");
		this.base = Core.atlas.find(this.name + "-base");
		
	},
},{

	updatePre()
	{
		this.setInertia(3);
		this.setFriction(0.01);
	},

	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		Draw.rect(driveShaft.base, this.x, this.y, fixedrot);
		//speeeeeeen
		rotL.drawRotRect(driveShaft.moving, this.x, this.y, 8, 14/4, 8,fixedrot, this.getRotation(), this.getRotation()+90);

		Draw.rect(driveShaft.overlaysprite, this.x, this.y, fixedrot);
		Draw.rect(driveShaft.topsprite, this.x, this.y, fixedrot);
        this.drawTeamTop();
	}


});

driveShaft.rotate = true;
driveShaft.update = true;
driveShaft.solid = false;
driveShaft.setAccept([1,0,1,0]);
///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");