const heatlib = require("libraries/heatlib");
const graphLib = require("libraries/graphlib");

let cwblankobj = graphLib.init();
graphLib.addGraph(cwblankobj, heatlib.baseTypesHeat.heatConnector);

const cupronickelWall = graphLib.finaliseExtend(Block, Building,"cupronickel-wall",cwblankobj,{
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
	updatePost(){
		if(this.timer.get(this.block.getTimerId(), 60)){
			let temp = this.getGraphConnector("heat graph").getTemp();
			let intensity = Mathf.clamp(Mathf.map(temp,400,1000,0,1));
			Damage.status(this.team, this.x, this.y, intensity*20.0+4, StatusEffects.burning, 3+intensity*20, false, true);
		}
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(cupronickelWall.bottom, this.x, this.y, 0);
		heatlib.drawHeat(cupronickelWall.heatsprite,this.x, this.y,0, temp);
        this.drawTeamTop();
	}
});
cupronickelWall.update = true;
cupronickelWall.solid = true;
cupronickelWall.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1]);
cupronickelWall.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.5);
cupronickelWall.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
cupronickelWall.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.03);


let cwlblankobj = graphLib.init();
graphLib.addGraph(cwlblankobj, heatlib.baseTypesHeat.heatConnector);
const cupronickelWallLarge = graphLib.finaliseExtend(Block, Building,"cupronickel-wall-large",cwlblankobj,{
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
	updatePost(){
		if(this.timer.get(this.block.getTimerId(), 120)){
			let temp = this.getGraphConnector("heat graph").getTemp();
			let intensity = Mathf.clamp(Mathf.map(temp,400,1000,0,1));
			Damage.status(this.team, this.x, this.y, intensity*40.0+8, StatusEffects.burning, 5+intensity*60, false, true);
			Damage.damage(this.team, this.x, this.y, intensity*10.0+8, intensity*20, false, true);
		}
	},
	draw() {
		let temp = this.getGraphConnector("heat graph").getTemp();
		Draw.rect(cupronickelWallLarge.bottom, this.x, this.y, 0);
		heatlib.drawHeat(cupronickelWallLarge.heatsprite,this.x, this.y,0, temp);
        this.drawTeamTop();
	}
});
cupronickelWallLarge.update = true;
cupronickelWallLarge.solid = true;
cupronickelWallLarge.getGraphConnectorBlock("heat graph").setAccept( [1,1,1,1,1,1,1,1]);
cupronickelWallLarge.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.5);
cupronickelWallLarge.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(200);
cupronickelWallLarge.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.09);