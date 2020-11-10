const graphLib = require("libraries/graphlib");

//graph
const magnetblock = Object.assign(Object.create(graphLib.graphCommon), {

	baseflux:0,
	getFlux(){
		return this.baseflux;
	},
	setFlux(s){
		this.baseflux=s;
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
			totalmags++;
        }));
		if(totalmags==0){totalmags=1;}
		this.flux =this.fluxtotal/totalmags;
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
				 let forcemag = Time.delta*0.1*f/(8+ldis);
				 bullet.vel.x += forcemag*graphLib.dirs[this.rotation].x;
				 bullet.vel.y += forcemag*graphLib.dirs[this.rotation].y;
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
nickelElectromagnet.consumes.power(4);
nickelElectromagnet.getGraphConnectorBlock("flux graph").setAccept( [1,1,0,0,0,0,0,0]);
nickelElectromagnet.getGraphConnectorBlock("flux graph").setFlux(25);





