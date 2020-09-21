const wormLib = this.global.unity.wormlib;

const aLightningBullet = extend(LightningBulletType, {});
aLightningBullet.damage = 23;
aLightningBullet.lightningColor = Pal.surge;
aLightningBullet.lightningLength = 24;
aLightningBullet.lightningLengthRand = 3;

const aWLightningB = new Weapon();
//aWLightningB.x = 0;
aWLightningB.reload = 90;
aWLightningB.rotateSpeed = 50;
aWLightningB.mirror = true;
aWLightningB.rotate = true;
aWLightningB.ignoreRotation = true;
aWLightningB.minShootVelocity = 2.1;
aWLightningB.bullet = aLightningBullet;

const aWLightning = new Weapon();
aWLightning.x = 0;
aWLightning.shots = 4;
aWLightning.reload = 70;
aWLightning.rotateSpeed = 50;
aWLightning.mirror = false;
aWLightning.ignoreRotation = true;
//aWLightning.minShootVelocity = 0.12;
aWLightning.bullet = aLightningBullet;

const arcnelidiaType = extendContent(UnitType, "arcnelidia", {
	load(){
		this.super$load();
		
		this.segmentRegion = Core.atlas.find(this.name + "-segment");
		this.tailRegion = Core.atlas.find(this.name + "-tail");
		
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
/*arcnelidiaType.constructor = () => {
	return extend(UnitEntity, {});
};*/
arcnelidiaType.segWeapSeq = new Seq();
arcnelidiaType.segWeapSeq.add(aWLightningB);
//wormLib.sortWeapons(arcnelidiaType.segWeapSeq);
arcnelidiaType.idType = 3;
arcnelidiaType.segmentOffset = 23;
arcnelidiaType.weapons.add(aWLightning);
arcnelidiaType.hitsize = 17;
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
//print(arcnelidiaType.constructor);
wormLib.setUniversal(arcnelidiaType, UnitEntity, false, {});
//print(arcnelidiaType.constructor);