const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");



const basecolor = Color.valueOf("6e7080");
let hpblankobj = graphLib.init();
graphLib.addGraph(hpblankobj, heatlib.baseTypesHeat.heatConnector);

const heatPipe = graphLib.finaliseExtend(Block, Building,"heat-pipe",hpblankobj,{
	_timerid:0,
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name+"-tiles");
		this._timerid = this.timers++;
	},
	getTimerId(){
		return this._timerid;
	},
    drawRequestRegion(req, list) {
        const scl = Vars.tilesize * req.animScale;
        Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl, req.rotation * 90);
    },

},{
	basespriteindex: 0,
	onNeighboursChanged(){
		let tgraph = this.getGraphConnector("heat graph");
		let culm = 0;
		let shift = [0,3,2,1];
		tgraph.eachNeighbour(function(n){
			culm += 1<<shift[n.portindex];
		});
		this.basespriteindex=culm;
	},
	unitOn(unit){
		if(this.timer.get(this.block.getTimerId(), 20)){
			let temp = this.getGraphConnector("heat graph").getTemp();
			let intensity = Mathf.clamp(Mathf.map(temp,400,1000,0,1));
			unit.apply(StatusEffects.burning, intensity*20+5);
			unit.damage(intensity*10);
		}
	},
	updatePost(){
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(heatlib.getRegion(heatPipe.bottom,this.basespriteindex,8,2), this.x, this.y, 0);
		if(temp<273||temp>498){
			Draw.color(heatlib.getTempColor(temp).add(basecolor));
			Draw.rect(heatlib.getRegion(heatPipe.heatsprite,this.basespriteindex,8,2),this.x, this.y,0);
			Draw.color();
		}
        this.drawTeamTop();
	}
});
heatPipe.update = true;
heatPipe.solid = false;
heatPipe.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1]);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.7);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(5);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.008);




//radiators


let srblankobj = graphLib.init();
graphLib.addGraph(srblankobj, heatlib.baseTypesHeat.heatConnector);

const smallRadiator = graphLib.finaliseExtend(Block, Building,"small-radiator",srblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name);
	},
},{
	updatePost(){
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(smallRadiator.bottom, this.x, this.y, 0);
		heatlib.drawHeat(smallRadiator.heatsprite,this.x, this.y,0, temp);
        this.drawTeamTop();
	}
});
smallRadiator.update = true;
smallRadiator.solid = true;
smallRadiator.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1]);
smallRadiator.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.7);
smallRadiator.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(10);
smallRadiator.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.05);
