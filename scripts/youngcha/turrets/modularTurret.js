
const rotL = require("libraries/rotpowerlib");
const modturretlib = require("libraries/turretmodui");
const graphLib = require("libraries/graphlib");
const heatlib = require("libraries/heatlib");


function getPart(name){
	for(let i = 0;i<partinfo.length;i++){
		if(partinfo[i].name==name){
			return partinfo[i];
		}
	}
	return null;
}

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
			shaftSpd: {
                name: "stat.unity.shaftSpd",
                value: 40,
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
                name: "graphite",
                amount: 12
            },
			{
                name: "unity-nickel",
                amount: 8
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
                value: 2,
			},
			magazine:{
				name: "stat.unity.magazine",
                value: 1,
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
        name: "Ammo Packer",
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
					if(!config.ammoType["homing"]){
						config.ammoType["homing"] = 1;
					}else{
						config.ammoType["homing"] += 1;
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
                value: 200,
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

modturretlib.TurretBaseUpdater.attachBaseUpdater(partinfo,"Gun base",{
	onShoot(){
		this.guns[this.currentBarrel].recoilTime = Time.time;
	},
	draw(x,y){
		let trg = new Vec2(0,0);
		trg.trns(this.build.rotation,1.0);
		Draw.z(Layer.turret-0.01);
		Draw.rect(getPart(this.basepart).shadowSprite,x+trg.x,y+trg.y,this.build.rotation-90);
		let barrel = this.guns[this.currentBarrel].partList;
		for(let i = 0;i<barrel.length;i++){
			if(!this.guns[this.currentBarrel].recoilTime){
				this.guns[this.currentBarrel].recoilTime=0;
			}
			let offset = ((i+1)*4-(2.0/(1+Time.time-this.guns[this.currentBarrel].recoilTime)));
			Draw.z(Layer.turret-0.01);
			Draw.rect(barrel[i].shadowSprite, x + offset*trg.x, y + offset*trg.y,this.build.rotation-90);
			Draw.z(Layer.turret);
			Draw.rect(barrel[i].baseSprite, x + offset*trg.x, y + offset*trg.y,this.build.rotation-90);
		}
		Draw.z(Layer.turret+0.01);
		Draw.rect(getPart(this.basepart).baseSprite,x+trg.x,y+trg.y,this.build.rotation-90);
	}
});
modturretlib.TurretBaseUpdater.attachBaseUpdater(partinfo,"Rotary Gun base",{
	reloadMultiplier(){
		return Mathf.clamp(this.build.getGraphConnector("torque graph").getNetwork().lastVelocity/ getPart(this.basepart).stats.shaftSpd.value);
	},
	onShoot(){
		this.guns[this.currentBarrel].recoilTime = Time.time;
	},
	draw(x,y){
		let trg = new Vec2(0,0);
		trg.trns(this.build.rotation,1.0);
		let trg2 = new Vec2(0,0);
		trg2.trns(this.build.rotation+90,1.0);
		Draw.z(Layer.turret-0.01);
		Draw.rect(getPart(this.basepart).shadowSprite,x+trg.x,y+trg.y,this.build.rotation-90);
		let gunrot = this.build.getGraphConnector("torque graph").getRotation();
		for(var cbarrel = 0;cbarrel<this.guns.length;cbarrel++){
			var barrel = this.guns[cbarrel].partList;
			var rx = Mathf.sinDeg(gunrot + 360.0*cbarrel/this.guns.length)*1.5;
			var rz = Mathf.cosDeg(gunrot + 360.0*cbarrel/this.guns.length)*0.01;
			for(var i = 0;i<barrel.length;i++){
				if(!this.guns[cbarrel].recoilTime){
					this.guns[cbarrel].recoilTime=0;
				}
				var offset = ((i+1)*4-(2.0/(1+Time.time-this.guns[cbarrel].recoilTime)));
				var dx = x + offset*trg.x + rx*trg2.x;
				var dy = y + offset*trg.y + rx*trg2.y;
				Draw.z(Layer.turret-0.01);
				Draw.rect(barrel[i].shadowSprite, dx, dy, this.build.rotation-90);
				Draw.z(Layer.turret+0.01+rz);
				Draw.rect(barrel[i].baseSprite, dx, dy, this.build.rotation-90);
			}
		}
		
		
		
		Draw.z(Layer.turret+0.03);
		Draw.rect(getPart(this.basepart).baseSprite,x+trg.x,y+trg.y,this.build.rotation-90);
	}
});

let blankobj = graphLib.init();
graphLib.addGraph(blankobj, rotL.baseTypes.torqueConnector);
graphLib.addGraph(blankobj, heatlib.baseTypesHeat.heatConnector);
Object.assign(blankobj.build, modturretlib.dcopy2(modturretlib.TurretModularBuild));
Object.assign(blankobj.block, modturretlib.dcopy2(modturretlib.TurretModularBlock));


const smallTurret = graphLib.finaliseExtendContent(Turret, Turret.TurretBuild, "small-turret-base", blankobj, {
	 load() {
        this.super$load();
        this.base = Core.atlas.find(this.name);
        this.partsAtlas = Core.atlas.find(this.name + "-parts");
		this.categorySprite = [Core.atlas.find(this.name + "-category1"),Core.atlas.find(this.name + "-category2"),Core.atlas.find(this.name + "-category3"),Core.atlas.find(this.name + "-category4"),Core.atlas.find(this.name + "-category5")];
        this.setConfigs();
		this.baseSprite = Core.atlas.find(this.name + "-root");
		this.baseOutline = Core.atlas.find(this.name + "-root-outline");
		
		getPart("Gun base").shadowSprite =Core.atlas.find(this.name + "-gbase-outline");
		getPart("Gun base").baseSprite = Core.atlas.find(this.name + "-gbase");
		getPart("Rotary Gun base").shadowSprite =Core.atlas.find(this.name + "-rgbase-outline");
		getPart("Rotary Gun base").baseSprite = Core.atlas.find(this.name + "-rgbase");
		getPart("Cannon breach").shadowSprite =Core.atlas.find(this.name + "-cbreach-outline");
		getPart("Cannon breach").baseSprite = Core.atlas.find(this.name + "-cbreach");
		getPart("Gun breach").shadowSprite =Core.atlas.find(this.name + "-gbreach-outline");
		getPart("Gun breach").baseSprite = Core.atlas.find(this.name + "-gbreach");
		getPart("Grenade breach").shadowSprite =Core.atlas.find(this.name + "-grbreach-outline");
		getPart("Grenade breach").baseSprite = Core.atlas.find(this.name + "-grbreach");
		getPart("Incendiary Modifier").shadowSprite =Core.atlas.find(this.name + "-firemod-outline");
		getPart("Incendiary Modifier").baseSprite = Core.atlas.find(this.name + "-firemod");
		getPart("Homing Modifier").shadowSprite =Core.atlas.find(this.name + "-homingmod-outline");
		getPart("Homing Modifier").baseSprite = Core.atlas.find(this.name + "-homingmod");
		getPart("Ammo Packer").shadowSprite =Core.atlas.find(this.name + "-apacker-outline");
		getPart("Ammo Packer").baseSprite = Core.atlas.find(this.name + "-apacker");
		getPart("Rangefinder").shadowSprite =Core.atlas.find(this.name + "-rfinder-outline");
		getPart("Rangefinder").baseSprite = Core.atlas.find(this.name + "-rfinder");
		getPart("Radiator").shadowSprite =Core.atlas.find(this.name + "-rad-outline");
		getPart("Radiator").baseSprite = Core.atlas.find(this.name + "-rad");
		getPart("Armour Plate").shadowSprite =Core.atlas.find(this.name + "-aplate-outline");
		getPart("Armour Plate").baseSprite = Core.atlas.find(this.name + "-aplate");
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
smallTurret.getGraphConnectorBlock("torque graph").setBaseInertia(5);
smallTurret.getGraphConnectorBlock("heat graph").setAccept([1,1, 1,1, 1,1, 1,1]);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatConductivity(0.1);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatCapacity(50);
smallTurret.getGraphConnectorBlock("heat graph").setBaseHeatRadiativity(0.01);



