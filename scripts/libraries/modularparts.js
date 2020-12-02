const modturretlib = require("libraries/turretmodui");

const smallParts = [
	
	
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
			},
			heatAccumMult: {
                name: "stat.unity.heatAccumMult",
                value: 1.0,
            },
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
                value: 25,
            },
			heatAccumMult: {
                name: "stat.unity.heatAccumMult",
                value: 0.4,
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
			heat: {
                name: "stat.unity.heatPerShot",
                value: 350,
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
			heat: {
                name: "stat.unity.heatPerShot",
                value: 500,
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
                value: 3.5,
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
			heat: {
                name: "stat.unity.heatPerShot",
                value: 1500,
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
					let mul = config.shots/(config.shots-1);
					config.reloadmult *= mul;
					config.ammoCostMul*= mul;
					config.heatMult = config.heatMult?config.heatMult*mul:mul;
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


modturretlib.preCalcConnection(smallParts);

const basicBase = {
	onShoot(){
		this.guns[this.currentBarrel].recoilTime = Time.time;
	},
	draw(x,y){
		
		let trg = new Vec2(0,0);
		trg.trns(this.build.rotation,1.0);
		Draw.z(Layer.turret-0.01);
		Draw.rect(getPart(this.build.getPartsConfig(),this.basepart).shadowSprite,x+trg.x,y+trg.y,this.build.rotation-90);
		
		if(this.guns){
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
		}
		Draw.z(Layer.turret+0.01);
		Draw.rect(getPart(this.build.getPartsConfig(),this.basepart).baseSprite,x+trg.x,y+trg.y,this.build.rotation-90);
	}
}
const rotaryBase = {
	reloadMultiplier(){
		return Mathf.clamp(this.build.getGraphConnector("torque graph").getNetwork().lastVelocity/ getPart(this.build.getPartsConfig(),this.basepart).stats.shaftSpd.value);
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
		Draw.rect(getPart(this.build.getPartsConfig(),this.basepart).shadowSprite,x+trg.x,y+trg.y,this.build.rotation-90);
		
		if(this.guns){
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
		}
		
		Draw.z(Layer.turret+0.03);
		Draw.rect(getPart(this.build.getPartsConfig(),this.basepart).baseSprite,x+trg.x,y+trg.y,this.build.rotation-90);
	}
}



modturretlib.TurretBaseUpdater.attachBaseUpdater(smallParts,"Gun base",basicBase);
modturretlib.TurretBaseUpdater.attachBaseUpdater(smallParts,"Rotary Gun base",rotaryBase);



const partTierCategory={
	small: smallParts,
}


function getPart(partinfo,name){
	for(let i = 0;i<partinfo.length;i++){
		if(partinfo[i].name==name){
			return partinfo[i];
		}
	}
	return null;
}


function _getPartList(ids, px,py, connlevel, req){
	let partmash =[
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
				x: px,
				y: py,
			},
			isRoot: true,
			cost: [],
			connectOut: [6, connlevel, 6, 0],
			connectIn: [0, 0, 0, 0],
			stats: {
				hp: {
					name: "stat.unity.hpinc",
					value: 10,
				},
			}

		},
	];
	for(var i = 0;i<ids.length;i++){
		if(!partTierCategory[ids[i]]){
			continue;
		}
		let plist = partTierCategory[ids[i]];
		for(var z = 0;z<plist.length;z++){
			if(!req(plist[z])){
				continue;
			}
			partmash.push(plist[z]);
		}
	}
	
	return partmash;
}

const _partCategories ={
	base:null,
	breach:null,
	barrel:null,
	ammo:null,
	misc:null,
}

function _PartIcons(){
	return Core.atlas.find("unity-partsicons");
}


function loadAllSprites(){
	// :)
	_partCategories.base = Core.atlas.find("unity-partscategory1");
	_partCategories.breach = Core.atlas.find("unity-partscategory2");
	_partCategories.barrel = Core.atlas.find("unity-partscategory3");
	_partCategories.ammo = Core.atlas.find("unity-partscategory4");
	_partCategories.misc = Core.atlas.find("unity-partscategory5");
	getPart(smallParts,"Gun base").shadowSprite =Core.atlas.find("unity-part-gbase-outline");
	getPart(smallParts,"Gun base").baseSprite = Core.atlas.find("unity-part-gbase");
	getPart(smallParts,"Rotary Gun base").shadowSprite =Core.atlas.find("unity-part-rgbase-outline");
	getPart(smallParts,"Rotary Gun base").baseSprite = Core.atlas.find("unity-part-rgbase");
	getPart(smallParts,"Cannon breach").shadowSprite =Core.atlas.find("unity-part-cbreach-outline");
	getPart(smallParts,"Cannon breach").baseSprite = Core.atlas.find("unity-part-cbreach");
	getPart(smallParts,"Gun breach").shadowSprite =Core.atlas.find("unity-part-gbreach-outline");
	getPart(smallParts,"Gun breach").baseSprite = Core.atlas.find("unity-part-gbreach");
	getPart(smallParts,"Grenade breach").shadowSprite = Core.atlas.find("unity-part-grbreach-outline");
	getPart(smallParts,"Grenade breach").baseSprite = Core.atlas.find("unity-part-grbreach");
	getPart(smallParts,"Incendiary Modifier").shadowSprite =Core.atlas.find("unity-part-firemod-outline");
	getPart(smallParts,"Incendiary Modifier").baseSprite = Core.atlas.find("unity-part-firemod");
	getPart(smallParts,"Homing Modifier").shadowSprite =Core.atlas.find("unity-part-homingmod-outline");
	getPart(smallParts,"Homing Modifier").baseSprite = Core.atlas.find("unity-part-homingmod");
	getPart(smallParts,"Ammo Packer").shadowSprite =Core.atlas.find("unity-part-apacker-outline");
	getPart(smallParts,"Ammo Packer").baseSprite = Core.atlas.find("unity-part-apacker");
	getPart(smallParts,"Rangefinder").shadowSprite =Core.atlas.find("unity-part-rfinder-outline");
	getPart(smallParts,"Rangefinder").baseSprite = Core.atlas.find("unity-part-rfinder");
	getPart(smallParts,"Radiator").shadowSprite =Core.atlas.find("unity-part-rad-outline");
	getPart(smallParts,"Radiator").baseSprite = Core.atlas.find("unity-part-rad");
	getPart(smallParts,"Armour Plate").shadowSprite = Core.atlas.find("unity-part-aplate-outline");
	getPart(smallParts,"Armour Plate").baseSprite = Core.atlas.find("unity-part-aplate");
	print("sprite died");
	print(getPart(smallParts,"Armour Plate").baseSprite);
}

Events.on(EventType.ClientLoadEvent, cons(e => {
	if(!Vars.headless){
		loadAllSprites();
	}
}));


const _smallConnect = 1; //2x2
const _medConnect = 10;  //3x3
const _largeConnect = 20; //4x4
const _hugeConnect = 30;   //6x6
                           //8x8?

module.exports = {
	getPartInfo:getPart,
	getPartList:_getPartList,
	partIcons:_PartIcons,
	small:_smallConnect,
	medium:_medConnect,
	large:_largeConnect,
	huge:_hugeConnect,
	partCategories:_partCategories
}