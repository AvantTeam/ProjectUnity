const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");




let hpblankobj = graphLib.init();
graphLib.addGraph(hpblankobj, heatlib.baseTypesHeat.heatConnector);

const heatPipe = graphLib.finaliseExtend(Block, Building,"heat-pipe",hpblankobj,{
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
		let fixedrot = ((this.rotdeg()+90)%180)-90;
		let variant = ((this.rotation+1)%4>=2)?1:0;
		Draw.rect(heatPipe.bottom, this.x, this.y, fixedrot);
		heatlib.drawHeat(heatPipe.heatsprite,this.x, this.y,fixedrot, temp);
        this.drawTeamTop();
	}
});
heatPipe.update = true;
heatPipe.rotate = true;
heatPipe.getGraphConnectorBlock("heat graph").setAccept( [1,0,1,0]);
heatPipe.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.7);
heatPipe.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(2);
