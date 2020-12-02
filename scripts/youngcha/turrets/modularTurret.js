
const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");
const heatlib = require("libraries/heatlib");
const partslib = require("libraries/modularparts");


function getPart(name){
	for(let i = 0;i<partinfo.length;i++){
		if(partinfo[i].name==name){
			return partinfo[i];
		}
	}
	return null;
}


let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
graphLib.addGraph(blankobj, heatlib.baseTypesHeat.heatConnector);
Object.assign(blankobj.build, modturretlib.dcopy2(modturretlib.TurretModularBuild));
Object.assign(blankobj.block, modturretlib.dcopy2(modturretlib.TurretModularBlock));


const smallTurret = graphLib.finaliseExtendContent(Turret, Turret.TurretBuild, "small-turret-base", blankobj, {
	 load() {
        this.super$load();
        this.base = Core.atlas.find(this.name);
        this.partsAtlas = partslib.partIcons();
        this.setConfigs();
		this.baseSprite = Core.atlas.find(this.name + "-root");
		this.baseOutline = Core.atlas.find(this.name + "-root-outline");
		this.partsConfig = partslib.getPartList(["small"], 1,0, partslib.small, (part)=>{return true;});
    },
	
}, {
	aniprog: 0,
    anitime: 0,
    anispeed: 0,
	getBaseSprite(){
		return smallTurret.baseSprite;
	},
	getBaseOutline(){
		return smallTurret.baseOutline;
	},
	getPartsConfig() {
        return smallTurret.partsConfig;
    },
    getPartsAtlas() {
        return smallTurret.partsAtlas;
    },
	getPartsCatagories() {
        return partslib.partCategories;
    },
	updatePre() {
		this.anitime += Time.delta;
		var prog = this.getPaidRatio();
        if(this.aniprog < prog) {
            this.anispeed = (prog - this.aniprog) * 0.1;
            this.aniprog += this.anispeed;
        }
        else {
            this.aniprog = prog;
            this.anispeed = 0;
        }
		this.updateAutoBuild();
	},
	drawExt() {
        Draw.rect(smallTurret.base, this.x, this.y, 0);
		
        this.drawTeamTop();
	}
});

smallTurret.rotate = true;
smallTurret.update = true;
smallTurret.solid = true;
smallTurret.configurable = true;
smallTurret.acceptsItems = true;
smallTurret.hasItems = true;
smallTurret.setGridWidth(3);
smallTurret.setGridHeight(3);
smallTurret.initBuildTimerId();
smallTurret.setSpriteGridSize(18);
smallTurret.setSpriteGridPadding(3);
smallTurret.getGraphConnectorBlock("torque graph").setAccept([1,1, 0,0, 0,0, 0,0]);
smallTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.03);
smallTurret.getGraphConnectorBlock("torque graph").setBaseInertia(50);
smallTurret.getGraphConnectorBlock("heat graph").setAccept([1,1, 1,1, 1,1, 1,1]);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.1);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.01);



