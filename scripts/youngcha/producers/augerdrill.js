const rotL = require("libraries/rotpowerlib");

const augerDrill = rotL.torqueExtendContent(Drill, Drill.DrillBuild, "auger-drill", rotL.baseTypes.torqueConsumer,{
	
	load(){
		this.super$load();
		this.bottom = [Core.atlas.find(this.name + "-bottom1"),Core.atlas.find(this.name + "-bottom2")];
		this.rotor = Core.atlas.find(this.name + "-rotor");
		this.rotortexture = Core.atlas.find(this.name + "-rotor-rotate");
		this.mbase = Core.atlas.find(this.name + "-mbase");
		this.gear = Core.atlas.find(this.name + "-gear");
		this.moving = Core.atlas.find(this.name + "-rotate");
		this.overlaysprite = Core.atlas.find(this.name + "-overlay");
		this.top = [Core.atlas.find(this.name + "-top1"),Core.atlas.find(this.name + "-top2")];
		// facing right (rotation :0)
		
	},
	
	rotatedOutput( x,  y){
        return false;
    }
	
	
},{
	
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(45);
		this.warmup = Math.min(1.0,this.warmup);
	},
	
	draw() {
		//todo drawing code.
		let tgraph = this.getGraphConnector("torque graph");
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = this.rotation%2;
		let shaftRot = tgraph.getRotation()*2;
		let offset = rotL.dirs[(this.rotation+1)%4];
		Draw.rect(augerDrill.bottom[variant], this.x, this.y, 0);
		Draw.rect(augerDrill.rotor, this.x, this.y, 360-(tgraph.getRotation()*0.25));
		rotL.drawRotRect(augerDrill.rotortexture, this.x, this.y, 24, 14/4, 14/4, 450-(tgraph.getRotation()*0.25), shaftRot, shaftRot+180);
		rotL.drawRotRect(augerDrill.rotortexture, this.x, this.y, 24, 14/4, 14/4, 450-(tgraph.getRotation()*0.25), shaftRot+180, shaftRot+360);
		
		Draw.rect(augerDrill.mbase, this.x, this.y, fixedrot);
		rotL.drawRotRect(augerDrill.moving, this.x, this.y, 24, 14/4, 14/4,fixedrot, tgraph.getRotation(), tgraph.getRotation()+180);
		rotL.drawRotRect(augerDrill.moving, this.x, this.y, 24, 14/4, 14/4,fixedrot, tgraph.getRotation()+180, tgraph.getRotation()+360);
		Draw.rect(augerDrill.overlaysprite, this.x, this.y, fixedrot);
		
		if(this.rotation==1&&this.rotation==2){
			offset.x*=-1;
			offset.y*=-1;
		}
		Draw.rect(augerDrill.gear, this.x+offset.x*4, this.y+offset.y*4, 360-tgraph.getRotation());
		Draw.rect(augerDrill.gear, this.x-offset.x*4, this.y-offset.y*4, tgraph.getRotation());
		
		Draw.rect(augerDrill.top[variant], this.x, this.y, 0);
        this.drawTeamTop();

	}
	
	
});
augerDrill.rotate = true;
augerDrill.update = true;
augerDrill.solid = true;
augerDrill.hasItems = true;
augerDrill.outputsItem = true;
augerDrill.tier= 3;
augerDrill.drawMineItem=true;
augerDrill.drillTime=400;
augerDrill.getGraphConnectorBlock("torque graph").setIdleFriction(0.03);
augerDrill.getGraphConnectorBlock("torque graph").setWorkingFriction(0.15);
augerDrill.getGraphConnectorBlock("torque graph").setNominalSpeed(8);
augerDrill.getGraphConnectorBlock("torque graph").setAccept([0,1,0, //right side
															0,0,0, //top 
															0,1,0, //left
															0,0,0]);//bottom
