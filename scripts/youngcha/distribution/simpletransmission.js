const rotL = require("libraries/rotpowerlib");

const simpleTrans = rotL.torqueExtend(Block, Building, "simple-transmission",rotL.baseTypes.torqueTransmission, {
	
	load(){
		this.super$load();
		this.topsprite = [];
		this.topsprite = [Core.atlas.find(this.name + "-top1"),Core.atlas.find(this.name + "-top2")];
		this.overlaysprite = [Core.atlas.find(this.name + "-overlay1"),Core.atlas.find(this.name + "-overlay2")];
		this.moving = [Core.atlas.find(this.name + "-moving1"),Core.atlas.find(this.name + "-moving2"),Core.atlas.find(this.name + "-moving3")];
		this.movingtest = Core.atlas.find(this.name + "-moving");
		this.base = Core.atlas.find(this.name + "-bottom");
		this.mbase = Core.atlas.find(this.name + "-mbase");
		
	},
	
},{
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(25);
		this.getGraphConnector("torque graph").setFriction(0.05);
	},
	
	draw() {
		let torquegraph = this.getGraphConnector("torque graph");
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		Draw.rect(simpleTrans.base, this.x, this.y, 0);
		Draw.rect(simpleTrans.mbase, this.x, this.y, this.rotdeg());
		//speeeeeeen
		let offset = rotL.dirs[(this.rotation+1)%4];
		let ox = offset.x*4;
		let oy = offset.y*4;
		rotL.drawRotRect(simpleTrans.moving[0], this.x+ox, this.y+oy, 16, 18/4, 18/4, fixedrot, torquegraph.getRotationOf(0), torquegraph.getRotationOf(0)+180);
		rotL.drawRotRect(simpleTrans.moving[0], this.x+ox, this.y+oy, 16, 18/4, 18/4, fixedrot, torquegraph.getRotationOf(0)+180, torquegraph.getRotationOf(0)+360);
		
		rotL.drawRotRect(simpleTrans.moving[1], this.x+ox*-0.125, this.y+oy*-0.125, 16, 18/4, 18/4, fixedrot, 360-(torquegraph.getRotationOf(0)), 360-(torquegraph.getRotationOf(0)+180));
		rotL.drawRotRect(simpleTrans.moving[1], this.x+ox*-0.125, this.y+oy*-0.125, 16, 18/4, 18/4, fixedrot, 720-(torquegraph.getRotationOf(0)+180), 720-(torquegraph.getRotationOf(0)+360));
		
		rotL.drawRotRect(simpleTrans.moving[2], this.x-ox, this.y-oy, 16, 10/4, 10/4, fixedrot, torquegraph.getRotationOf(1)+0, torquegraph.getRotationOf(1)+180);
		rotL.drawRotRect(simpleTrans.moving[2], this.x-ox, this.y-oy, 16, 10/4, 10/4, fixedrot, torquegraph.getRotationOf(1)+180, torquegraph.getRotationOf(1)+360);
		
		//
		Draw.rect(simpleTrans.overlaysprite[variant], this.x, this.y, this.rotdeg());
		
		
		//rotL.drawSlideRect(simpleTrans.movingtest, this.x-ox, this.y-oy,26*0.25,9,54*0.25,9,  this.rotdeg(), 18*0.25, this.getNetworkRotation(0)/360.0)
		
		Draw.rect(simpleTrans.topsprite[this.rotation%2], this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});

simpleTrans.rotate = true;
simpleTrans.update = true;
simpleTrans.solid = true;
simpleTrans.getGraphConnectorBlock("torque graph").setAccept([2,1,0,0,1,2,0,0]);
simpleTrans.getGraphConnectorBlock("torque graph").setRatio([1,2.5]);
simpleTrans.getGraphConnectorBlock("torque graph").setBaseFriction(0.05);
simpleTrans.getGraphConnectorBlock("torque graph").setBaseInertia(25);
