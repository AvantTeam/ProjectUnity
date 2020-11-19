const graphLib = require("libraries/graphlib");
const rotL = require("libraries/rotpowerlib");
const heatlib = require("libraries/heatlib");


//graph
const magnetblock = Object.assign(Object.create(graphLib.graphCommon), {

	baseflux:0,
	fluxproducer:true,
	getFlux(){
		return this.baseflux;
	},
	setFlux(s){
		this.baseflux=s;
	},
	getFluxproducer(){
		return this.fluxproducer;
	},
	setFluxproducer(s){
		this.fluxproducer=s;
	},
	setStats(table) {
		table.row();
		table.left();
        table.add("[lightgray]" + Core.bundle.get("stat.unity.flux") + ":[] ").left();
		table.add(this.getFlux()+"Wb");
	},
});
const magnetbuild = Object.assign(Object.create(graphLib.graphProps),{
	flux:0,
	getFlux(){
		return this.flux;
	},
	setFlux(s){
		this.flux=s;
	},
	initStats(){
		this.setFlux(this.getBlockData().getFlux());
	},
	display(table) {
        if (!this._network) {
            return;
        }
        let ps = " Wb";
        let net = this._network;
        table.row();
        table.table(
            cons(sub => {
                sub.clearChildren();
                sub.left();
                sub.label(prov(() => {
                    return Strings.fixed(net.flux, 2) + ps;
                })).color(Color.lightGray);
            })
        ).left();
    },
});
const magnetgraph = {
	flux:0,
	fluxtotal:0,
	getFlux(){
		return this.flux;
	},
	setFlux(s){
		this.flux=s;
	},
	updateGraph() {
		this.fluxtotal = 0;
		let totalmags = 0;
		this.connected.each(cons(building => {
            this.fluxtotal += building.getFlux();
			if(building.getBlockData().getFluxproducer()){
				totalmags++;
			}
        }));
		if(totalmags==0){totalmags=1;}
		
		let weight = 1;
		if(totalmags>1){
			weight = 1.5*totalmags/(Math.log10(totalmags)+1) -0.5;
		}
		
		this.flux = (this.fluxtotal)/weight;
	},
	mergeStats(graph){
		//nothing indirectly derived (e.g. rotlib angular velocity) so nothing here.
	},
};
const magnetgraphtype = {
	block:magnetblock,
	build:magnetbuild,
	graph:magnetgraph,
}
graphLib.setGraphName(magnetgraphtype,"flux graph");


//bullet bending,
const magbehaviour = {
	updatePost(){
		let f = this.getGraphConnector("flux graph").getFlux();
		 Groups.bullet.intersect(this.x - f*2, this.y - f*2, f * 4, f * 4, cons(bullet=>{
			 if(bullet.type == null || !bullet.type.hittable){
				 return;
			 }
			 let dx = bullet.x-this.x;
			 let dy = bullet.y-this.y;
			 let ldis = dx*dx+dy*dy;
			 if(ldis<f*f*4){
				 ldis = Math.sqrt(ldis);
				 let invmass = 1.0/Math.max(1.0,bullet.type.estimateDPS()/10.0);
				 let forcemag = Time.delta*0.1*f/(8+ldis);
				 bullet.vel.x += forcemag*graphLib.dirs[this.rotation].x*invmass;
				 bullet.vel.y += forcemag*graphLib.dirs[this.rotation].y*invmass;
			 }
		 }));
	}
	
}

//----------------------------------------------------------------------------------------------------------------------------------

//adding a block
let nsblankobj = graphLib.init();
graphLib.addGraph(nsblankobj, magnetgraphtype);
Object.assign(nsblankobj.build,magbehaviour);
const nickelStator = graphLib.finaliseExtend(Block, Building,"nickel-stator",nsblankobj,{
	
	load(){
		this.super$load();
		this.basesprite = [Core.atlas.find(this.name),Core.atlas.find(this.name+2),Core.atlas.find(this.name+3),Core.atlas.find(this.name+4)];
	},
	
},{
	updatePre(){},
	draw() {
		Draw.rect(nickelStator.basesprite[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();
	}
	
});

nickelStator.rotate = true;
nickelStator.update = true;
nickelStator.solid = true;
nickelStator.getGraphConnectorBlock("flux graph").setAccept( [1,0,0,0]);
nickelStator.getGraphConnectorBlock("flux graph").setFlux(2);


//----------------------------------------------------------------------------------------------------------------------------------

//adding a block
let nslblankobj = graphLib.init();
graphLib.addGraph(nslblankobj, magnetgraphtype);
Object.assign(nslblankobj.build,magbehaviour);
const nickelStatorLarge = graphLib.finaliseExtend(Block, Building,"nickel-stator-large",nslblankobj,{
	
	load(){
		this.super$load();
		this.basesprite = [Core.atlas.find(this.name),Core.atlas.find(this.name+2),Core.atlas.find(this.name+3),Core.atlas.find(this.name+4)];
	},
	
},{
	updatePre(){},
	draw() {
		Draw.rect(nickelStatorLarge.basesprite[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();
	}
});

nickelStatorLarge.rotate = true;
nickelStatorLarge.update = true;
nickelStatorLarge.solid = true;
nickelStatorLarge.getGraphConnectorBlock("flux graph").setAccept( [1,1,0,0,0,0,0,0]);
nickelStatorLarge.getGraphConnectorBlock("flux graph").setFlux(10);



//----------------------------------------------------------------------------------------------------------------------------------

//adding a block
let nemblankobj = graphLib.init();
graphLib.addGraph(nemblankobj, magnetgraphtype);
Object.assign(nemblankobj.build,magbehaviour);
const nickelElectromagnet = graphLib.finaliseExtend(Block, Building,"nickel-electromagnet",nemblankobj,{
	
	load(){
		this.super$load();
		this.basesprite = [Core.atlas.find(this.name),Core.atlas.find(this.name+2),Core.atlas.find(this.name+3),Core.atlas.find(this.name+4)];
	},
	
},{
	updatePre(){
		this.getGraphConnector("flux graph").setFlux(this.power.graph.getSatisfaction() * this.block.getGraphConnectorBlock("flux graph").getFlux());
	},
	draw() {
		Draw.rect(nickelElectromagnet.basesprite[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();
	}
});

nickelElectromagnet.rotate = true;
nickelElectromagnet.update = true;
nickelElectromagnet.solid = true;
nickelElectromagnet.consumes.power(1.6);
nickelElectromagnet.getGraphConnectorBlock("flux graph").setAccept( [1,1,0,0,0,0,0,0]);
nickelElectromagnet.getGraphConnectorBlock("flux graph").setFlux(25);




//----------------------------------------------------------------------------------------------------------------------------------
//ROTORS
//----------------------------------------------------------------------------------------------------------------------------------

const rotorBlock = {
	_baseTopSpeed: 20,
	_baseTorque: 5,
	_torqueEfficiency: 1.0, // how much extra flux get converted into extra torque                                                         usually around 2        a value of one will increase max torque by 1 for each unit of flux.
	_fluxEfficiency: 1.0, // how much added magnets will decrease top speed, higher is better.                                             usually around ~8        a value of one will decrease top speed by half for each flux unit.
	_rotPowerEfficiency: 1.0, // how good it converts rotation to power, power produced caps at the value of PowerGenerator.powerProduction   usually around ~0.2     a value of one will produce the maximum power for 0.1r/s above top speed.
	getTorqueEfficiency(){return this._torqueEfficiency;},
	setTorqueEfficiency(s){this._torqueEfficiency=s;},
	getRotPowerEfficiency(){return this._rotPowerEfficiency;},
	setRotPowerEfficiency(s){this._rotPowerEfficiency=s;},
	getFluxEfficiency(){return this._fluxEfficiency;},
	setFluxEfficiency(s){this._fluxEfficiency=s;},
	getBaseTorque(){return this._baseTorque;},
	setBaseTorque(s){this._baseTorque=s;},
	getBaseTopSpeed(){return this._baseTopSpeed;},
	setBaseTopSpeed(s){this._baseTopSpeed=s;},
}
const rotorBuild = {
	displayBarsExt(barsTable){	
        let block = this.block;
		let flux = this.getGraphConnector("flux graph").getNetwork().getFlux();
		let tgraph = this.getGraphConnector("torque graph");
		let mtorque = flux * this.block.getTorqueEfficiency()* this.block.getBaseTorque();
		
        barsTable.add(new Bar(
            prov(() => Core.bundle.format("bar.poweroutput", Strings.fixed((this.getPowerProduction() - block.consumes.getPower().usage) * 60 * this.timeScale, 1))),
            prov(() => Pal.powerBar),
            floatp(() => this.productionEfficiency))).growX();
        barsTable.row();
		
		barsTable.add(new Bar(
            prov(() => Core.bundle.get("stat.unity.torque") + ": " + Strings.fixed(tgraph.getForce(), 1) + "/" + Strings.fixed(mtorque, 1)),
            prov(() => Pal.darkishGray),
            floatp(() => tgraph.getForce() / mtorque))).growX();
        barsTable.row();
		
		barsTable.add(new Bar(
            prov(() => Core.bundle.get("stat.unity.maxspeed")+":"+ Strings.fixed(this.getTopSpeed()/6.0,1)+"r/s" ),
            prov(() => Pal.darkishGray),
            floatp(() => this.getTopSpeed()/this.block.getBaseTopSpeed()  ))).growX();
        barsTable.row();
		
	},
	
	getTopSpeed(){
		if(!this.getGraphConnector("flux graph").getNetwork()){return 1;}
		let flux = this.getGraphConnector("flux graph").getNetwork().getFlux();
		return this.block.getBaseTopSpeed()/ (1.0+ flux/this.block.getFluxEfficiency());
	},
	
	updatePre(){
		let blkdata = this.block.getGraphConnectorBlock("torque graph");
		let fric = blkdata.getBaseFriction();
		let flux = this.getGraphConnector("flux graph").getNetwork().getFlux();
		let topspeed =  this.block.getBaseTopSpeed()/ (1.0+ flux/this.block.getFluxEfficiency());
		let breakeven  = this.block.consumes.getPower().usage/this.block.powerProduction;
		
		let tgraph = this.getGraphConnector("torque graph");
		let rotvel = tgraph.getNetwork().lastVelocity;
		if(!rotvel){
			rotvel=0;
		}
		if(rotvel>topspeed){
			this.productionEfficiency = breakeven + (1.0-breakeven) * Mathf.clamp((rotvel - topspeed)*this.block.getRotPowerEfficiency());
			tgraph.setFriction(fric);
		}else{
			this.productionEfficiency = Mathf.pow((rotvel/topspeed)*breakeven,3);
			tgraph.setFriction(fric*0.5 + ((rotvel/topspeed)*fric*0.5) );
		}
		let mul = Mathf.clamp(1.0-(rotvel/topspeed),-1.0,1.0);
		tgraph.setForce(mul*flux * this.block.getTorqueEfficiency()* this.block.getBaseTorque()* this.edelta());
	},
}



let erblankobj = graphLib.init();
graphLib.addGraph(erblankobj, magnetgraphtype); //flux graph
graphLib.addGraph(erblankobj, rotL.baseTypes.torqueConnector);  //rotlib graph

Object.assign(erblankobj.block, rotorBlock);
Object.assign(erblankobj.build,rotorBuild);
const electricRotor = graphLib.finaliseExtendContent(PowerGenerator, PowerGenerator.GeneratorBuild,"electric-rotor",erblankobj,{
	load(){
		this.super$load();
		this.top = [Core.atlas.find(this.name+"-top1"),Core.atlas.find(this.name+"-top2"),Core.atlas.find(this.name+"-top3"),Core.atlas.find(this.name+"-top4")];
		this.overlaysprite = Core.atlas.find(this.name+"-overlay");
		this.rotor = Core.atlas.find(this.name+"-rotor");
		this.bottom = Core.atlas.find(this.name+"-bottom");
	},
	
},{
	updatePost(){
		this.getGraphConnector("torque graph").setInertia(150);
	},
	draw() {
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let tgraph = this.getGraphConnector("torque graph");
		let variant = ((this.rotation+1)%4>=2)?1:0;
		let shaftRot = variant==1?360-tgraph.getRotation():tgraph.getRotation();
		Draw.rect(electricRotor.bottom, this.x, this.y, fixedrot);
		rotL.drawRotRect(electricRotor.rotor, this.x, this.y, 24, 15, 24, this.rotdeg(), shaftRot, shaftRot+90);
		rotL.drawRotRect(electricRotor.rotor, this.x, this.y, 24, 15, 24, this.rotdeg(), shaftRot+120, shaftRot+210);
		rotL.drawRotRect(electricRotor.rotor, this.x, this.y, 24, 15, 24, this.rotdeg(), shaftRot+240, shaftRot+330);
		Draw.rect(electricRotor.overlaysprite, this.x, this.y, fixedrot);
		Draw.rect(electricRotor.top[this.rotation], this.x, this.y, 0);
        this.drawTeamTop();
	}
});

electricRotor.rotate = true;
electricRotor.update = true;
electricRotor.solid = true;
electricRotor.outputsPower = electricRotor.consumesPower = true;
electricRotor.consumes.power(8.0);
electricRotor.getGraphConnectorBlock("flux graph").setAccept( [0,0,0, 1,1,1, 0,0,0, 1,1,1]);
electricRotor.getGraphConnectorBlock("flux graph").setFluxproducer(false);
electricRotor.getGraphConnectorBlock("torque graph").setAccept([0,1,0, 0,0,0, 0,1,0, 0,0,0]);
electricRotor.getGraphConnectorBlock("torque graph").setBaseFriction(0.05);
electricRotor.setFluxEfficiency(10.0);
electricRotor.setRotPowerEfficiency(0.25);
electricRotor.setTorqueEfficiency(0.8);
electricRotor.setBaseTorque(5.0);
electricRotor.setBaseTopSpeed(15.0);
electricRotor.powerProduction = 24.0;