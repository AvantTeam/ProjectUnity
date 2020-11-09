const rotL = require("libraries/rotpowerlib");

const inlinegearbox = rotL.torqueExtend(Block, Building, "inline-gearbox",rotL.baseTypes.torqueConnector, {
	
	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.overlaysprite = Core.atlas.find(this.name + "-overlay");
		this.moving = Core.atlas.find(this.name + "-moving");
		this.base = Core.atlas.find(this.name + "-base");
		this.mbase = Core.atlas.find(this.name + "-mbase");
		this.gear = Core.atlas.find(this.name + "-gear");
		
	},
	
},{
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(20);
		this.getGraphConnector("torque graph").setFriction(0.02);
	},
	
	draw() {
		let torquegraph = this.getGraphConnector("torque graph");
		let shaftrot = torquegraph.getRotation();
		
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		Draw.rect(inlinegearbox.base, this.x, this.y, 0);
		Draw.rect(inlinegearbox.mbase, this.x, this.y, fixedrot);
		//speeeeeeen
		let offset = rotL.dirs[(this.rotation+1)%4];
		let ox = offset.x*4;
		let oy = offset.y*4;
		rotL.drawRotRect(inlinegearbox.moving, this.x+ox, this.y+oy, 16, 14/4, 8, fixedrot, shaftrot, shaftrot+90);
		rotL.drawRotRect(inlinegearbox.moving, this.x-ox, this.y-oy, 16, 14/4, 8, fixedrot, shaftrot+90, shaftrot+180);
		//gears
		Draw.rect(inlinegearbox.gear, this.x+2, this.y+2, shaftrot);
		Draw.rect(inlinegearbox.gear, this.x-2, this.y+2, -shaftrot);
		Draw.rect(inlinegearbox.gear, this.x+2, this.y-2, -shaftrot);
		Draw.rect(inlinegearbox.gear, this.x-2, this.y-2, shaftrot);
		//
		Draw.rect(inlinegearbox.overlaysprite, this.x, this.y, fixedrot);
		Draw.rect(inlinegearbox.topsprite, this.x, this.y, fixedrot);
        this.drawTeamTop();
	}
	
	
});

inlinegearbox.rotate = true;
inlinegearbox.update = true;
inlinegearbox.solid = true;
inlinegearbox.getGraphConnectorBlock("torque graph").setAccept([1,1,0,0,1,1,0,0]);
inlinegearbox.getGraphConnectorBlock("torque graph").setBaseFriction(0.02);
inlinegearbox.getGraphConnectorBlock("torque graph").setBaseInertia(20);