const rotL = require("libraries/rotpowerlib");
const waterTurbine = rotL.torqueExtendContent(ArmoredConduit, ArmoredConduit.ArmoredConduitBuild, "water-turbine", rotL.baseTypes.torqueGenerator,{
	
	load(){
		this.super$load();
		this.topsprite = [
			Core.atlas.find(this.name + "-top1"),
			Core.atlas.find(this.name + "-top2"),
			Core.atlas.find(this.name + "-top3"),
			Core.atlas.find(this.name + "-top4")
		];
		this.base = [
			Core.atlas.find(this.name + "-bottom1"),
			Core.atlas.find(this.name + "-bottom2")
		];
		this.liquidSprite = [
			Core.atlas.find(this.name + "-liquid1"),
			Core.atlas.find(this.name + "-liquid2")
		];
		this.rotor = Core.atlas.find(this.name + "-rotor");
		this.setAccept([0,0,0,
						0,1,0,
						0,0,0,
						0,1,0]);
		this.setMaxSpeed(7);
		this.setMaxTorque(15);
		this._timerFlow = this.timers++;
	},
	_timerFlow:0,
	getTimerFlow(){
		return this._timerFlow;
	},
	setTimerFlow(s){
		this._timerFlow=s;
	},
	rotatedOutput( x,  y){
        return false;
    }
	
	
},{
	_flowRate:0,
	getFlowRate(){
		return this._flowRate;
	},
	updatePre()
	{
		let flow = this._flowRate*40;
		this.setInertia(20);
		this.setFriction(0.03);
		
		
		this.smoothLiquid = Mathf.lerpDelta(this.smoothLiquid, this.liquids.currentAmount() / this.liquidCapacity, 0.05);
		
		if((this.liquids.total() > 0.001) && this.timer.get(this.block.getTimerFlow(), 1)){
			this._flowRate = this.moveLiquidForward(waterTurbine.leaks, this.liquids.current());
		}
		let mul = flow/100;
		if(mul>1.0){
			mul=0.5*Mathf.log2(mul)+1;
		}
		this.setMotorForceMult(mul);
	},
	
	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		let shaftRot = variant==1?360-this.getRotation():this.getRotation();
		Draw.rect(waterTurbine.base[this.rotation%2], this.x, this.y, 0);
		if(this.liquids.total() > 0.001){
			Drawf.liquid(waterTurbine.liquidSprite[this.rotation%2], this.x, this.y, this.liquids.total() / this.block.liquidCapacity, this.liquids.current().color);
		}
		
		Drawf.shadow(waterTurbine.rotor, this.x -(this.block.size / 2), this.y - (this.block.size / 2), this.getRotation());
		Draw.rect(waterTurbine.rotor, this.x, this.y, this.getRotation());
		
		Draw.rect(waterTurbine.topsprite[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();

	},
	
	moveLiquidForward(leaks, liquid) {
		//moveLiquidForward only works for 1x1 blocks so override is needed.
		let rpos = rotL.getConnectSidePos(1, 3,this.rotation).toPos;
		let next = this.tile.nearby(rpos.x,rpos.y)
        if (next == null) return 0;
        if (next.build != null) {
            return this.moveLiquid(next.build, liquid);
        } 
        return 0;
	}
	
});

/*driveShaft.buildType= ()=>{
	
	
}*/
waterTurbine.rotate = true;
waterTurbine.update = true;
waterTurbine.solid = true;
waterTurbine.hasLiquids = true;
waterTurbine.outputsLiquid = true;
waterTurbine.liquidCapacity = 300;
waterTurbine.noUpdateDisabled = false;
waterTurbine.setUseOgUpdate(false);
waterTurbine.liquidPressure = 0.3;


///Vars.content.getByName(ContentType.block, "unity-drive-shaft").overlaysprite = Core.atlas.find("unity-drive-shaft-overlay");