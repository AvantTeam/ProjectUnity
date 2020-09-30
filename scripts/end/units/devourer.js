const wormLib = this.global.unity.wormlib;

const tdDeathMissile = extend(MissileBulletType, {
	hitTile(b, tile, initialHealth){
		this.super$hitTile(b, tile, initialHealth);
		
		var healthValue = tile.maxHealth / (tile.block.size * tile.block.size);
		
		if(healthValue >= 1200){
			var dmg = (healthValue - 1200) * 700;
			tile.damage(dmg);
		}
	},

	hitEntity(b, other, initialHealth){
		this.super$hitEntity(b, other, initialHealth);
		
		var healthValue = other.maxHealth / (other.hitSize * 5);
		
		if(healthValue >= 105){
			var dmg = (healthValue - 105) * 700;
			other.damage(dmg);
		}
	}
});
tdDeathMissile.damage = 23;
tdDeathMissile.speed = 6;
tdDeathMissile.width = 9;
tdDeathMissile.height = 11;
tdDeathMissile.backColor = Color.valueOf("f53036");
tdDeathMissile.frontColor = Color.valueOf("ff786e");
tdDeathMissile.trailColor = Color.valueOf("f53036");
tdDeathMissile.splashDamageRadius = 30;
tdDeathMissile.splashDamage = 43;
tdDeathMissile.weaveScale = 1.6;
tdDeathMissile.weaveMag = 18;

const tdDeathBullet = extend(BasicBulletType, {
	hitTile(b, tile, initialHealth){
		this.super$hitTile(b, tile, initialHealth);
		
		var healthValue = tile.maxHealth / (tile.block.size * tile.block.size);
		
		if(healthValue >= 1200){
			var dmg = (healthValue - 1200) * 700;
			tile.damage(dmg);
		}
	},

	hitEntity(b, other, initialHealth){
		this.super$hitEntity(b, other, initialHealth);
		
		var healthValue = other.maxHealth / (other.hitSize * 5);
		
		if(healthValue >= 105){
			var dmg = (healthValue - 105) * 700;
			other.damage(dmg);
		}
	}
});
tdDeathBullet.damage = 130;
tdDeathBullet.speed = 9.2;
tdDeathBullet.hitSize = 8;
tdDeathBullet.width = 19;
tdDeathBullet.height = 25;
tdDeathBullet.shrinkY = 0;
tdDeathBullet.backColor = Color.valueOf("f53036");
tdDeathBullet.frontColor = Color.valueOf("ff786e");

const tdFlameBullet = extend(BasicBulletType, {});
tdFlameBullet.pierce = true;
tdFlameBullet.damage = 32;
tdFlameBullet.hittable = false;
tdFlameBullet.speed = 7;
tdFlameBullet.drag = 0.017;
tdFlameBullet.hitSize = 8;
tdFlameBullet.lifetime = 1.8 * 60;
tdFlameBullet.width = 10;
tdFlameBullet.height = 10;
tdFlameBullet.hitEffect = Fx.hitFlameSmall;
tdFlameBullet.despawnEffect = Fx.none;
tdFlameBullet.shootEffect = Fx.shootSmallFlame;
tdFlameBullet.smokeEffect = Fx.shootSmallFlame;
tdFlameBullet.status = StatusEffects.burning;

const tdSmallLaserBullet = extendContent(ContinuousLaserBulletType, 20, {});
tdSmallLaserBullet.lifetime = 2 * 60;
tdSmallLaserBullet.length = 190;
tdSmallLaserBullet.strokes = [2 * 0.4, 1.5 * 0.4, 1 * 0.4, 0.4 * 0.4];
tdSmallLaserBullet.colors = [Color.valueOf("f5303680"), Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white];

const tdLaserBullet = extendContent(ContinuousLaserBulletType, 80, {
	update(b){
		this.super$update(b);
		
		if(Mathf.chanceDelta(0.4)){
			Lightning.create(b.team, Color.valueOf("f53036"), 23, b.x, b.y, b.rotation(), Mathf.round((this.length / 8) + Mathf.random(2, 7)));
		};
		
		if(Mathf.chanceDelta(0.9)){
			var lLength = Mathf.random(4, 9);
			Tmp.v2.trns(b.rotation(), Mathf.random(0, this.length - (lLength * 8)));
			Lightning.create(b.team, Color.valueOf("f53036"), 23, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.rotation(), Mathf.round(lLength));
		};
		
		if(Mathf.chance(0.4)){
			Tmp.v2.trns(b.rotation(), Mathf.random(2.9, this.length));
			Damage.createIncend(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 7, 2);
		}
	}
});
tdLaserBullet.lifetime = 5 * 60;
tdLaserBullet.length = 320;
tdLaserBullet.colors = [Color.valueOf("f5303680"), Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white];

const tdDeathMain = new Weapon("unity-doeg-destroyer");
tdDeathMain.mirror = true;
tdDeathMain.ignoreRotation = true;
tdDeathMain.rotate = true;
tdDeathMain.x = 22;
tdDeathMain.y = -17.75;
tdDeathMain.shootY = 12;
tdDeathMain.occlusion = 14;
tdDeathMain.reload = 1.5 * 60;
tdDeathMain.bullet = tdDeathBullet;
tdDeathMain.inaccuracy = 1.4;
tdDeathMain.shots = 6;
tdDeathMain.shotDelay = 4;
tdDeathMain.shootSound = Sounds.shootBig;

const tdDeathMissileW = new Weapon("unity-doeg-launcher");
tdDeathMissileW.x = 19;
//tdDeathMissileW.y = 12;
tdDeathMissileW.y = 0;
tdDeathMissileW.shootY = 8;
tdDeathMissileW.rotate = true;
tdDeathMissileW.reload = 1.2 * 60;
tdDeathMissileW.shots = 8;
tdDeathMissileW.shotDelay = 3;
tdDeathMissileW.inaccuracy = 1.4;
tdDeathMissileW.occlusion = 14;
tdDeathMissileW.xRand = 12;
tdDeathMissileW.bullet = tdDeathMissile;
tdDeathMissileW.shootSound = Sounds.missile;

const tdLaserSmall = new Weapon("unity-doeg-small-laser");
tdLaserSmall.alternate = false;
tdLaserSmall.rotate = true;
tdLaserSmall.x = 19;
tdLaserSmall.y = 0;
tdLaserSmall.reload = 2 * 60;
tdLaserSmall.occlusion = 14;
tdLaserSmall.continuous = true;
tdLaserSmall.bullet = tdSmallLaserBullet;
tdLaserSmall.shootSound = Sounds.laser;

const tdflameMain = new Weapon("");
tdflameMain.mirror = false;
tdflameMain.ignoreRotation = true;
tdflameMain.x = 0;
tdflameMain.y = 15;
tdflameMain.xRand = 3;
tdflameMain.reload = 3;
tdflameMain.shots = 2;
tdflameMain.inaccuracy = 2.1;
tdflameMain.bullet = tdFlameBullet;
tdflameMain.velocityRnd = 0.2;
tdflameMain.shootSound = Sounds.flame;

const tdLaserMain = new Weapon("");
tdLaserMain.mirror = false;
tdLaserMain.ignoreRotation = true;
tdLaserMain.x = 0;
tdLaserMain.y = 18;
tdLaserMain.reload = 15 * 60;
tdLaserMain.continuous = true;
tdLaserMain.bullet = tdLaserBullet;
tdLaserMain.shake = 4;
tdLaserMain.shootSound = Sounds.laserbig;

const trueDevourer = extendContent(UnitType, "devourer-of-eldrich-gods", {
	load(){
		this.super$load();
		
		this.segmentRegion = Core.atlas.find(this.name + "-segment");
		this.segmentCellRegion = Core.atlas.find(this.name + "-segment-cell");
		this.tailRegion = Core.atlas.find(this.name + "-tail");
		
		this.segWeapSeq.each(w => {
			w.load();
		});
	},
	init(){
		this.super$init();
		
		wormLib.sortWeapons(this.segWeapSeq);
		
		Vars.content.getBy(ContentType.status).each(s => {
			if(s instanceof StatusEffect) this.immunities.add(s);
		});
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
	getSegmentCellR(){
		return this.segmentCellRegion;
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
trueDevourer.idType = 3;
trueDevourer.lowAltitude = true;
trueDevourer.segWeapSeq = new Seq();
//trueDevourer.segWeapSeq.add(tdLaserSmall);
trueDevourer.segWeapSeq.add(tdDeathMissileW);
trueDevourer.segWeapSeq.add(tdDeathMain);
trueDevourer.segmentOffset = (41 * 1.55) + 1;
trueDevourer.weapons.add(tdLaserMain);
trueDevourer.weapons.add(tdDeathMain);
trueDevourer.weapons.add(tdflameMain);
trueDevourer.hitSize = 41 * 1.55;
trueDevourer.health = 12500000;
trueDevourer.speed = 4;
trueDevourer.accel = 0.053;
trueDevourer.drag = 0.012;
trueDevourer.rotateSpeed = 3.2;
trueDevourer.engineSize = -1;
trueDevourer.faceTarget = false;
trueDevourer.armor = 8;
trueDevourer.flying = true;
trueDevourer.visualElevation = 1.9;
trueDevourer.range = 450;
wormLib.setUniversal(trueDevourer, UnitEntity, true, {
	getSegmentLength(){
		return 45;
	},
	
	isImmune(status){
		return true;
	},
	
	damage(amount, withEffect){
		var pre = this.hitTime;
		var hAmount = amount / 1.2;
		var trueAmount = hAmount <= 400 ? hAmount : (Math.log(hAmount - 399) * 2) + 400;
		this.super$damage(trueAmount);
		
		//just incase that its undefined
		if(withEffect == true){
			this.hitTime = pre;
		}
	},
});