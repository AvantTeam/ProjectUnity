
const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");
const heatlib = require("libraries/heatlib");
const partslib = require("libraries/modularparts");


let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
graphLib.addGraph(blankobj, heatlib.baseTypesHeat.heatConnector);
Object.assign(blankobj.build, modturretlib.dcopy2(modturretlib.TurretModularBuild));
Object.assign(blankobj.block, modturretlib.dcopy2(modturretlib.TurretModularBlock));


const smallTurret = graphLib.finaliseExtendContent(Turret, Turret.TurretBuild, "small-turret-base", blankobj, {
	 load() {
        this.super$load();
        this.base = [Core.atlas.find(this.name+"1"),Core.atlas.find(this.name+"2"),Core.atlas.find(this.name+"3"),Core.atlas.find(this.name+"4")];
        this.partsAtlas = partslib.partIcons();
		this.baseSprite = Core.atlas.find(this.name + "-root");
		this.baseOutline = Core.atlas.find(this.name + "-root-outline");
		
    },
	init(){
		this.super$init();
		this.setConfigs();
		this.partsConfig = partslib.getPartList(["small"], 1,0, partslib.small, (part)=>{return true;});
	}
	
}, {
	getBaseSprite(){return smallTurret.baseSprite;},
	getBaseOutline(){return smallTurret.baseOutline;},
	getPartsConfig() {return smallTurret.partsConfig;},
    getPartsAtlas() {return smallTurret.partsAtlas;},
	getPartsCatagories() {return partslib.partCategories;},
	updatePre() {this.updateAutoBuild();},
	drawExt() {
        Draw.rect(smallTurret.base[Math.round(this.rotdeg()/90)], this.x, this.y, 0);
        this.drawTeamTop();
	}
});

smallTurret.rotate = true;
smallTurret.update = true;
smallTurret.solid = true;
smallTurret.configurable = true;
smallTurret.acceptsLiquids = false;
smallTurret.hasItems = true;
smallTurret.setGridWidth(3);
smallTurret.setGridHeight(3);
smallTurret.initBuildTimerId();
smallTurret.setSpriteGridSize(18);
smallTurret.setSpriteGridPadding(3);
smallTurret.setSpriteYscale(0.8);
smallTurret.getGraphConnectorBlock("torque graph").setAccept([1,1, 0,0, 0,0, 0,0]);
smallTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.03);
smallTurret.getGraphConnectorBlock("torque graph").setBaseInertia(50);
smallTurret.getGraphConnectorBlock("heat graph").setAccept([1,1, 1,1, 1,1, 1,1]);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.1);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.01);






// Medium Turret base


let mblankobj = graphLib.init();
graphLib.addGraph(mblankobj, rotL.baseTypes.torqueConnector);
graphLib.addGraph(mblankobj, heatlib.baseTypesHeat.heatConnector);
Object.assign(mblankobj.build, modturretlib.dcopy2(modturretlib.TurretModularBuild));
Object.assign(mblankobj.block, modturretlib.dcopy2(modturretlib.TurretModularBlock));


const medTurret = graphLib.finaliseExtendContent(Turret, Turret.TurretBuild, "med-turret-base", mblankobj, {
	 load() {
        this.super$load();
        this.base = [Core.atlas.find(this.name+"1"),Core.atlas.find(this.name+"2"),Core.atlas.find(this.name+"3"),Core.atlas.find(this.name+"4")];
        this.partsAtlas = partslib.partIcons();
		this.baseSprite = Core.atlas.find(this.name + "-root");
		this.baseOutline = Core.atlas.find(this.name + "-root-outline");
    },
	init(){
		this.super$init();
		this.setConfigs();
		this.partsConfig = partslib.getPartList(["small","medium"], 2,0, partslib.medium, (part)=>{return true;});
	}
	
}, {
	getBaseSprite(){return medTurret.baseSprite;},
	getBaseOutline(){return medTurret.baseOutline;},
	getPartsConfig() {return medTurret.partsConfig;},
    getPartsAtlas() {return medTurret.partsAtlas;},
	getPartsCatagories() {return partslib.partCategories;},
	updatePre() {this.updateAutoBuild();},
	drawExt() {
        Draw.rect(medTurret.base[Math.round(this.rotdeg()/90)], this.x, this.y, 0);
        this.drawTeamTop();
	}
});

medTurret.rotate = true;
medTurret.update = true;
medTurret.solid = true;
medTurret.configurable = true;
medTurret.acceptsItems = true;
medTurret.acceptsLiquids = false;
medTurret.hasItems = true;
medTurret.setGridWidth(5);
medTurret.setGridHeight(5);
medTurret.initBuildTimerId();
medTurret.setSpriteGridSize(16);
medTurret.setSpriteGridPadding(4);
medTurret.setSpriteYshift(0.8);
medTurret.setSpriteYscale(0.8);
medTurret.setCostAccum(0.12);
medTurret.getGraphConnectorBlock("torque graph").setAccept([0,1,0, 0,0,0, 0,0,0, 0,0,0]);
medTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.05);
medTurret.getGraphConnectorBlock("torque graph").setBaseInertia(150);
medTurret.getGraphConnectorBlock("heat graph").setAccept([1,1,1, 1,1,1, 1,1,1, 1,1,1]);
medTurret.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.05);
medTurret.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(120);
medTurret.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.02);










