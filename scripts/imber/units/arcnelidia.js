const wormLib = this.global.unity.wormlib;

const aLaserBullet = extend(LaserBulletType, {});
aLaserBullet.damage = 200;
aLaserBullet.colors = [Pal.surge.cpy().mul(1, 1, 1, 0.4), Pal.surge, Color.white];
aLaserBullet.hitEffect = Fx.hitLancer;
aLaserBullet.despawnEffect = Fx.none;
aLaserBullet.hitSize = 4;
aLaserBullet.lifetime = 16;
aLaserBullet.drawSize = 400;
aLaserBullet.collidesAir = false;
aLaserBullet.length = 190;

const aWLightning = new Weapon();
aWLightning.x = 3;
aWLightning.reload = 10;
aWLightning.rotateSpeed = 50;
aWLightning.shootSound = Sounds.laser;
aWLightning.mirror = true;
aWLightning.rotate = true;
aWLightning.minShootVelocity = 2.1;
aWLightning.bullet = aLaserBullet;

const aWLightningB = new Weapon();
aWLightningB.x = aWLightning.y = 0;
aWLightningB.reload = 60;
aWLightningB.rotateSpeed = 50;
aWLightningB.minShootVelocity = 0.01;
aWLightningB.bullet = UnitTypes.horizon.weapons.first().bullet;

const arcnelidiaType = extendContent(UnitType, "arcnelidia", {
	load(){
		this.super$load();
		
		this.segmentRegion = Core.atlas.find(this.name + "-segment");
		this.tailRegion = Core.atlas.find(this.name + "-tail");

		this.segmentOutline = Core.atlas.find(this.name + "-segment-outline");
		this.tailOutline = Core.atlas.find(this.name + "-tail-outline");
		
		this.segWeapSeq.each(w => {
			w.load();
		});
	},
	
	init(){
		this.super$init();
		
		wormLib.sortWeapons(this.segWeapSeq);
	},
	
	setTypeID(id){
		this.idType = id;
	},
	
	getSegmentWeapon(){
		return this.segWeapSeq;
	},
	getTypeID(){
		return this.idType;
	},
	
	segmentOffsetF(){
		return this.segmentOffset;
	},
	
	segmentRegionF(){
		return this.segmentRegion;
	},

	tailRegionF(){
		return this.tailRegion;
	},
	
	segmentRegionOutline(){
		return this.segmentOutline;
	},

	tailRegionOutline(){
		return this.tailOutline;
	},

	drawBody(unit){
		this.super$drawBody(unit);
		wormLib.drawSegments(unit);
	},
	
	drawShadow(unit){
		this.super$drawShadow(unit);
		wormLib.drawShadowSegments(unit);
	},
	
	drawOcclusion(unit){
		this.super$drawOcclusion(unit);
		wormLib.drawOcclusionSegments(unit);
	}
});

arcnelidiaType.segWeapSeq = new Seq();
arcnelidiaType.segWeapSeq.add(aWLightningB);
//wormLib.sortWeapons(arcnelidiaType.segWeapSeq);
arcnelidiaType.idType = 3;
arcnelidiaType.segmentOffset = 23;
arcnelidiaType.weapons.add(aWLightning);
arcnelidiaType.hitSize = 17;
arcnelidiaType.health = 800;
arcnelidiaType.speed = 4;
arcnelidiaType.accel = 0.035;
arcnelidiaType.drag = 0.007;
arcnelidiaType.rotateSpeed = 3.2;
arcnelidiaType.engineSize = -1;
arcnelidiaType.faceTarget = false;
arcnelidiaType.armor = 5;
arcnelidiaType.flying = true;
arcnelidiaType.visualElevation = 0.8;
arcnelidiaType.range = 210;
wormLib.setUniversal(arcnelidiaType, UnitEntity, false, {});
