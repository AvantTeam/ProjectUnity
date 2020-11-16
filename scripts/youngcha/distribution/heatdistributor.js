const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");




let hpblankobj = graphLib.init();
graphLib.addGraph(hpblankobj, heatlib.baseTypesHeat.heatConnector);

const heatPipe = graphLib.finaliseExtend(Block, Building,"heat-pipe",hpblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name+"-tiles");
	},
},{
	basespriteindex: 0,
	onNeighboursChanged(){
		let tgraph = this.getGraphConnector("heat graph");
		let culm = 0;
		let rot = this.rotation;
		let shift = [0,3,2,1];
		tgraph.eachNeighbour(function(n){
			culm += 1<<shift[n.portindex];
		});
		this.basespriteindex=culm;
	},
	updatePost(){
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		Draw.rect(heatlib.getRegion(heatPipe.bottom,this.basespriteindex,8,2), this.x, this.y, fixedrot);
		heatlib.drawHeat(heatlib.getRegion(heatPipe.heatsprite,this.basespriteindex,8,2),this.x, this.y,fixedrot, temp);
        this.drawTeamTop();
	}
});
heatPipe.update = true;
heatPipe.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1]);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.7);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(5);
heatPipe.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.008);