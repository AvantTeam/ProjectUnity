const rotL = require("libraries/rotpowerlib");

const inlinegearbox = rotL.torqueExtend(Block, Building, "simple-transmission",rotL.baseTypes.torqueTransmission, {
	
	load(){
		this.super$load();
		this.topsprite = [];
		this.topsprite = [Core.atlas.find(this.name + "-top1"),Core.atlas.find(this.name + "-top2")];
		this.overlaysprite = [Core.atlas.find(this.name + "-overlay1"),Core.atlas.find(this.name + "-overlay2")];
		this.moving = [Core.atlas.find(this.name + "-moving1"),Core.atlas.find(this.name + "-moving2"),Core.atlas.find(this.name + "-moving3")];
		this.base = Core.atlas.find(this.name + "-bottom");
		this.mbase = Core.atlas.find(this.name + "-mbase");
		this.setAccept([2,1,0,0,1,2,0,0]);
	},
	
},{
	
	updatePre()
	{
		this.setInertia(25);
		this.setFriction(0.05);
	},
	
	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		Draw.rect(inlinegearbox.base, this.x, this.y, 0);
		Draw.rect(inlinegearbox.mbase, this.x, this.y, this.rotdeg());
		//speeeeeeen
		let offset = rotL.dirs[(this.rotation+1)%4];
		let ox = offset.x*4;
		let oy = offset.y*4;
		rotL.drawRotRect(inlinegearbox.moving[0], this.x+ox, this.y+oy, 16, 18/4, 18/4, fixedrot, this.getNetworkRotation(0), this.getNetworkRotation(0)+180);
		rotL.drawRotRect(inlinegearbox.moving[0], this.x+ox, this.y+oy, 16, 18/4, 18/4, fixedrot, this.getNetworkRotation(0)+180, this.getNetworkRotation(0)+360);
		
		rotL.drawRotRect(inlinegearbox.moving[1], this.x+ox*-0.125, this.y+oy*-0.125, 16, 18/4, 18/4, fixedrot, 360-(this.getNetworkRotation(0)), 360-(this.getNetworkRotation(0)+180));
		rotL.drawRotRect(inlinegearbox.moving[1], this.x+ox*-0.125, this.y+oy*-0.125, 16, 18/4, 18/4, fixedrot, 720-(this.getNetworkRotation(0)+180), 720-(this.getNetworkRotation(0)+360));
		
		rotL.drawRotRect(inlinegearbox.moving[2], this.x-ox, this.y-oy, 16, 10/4, 10/4, fixedrot, this.getNetworkRotation(1)+0, this.getNetworkRotation(1)+180);
		rotL.drawRotRect(inlinegearbox.moving[2], this.x-ox, this.y-oy, 16, 10/4, 10/4, fixedrot, this.getNetworkRotation(1)+180, this.getNetworkRotation(1)+360);
		
		//
		Draw.rect(inlinegearbox.overlaysprite[variant], this.x, this.y, this.rotdeg());
		Draw.rect(inlinegearbox.topsprite[this.rotation%2], this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});

/*driveShaft.buildType= ()=>{
	
	
}*/
inlinegearbox.rotate = true;
inlinegearbox.update = true;
inlinegearbox.solid = true;

///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");