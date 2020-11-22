const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");

let spblankobj = graphLib.init();
graphLib.addGraph(spblankobj, heatlib.baseTypesHeat.heatConnector);

const sporePyrolyser = graphLib.finaliseExtendContent(GenericCrafter, GenericCrafter.GenericCrafterBuild,"spore-pyrolyser",spblankobj,{
	_timerid:0,
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = Core.atlas.find(this.name);
		this._timerid = this.timers++;
	},
	getTimerId(){
		return this._timerid;
	}
},{
	getProgressIncrease(baseTime) {
		let temp = this.getGraphConnector("heat graph").getTemp();
        return Mathf.sqrt(Mathf.clamp((temp-370.0)/300.0)) / baseTime * this.edelta();
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(sporePyrolyser.bottom, this.x, this.y, 0);
		heatlib.drawHeat(sporePyrolyser.heatsprite,this.x, this.y,0, temp*1.5);
        this.drawTeamTop();
	}
});
sporePyrolyser.update = true;
sporePyrolyser.solid = true;
sporePyrolyser.getGraphConnectorBlock("heat graph").setAccept( [1,1,1, 1,1,1, 1,1,1, 1,1,1]);
sporePyrolyser.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.4);
sporePyrolyser.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(60);
sporePyrolyser.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.008);