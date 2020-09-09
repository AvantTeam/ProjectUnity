const wormLib = this.global.unity.wormlib;

const aLightningBullet = extend(LightningBulletType, {});
aLightningBullet.damage = 31;
aLightningBullet.lightningColor = Pal.surge;
aLightningBullet.lightningLength = 24;
aLightningBullet.lightningLengthRand = 3;

const aWLightning = new Weapon();
aWLightning.x = 0;
aWLightning.reload = 120;
aWLightning.rotateSpeed = 50;
aWLightning.mirror = false;
aWLightning.ignoreRotation = true;
aWLightning.minShootVelocity = 0.12;
aWLightning.bullet = aLightningBullet;

const arcnelidiaType = extendContent(UnitType, "arcnelidia", {
	load(){
		this.super$load();
		
		this.segmentRegion = Core.atlas.find(this.name + "-segment");
		this.tailRegion = Core.atlas.find(this.name + "-tail");
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
	}
});
arcnelidiaType.segmentOffset = 23;
arcnelidiaType.weapons.add(aWLightning);
arcnelidiaType.hitsize = 10;
arcnelidiaType.health = 800;
arcnelidiaType.speed = 4;
arcnelidiaType.accel = 0.03;
arcnelidiaType.drag = 0.007;
arcnelidiaType.rotateSpeed = 3.2;
arcnelidiaType.engineSize = -1;
arcnelidiaType.armor = 5;
arcnelidiaType.flying = true;
arcnelidiaType.visualElevation = 0.8;
arcnelidiaType.range = 210;
wormLib.setUniversal(arcnelidiaType, UnitEntity, 9, {});