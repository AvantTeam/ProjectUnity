const rotL = require("libraries/rotpowerlib");

const driveShaft = rotL.torqueExtend(Block, Building, "drive-shaft", rotL.baseTypes.torqueConnector, {

	load(){
		this.super$load();
		this.overlaysprite = null;
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.overlaysprite = Core.atlas.find(this.name + "-overlay");
		this.moving = Core.atlas.find(this.name + "-moving");
		this.base = [Core.atlas.find(this.name + "-base1"),Core.atlas.find(this.name + "-base2"),Core.atlas.find(this.name + "-base3"),Core.atlas.find(this.name + "-base4")];
		
	},
},{
	basespriteindex: 0,
	XOR(a,b) {
	  return ( a || b ) && !( a && b );
	},
	onNeighboursChanged(){
		let tgraph = this.getGraphConnector("torque graph");
		let culm = 0;
		let rot = this.rotation;
		tgraph.eachNeighbour(function(n){
			if(rot==1||rot==2){
				culm += (n.portindex==0)?2:1;
			}else{
				culm += (n.portindex==0)?1:2;
			}
		});
		this.basespriteindex=culm;
	},
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(3);
		this.getGraphConnector("torque graph").setFriction(0.01);
	},

	draw() {
		let tgraph = this.getGraphConnector("torque graph");
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		Draw.rect(driveShaft.base[this.basespriteindex], this.x, this.y, fixedrot);
		//speeeeeeen
		rotL.drawRotRect(driveShaft.moving, this.x, this.y, 8, 14/4, 8,fixedrot, tgraph.getRotation(), tgraph.getRotation()+90);

		Draw.rect(driveShaft.overlaysprite, this.x, this.y, fixedrot);
		Draw.rect(driveShaft.topsprite, this.x, this.y, fixedrot);
        this.drawTeamTop();
	}


});

driveShaft.rotate = true;
driveShaft.update = true;
driveShaft.solid = false;
driveShaft.getGraphConnectorBlock("torque graph").setAccept([1,0,1,0]);
driveShaft.getGraphConnectorBlock("torque graph").setBaseFriction(0.01);
driveShaft.getGraphConnectorBlock("torque graph").setBaseInertia(3);