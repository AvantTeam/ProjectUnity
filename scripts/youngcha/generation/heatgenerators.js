const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");




let thblankobj = graphLib.init();
graphLib.addGraph(thblankobj, heatlib.baseTypesHeat.heatConnector);

const thermalHeater = graphLib.finaliseExtend(Block, Building,"thermal-heater",thblankobj,{
	load(){
		this.super$load();
		this.heatsprite = Core.atlas.find(this.name+"-heat");
		this.bottom = [Core.atlas.find(this.name)+1,Core.atlas.find(this.name)+2,Core.atlas.find(this.name)+3,Core.atlas.find(this.name)+4];
	},
	attribute: Attribute.heat,
	
	getTerrainAttrib(){return this.attribute},
	
	canPlaceOn( tile,  team){
        return tile.getLinkedTilesAs(this, this.tempTiles).sumf(other => other.floor().attributes.get(this.attribute)) > 0.01;
    }
},{
	sum:0,
	updatePost(){
		let eff = this.sum + this.block.getTerrainAttrib().env();
		let hgraph = this.getGraphConnector("heat graph");
		let temp = hgraph.getTemp();
		hgraph.setHeat(hgraph.getHeat()+ Math.max(0,1973-temp)*0.04);
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(thermalHeater.bottom[this.rotation], this.x, this.y, 0);
		heatlib.drawHeat(thermalHeater.heatsprite,this.x, this.y,this.rotdeg(), temp);
        this.drawTeamTop();
	},
	onProximityAdded(){
		this.super$onProximityAdded();
		this.sum = this.block.sumAttribute( this.block.getTerrainAttrib(), this.tile.x, this.tile.y);
	}
});
thermalHeater.update = true;
thermalHeater.rotate = true;
thermalHeater.getGraphConnectorBlock("heat graph").setAccept( [1,1, 0,0, 0,0, 0,0]);
thermalHeater.getGraphConnectorBlock("heat graph").getBaseHeatConductivity(0.6);
thermalHeater.getGraphConnectorBlock("heat graph").getBaseHeatCapacity(20);
