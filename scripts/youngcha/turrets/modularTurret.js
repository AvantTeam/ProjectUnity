
const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");
const heatlib = require("libraries/heatlib");

const partinfo = [
	{
        name: "Pivot",
        desc: "",
        category: "none",
        tx: 0,
        ty: 3,
        tw: 1,
        th: 1,
        cannotPlace: true,
        prePlace: {
            x: 1,
            y: 0,
        },
        isRoot: true,
        cost: [],
        connectOut: [6, 1, 6, 0],
        connectIn: [0, 0, 0, 0],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
        }

    },
	
	{
        name: "Gun base",
        desc: "A basic gun base, very extendable",
        category: "base",
        tx: 0,
        ty: 2,
        tw: 1,
        th: 1,
        cost: [
			{
                name: "unity-nickel",
                amount: 10
            },
            {
                name: "titanium",
                amount: 5
            },
		],
        connectOut: [5, 2, 5, 0],
        connectIn: [0, 0, 0, 1],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			support: {
                name: "stat.unity.supports",
                value: "[accent]1x [white]small turret",
            },
			reload: {
				name: "stat.unity.reload",
				value: 10,
			}
        }

    },
	
	{
        name: "Rotary Gun base",
        desc: "A spinning gun base that can fire shots at intense speed, given enough ammo and torque. Its size makes it difficult to modify.",
        category: "base",
        tx: 1,
        ty: 2,
        tw: 3,
        th: 1,
        cost: [
			{
                name: "unity-nickel",
                amount: 8
            },
            {
                name: "titanium",
                amount: 10
            },
			{
                name: "graphite",
                amount: 5
            },
		],
        connectOut: [0, 2,2,2, 0, 0,0,0],
        connectIn: [0, 0,0,0, 0, 0,1,0],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 30,
            },
			support: {
                name: "stat.unity.supports",
                value: "[accent]3x [white]small turret",
            },
			reload: {
				name: "stat.unity.reload",
				value: 2
			},
			mass: {
                name: "stat.unity.blademass",
                value: 20,
            },
			useTorque: {
                name: "stat.unity.usesTorque",
                value: true,
            },
        }

    },
	
	
	{
        name: "Gun breach",
        desc: "Run of the mill breach, Accepts and fires simple shots",
        category: "breach",
        tx: 0,
        ty: 1,
        tw: 1,
        th: 1,
        cost: [
			{
                name: "unity-nickel",
                amount: 4
            },
		],
        connectOut: [0,3,0,0],
        connectIn: [0,0,0,2],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			bulletType: {
                name: "stat.unity.bulletType",
                value: "normal",
            },
			baseDmg:{
				name: "stat.unity.bulletDmg",
                value: 15,
			},
			baseSpeed:{
				name: "stat.unity.bulletSpd",
                value: 4,
			},
			ammoType:{
				name: "stat.unity.ammoType",
                value: "normal",
			},
			payload:{
				name: "stat.unity.payload",
                value: 1,
			},
			magazine:{
				name: "stat.unity.magazine",
                value: 3,
			},
			shots:{
				name: "stat.unity.shots",
                value: 1,
			},
			reloadMultiplier:{
				name: "stat.unity.reloadMult",
                value: 1,
			},
			spread:{
				name: "stat.unity.spread",
                value: 10,
			},
			lifetime:{
				name: "stat.unity.lifetime",
                value: 35,
			},
			
        },
		

    },
	
	
	
	{
        name: "Grenade breach",
        desc: "Accepts and fires bouncing grenades",
        category: "breach",
        tx: 1,
        ty: 1,
        tw: 1,
        th: 1,
        cost: [
			{
                name: "unity-nickel",
                amount: 5
            },
			{
                name: "graphite",
                amount: 5
            },
		],
        connectOut: [0,3,0,0],
        connectIn: [0,0,0,2],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			bulletType: {
                name: "stat.unity.bulletType",
                value: "grenade",
            },
			baseDmg:{
				name: "stat.unity.bulletDmg",
                value: 5,
			},
			baseSpeed:{
				name: "stat.unity.bulletSpd",
                value: 3,
			},
			ammoType:{
				name: "stat.unity.ammoType",
                value: "explosive",
			},
			payload:{
				name: "stat.unity.payload",
                value: 1,
			},
			magazine:{
				name: "stat.unity.magazine",
                value: 2,
			},
			shots:{
				name: "stat.unity.shots",
                value: 1,
			},
			reloadMultiplier:{
				name: "stat.unity.reloadMult",
                value: 3.5,
			},
			spread:{
				name: "stat.unity.spread",
                value: 20,
			},
			lifetime:{
				name: "stat.unity.lifetime",
                value: 150,
			},
			mod: {
                name: "stat.unity.mod",
                value: "Explosive",
				cons: cons((config)=>{
					config.splashDamage = 45;
					config.splashDamageRadius = 25.0;
				})
            },
			
        },
		

    },
	
	
	{
        name: "Cannon breach",
        desc: "Accepts and fires large shells",
        category: "breach",
        tx: 2,
        ty: 1,
        tw: 1,
        th: 1,
        cost: [
			{
                name: "titanium",
                amount: 8
            },
			{
                name: "lead",
                amount: 4
            },
		],
        connectOut: [0,3,0,0],
        connectIn: [0,0,0,2],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			bulletType: {
                name: "stat.unity.bulletType",
                value: "shell",
            },
			baseDmg:{
				name: "stat.unity.bulletDmg",
                value: 85,
			},
			baseSpeed:{
				name: "stat.unity.bulletSpd",
                value: 10,
			},
			ammoType:{
				name: "stat.unity.ammoType",
                value: "heavy",
			},
			payload:{
				name: "stat.unity.payload",
                value: 1,
			},
			magazine:{
				name: "stat.unity.magazine",
                value: 2,
			},
			shots:{
				name: "stat.unity.shots",
                value: 1,
			},
			reloadMultiplier:{
				name: "stat.unity.reloadMult",
                value: 6.5,
			},
			spread:{
				name: "stat.unity.spread",
                value: 7,
			},
			lifetime:{
				name: "stat.unity.lifetime",
                value: 25,
			},
			mod: {
                name: "stat.unity.mod",
                value: "Piercing",
				cons: cons((config)=>{
					config.pierce = 2;
				})
            },
			
        },
		

    },
	
	{
        name: "Ammo Packet",
        desc: "Makes the gun shoot more bullet at once",
        category: "ammo",
        tx: 3,
        ty: 1,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "metaglass",
                amount: 8
            },
			{
                name: "graphite",
                amount: 8
            },
			{
                name: "titanium",
                amount: 8
            },
		],
        connectOut: [0,0,0,0],
        connectIn: [5,0,5,0],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 15,
            },
			mod: {
                name: "stat.unity.mod",
                value: "+1 ammo size",
				cons: cons((config)=>{
					if(!config.shots){
						config.shots=1;
					}
					if(!config.ammoCostMul){
						config.ammoCostMul=1;
					}
					if(!config.reloadmult){
						config.reloadmult=1;
					}
					config.shots = config.shots+1;
					config.reloadmult *= config.shots/(config.shots-1);
					config.ammoCostMul*= config.shots/(config.shots-1);
				})
            },
        }

    },
	
	{
        name: "Incendiary Modifier",
        desc: "Makes the gun's bullets spark fires.",
        category: "ammo",
        tx: 4,
        ty: 0,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "unity-cupronickel",
                amount: 8
            },
			{
                name: "graphite",
                amount: 5
            },
		],
        connectOut: [0,0,0,0],
        connectIn: [5,0,5,0],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			mod: {
                name: "stat.unity.mod",
                value: "Incendiary",
				cons: cons((config)=>{
					config.incindiary = true;
					config.status = StatusEffects.burning;
					if(!config.ammoType){
						config.ammoType={};
					}
					if(!config.ammoType["fire"]){
						config.ammoType["fire"] = 1;
					}
				})
            },
        }

    },
	
	{
        name: "Homing Modifier",
        desc: "Makes the gun's bullets spark fires.",
        category: "ammo",
        tx: 4,
        ty: 2,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "graphite",
                amount: 8
            },
			{
                name: "titanium",
                amount: 5
            },
			{
                name: "silicon",
                amount: 10
            },
		],
        connectOut: [0,0,0,0],
        connectIn: [5,0,5,0],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 10,
            },
			mod: {
                name: "stat.unity.mod",
                value: "Homing",
				cons: cons((config)=>{
					if(!config.homingPower){
						config.homingPower = 0.04;
						config.homingRange = 35;
					}else{
						config.homingPower += 0.02;
						config.homingRange += 15;
					}
					if(!config.ammoType){
						config.ammoType={};
					}
					if(!froms.ammoType["homing"]){
						froms.ammoType["homing"] = 1;
					}else{
						froms.ammoType["homing"] += 1;
					}
				})
            },
        }
    },
	
	{
        name: "Armour Plate",
        desc: "Reinforces the turret",
        category: "misc",
        tx: 2,
        ty: 3,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "titanium",
                amount: 10
            },
		],
        connectOut: [6,6,6,6],
        connectIn: [6,6,6,6],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 100,
            },
        }

    },
	
	{
        name: "Radiator",
        desc: "Dissipates waste heat passively",
        category: "misc",
        tx: 3,
        ty: 3,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "unity-cupronickel",
                amount: 10
            },
		],
        connectOut: [6,6,6,6],
        connectIn: [6,6,6,6],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 5,
            },
			radiate: {
                name: "stat.unity.heatRadiativity",
                value: 0.03,
            },
        }

    },
	
	{
        name: "Rangefinder",
        desc: "Increases the range of the turret",
        category: "misc",
        tx: 4,
        ty: 3,
        tw: 1,
        th: 1,
        cost: [
            {
                name: "graphite",
                amount: 15
            },
			{
                name: "metaglass",
                amount: 10
            },
			{
                name: "copper",
                amount: 5
            },
		],
        connectOut: [6,6,6,6],
        connectIn: [6,6,6,6],
        stats: {
            hp: {
                name: "stat.unity.hpinc",
                value: 5,
            },
			rangeinc: {
                name: "stat.unity.range",
                value: 50,
            },
        }

    },
	
	
];

modturretlib.preCalcConnection(partinfo);

let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
graphLib.addGraph(blankobj, heatlib.baseTypesHeat.heatConnector);
Object.assign(blankobj.build, modturretlib.dcopy2(modturretlib.TurretModularBuild));
Object.assign(blankobj.block, modturretlib.dcopy2(modturretlib.ModularBlock));


const smallTurret = graphLib.finaliseExtendContent(Turret, Turret.TurretBuild, "small-turret-base", blankobj, {
	 load() {
        this.super$load();
        this.base = Core.atlas.find(this.name);
        this.partsAtlas = Core.atlas.find(this.name + "-parts");
		this.categorySprite = [Core.atlas.find(this.name + "-category1"),Core.atlas.find(this.name + "-category2"),Core.atlas.find(this.name + "-category3"),Core.atlas.find(this.name + "-category4"),Core.atlas.find(this.name + "-category5")];
        this.setConfigs();

    },
	
}, {
	aniprog: 0,
    anitime: 0,
    anispeed: 0,
	getPartsConfig() {
        return partinfo;
    },
    getPartsAtlas() {
        return smallTurret.partsAtlas;
    },
	getPartsCatagories() {
        return {
			base:smallTurret.categorySprite[0],
			breach:smallTurret.categorySprite[1],
			barrel:smallTurret.categorySprite[2],
			ammo:smallTurret.categorySprite[3],
			misc:smallTurret.categorySprite[4],
		};
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
	draw() {
        Draw.rect(smallTurret.base, this.x, this.y, 0);
		let turretSprite = this.getBufferRegion();
        if(turretSprite) {
            Draw.z(Layer.turret);
            if(this.getPaidRatio() < 1) {
				let ou = turretSprite.u;
				let ou2 = turretSprite.u2;
				let ov = turretSprite.v;
				let ov2 = turretSprite.v2;
                turretSprite.setU2(Mathf.map(this.aniprog, 0, 1, ou + 0.5*(ou2-ou), ou2));
				turretSprite.setU(Mathf.map(this.aniprog, 0, 1, ou+ 0.5*(ou2-ou), ou));
				turretSprite.setV2(Mathf.map(this.aniprog, 0, 1, ov+ 0.5*(ov2-ou), ov2));
				turretSprite.setV(Mathf.map(this.aniprog, 0, 1, ov+ 0.5*(ov2-ou), ov));
            }
            var that = this;
            if(this.getPaidRatio() < 1) {
                modturretlib.drawConstruct(turretSprite, this.aniprog, Pal.accent, 1.0, this.anitime * 0.5, Layer.turret, function(tex) {
                    Draw.rect(tex, that.x, that.y, that.rotation+90);
                });
            }
            else {
                Draw.rect(turretSprite, that.x, that.y, this.rotation+90);
            }
        }
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
smallTurret.getGraphConnectorBlock("torque graph").setAccept([1,1, 0,0, 0,0, 0,0]);
smallTurret.getGraphConnectorBlock("torque graph").setBaseFriction(0.03);
smallTurret.getGraphConnectorBlock("torque graph").setBaseInertia(5);
smallTurret.getGraphConnectorBlock("heat graph").setAccept([1,1, 1,1, 1,1, 1,1]);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.1);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.01);



