const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");


const partinfo = 
[
	{
		name:"Pivot",
		desc:"",
		category:"Blade",
		tx:4,
		ty:0,
		tw:1,
		th:1,
		cannotPlace:true,
		prePlace:{
			x:0,
			y:0,
		},
		isRoot:true,
		cost:[
		],
		connectOut:[1,0,0,0],
		connectIn:[0,0,0,0]
	},
	{
		name:"Blade",
		desc:"Slices and knocks back enemies",
		category:"Blade",
		tx:0,
		ty:0,
		tw:1,
		th:1,
		cost:[
			{
				name:"unity-nickel",
				amount: 3
			},
			{
				name:"titanium",
				amount: 5
			}
		],
		connectOut:[1,0,0,0],
		connectIn:[0,0,1,0],
	},
	{
		name:"Serrated blade",
		desc:"A heavy reinforced blade.",
		category:"Blade",
		tx:2,
		ty:0,
		tw:2,
		th:1,
		cost:[
			{
				name:"unity-nickel",
				amount: 8
			},
			{
				name:"lead",
				amount: 5
			}
		],
		connectOut:[1, 0,0, 0, 0,0],
		connectIn:[0, 0,0, 1, 0,0],
	},
	{
		name:"Rod",
		desc:"Supporting structure, does not collide",
		category:"Blade",
		tx:1,
		ty:0,
		tw:1,
		th:1,
		cost:[
			{
				name:"titanium",
				amount: 3
			}
		],
		connectOut:[1,0,0,0],
		connectIn:[0,0,1,0],
	},
	
];
let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
Object.assign(blankobj.build,modturretlib.dcopy2(modturretlib.ModularBuild));
Object.assign(blankobj.block,modturretlib.dcopy2(modturretlib.ModularBlock));
const chopperTurret = graphLib.finaliseExtend(Block, Building, "chopper", blankobj, {

	load(){
		this.super$load();
		this.topsprite = Core.atlas.find(this.name + "-top");
		this.base = Core.atlas.find(this.name + "-base");
		this.partsAtlas = Core.atlas.find(this.name + "-parts");
		
		this.setConfigs();
		

	},
},{
	getPartsConfig(){
		return partinfo;
	},
	getPartsAtlas(){
		return chopperTurret.partsAtlas;
	},
	updatePre()
	{
		this.getGraphConnector("torque graph").setInertia(3);
		this.getGraphConnector("torque graph").setFriction(0.01);
	},

	draw() {
		let tgraph = this.getGraphConnector("torque graph");
		Draw.rect(chopperTurret.base, this.x, this.y, this.rotdeg());
		//speeeeeeen tgraph.getRotation()
		let blades = this.getBufferRegion();
		if(blades){
			Draw.z(Layer.turret);
			Draw.rect(blades, this.x+blades.width*0.125, this.y, blades.width*0.25,blades.height*0.25,0,blades.height*0.5*0.25,tgraph.getRotation());
			Draw.rect(chopperTurret.topsprite, this.x, this.y, 0);
		}
		
        this.drawTeamTop();
	}


});

chopperTurret.rotate = true;
chopperTurret.update = true;
chopperTurret.solid = true;
chopperTurret.configurable = true;
chopperTurret.setGridWidth(7);
chopperTurret.setGridHeight(1);
chopperTurret.getGraphConnectorBlock("torque graph").setAccept([1,0,0,0]);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.01);
chopperTurret.getGraphConnectorBlock("torque graph").setBaseInertia(3);