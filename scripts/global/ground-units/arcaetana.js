const arcaetanaArtilleryFrag = extend(ArtilleryBulletType, {});
arcaetanaArtilleryFrag.speed = 2.5;
arcaetanaArtilleryFrag.damage = 23;
arcaetanaArtilleryFrag.lifetime = 82;
arcaetanaArtilleryFrag.splashDamageRadius = 40;
arcaetanaArtilleryFrag.splashDamage = 20;
arcaetanaArtilleryFrag.width = arcaetanaArtilleryFrag.height = 20;
arcaetanaArtilleryFrag.hitShake = 4;
arcaetanaArtilleryFrag.status = StatusEffects.sapped;
arcaetanaArtilleryFrag.statusDuration = 60 * 10;
arcaetanaArtilleryFrag.smokeEffect = Fx.shootBigSmoke2;
arcaetanaArtilleryFrag.backColor = Pal.sapBulletBack;
arcaetanaArtilleryFrag.frontColor = arcaetanaArtilleryFrag.lightningColor = Pal.sapBullet;
arcaetanaArtilleryFrag.lightning = 3;
arcaetanaArtilleryFrag.lightningLength = 6;

const arcaetanaArtillery = extend(ArtilleryBulletType, {
	update(b){
		this.super$update(b);
		
		if(Mathf.chanceDelta(0.3)){
			Lightning.create(b, Color.valueOf("bf92f9"), 43, b.x, b.y, Mathf.range(56) + b.rotation(), 8);
		}
	}
});
arcaetanaArtillery.speed = 3.5;
arcaetanaArtillery.damage = 45;
arcaetanaArtillery.lifetime = 85;
arcaetanaArtillery.collides = true;
arcaetanaArtillery.collidesTiles = true;
arcaetanaArtillery.width = arcaetanaArtillery.height = 27;
arcaetanaArtillery.ammoMultiplier = 3;
arcaetanaArtillery.knockback = 0.9;
arcaetanaArtillery.splashDamageRadius = 90;
arcaetanaArtillery.splashDamage = 50;
arcaetanaArtillery.lightning = 6;
arcaetanaArtillery.lightningLength = 23;
arcaetanaArtillery.hitShake = 7;
arcaetanaArtillery.backColor = Pal.sapBulletBack;
arcaetanaArtillery.frontColor = arcaetanaArtillery.lightningColor = Pal.sapBullet;
arcaetanaArtillery.status = StatusEffects.sapped;
arcaetanaArtillery.statusDuration = 60 * 10;
arcaetanaArtillery.smokeEffect = Fx.shootBigSmoke2;
arcaetanaArtillery.fragLifeMin = 0.3;
arcaetanaArtillery.fragBullets = 13;
arcaetanaArtillery.fragBullet = arcaetanaArtilleryFrag;

const arcaetanaLaser = extendContent(LaserBulletType, 98, {
	hit(b, x, y){
		if(x == null || y == null) return;
		this.super$hit(b, x, y);
		
		if(Mathf.chance(0.3)){
			Lightning.create(b, Color.valueOf("bf92f9"), 12, x, y, Mathf.range(30) + b.rotation(), 7);
		}
	}
});
arcaetanaLaser.colors = [Color.valueOf("a96bfa80"), Color.valueOf("bf92f9"), Color.white];
arcaetanaLaser.length = 195;
arcaetanaLaser.ammoMultiplier = 6;
arcaetanaLaser.width = 19;
arcaetanaLaser.drawSize = (arcaetanaLaser.length * 2) + 20;

const arcaetanaDeathLaser = extendContent(LaserBulletType, 325, {
	hit(b, x, y){
		if(x == null || y == null) return;
		this.super$hit(b, x, y);
		
		if(Mathf.chance(0.4)){
			Lightning.create(b, Color.valueOf("bf92f9"), 34, x, y, Mathf.range(30) + b.rotation(), 12);
		}
	}
});
arcaetanaDeathLaser.colors = [Color.valueOf("a96bfa80"), Color.valueOf("bf92f9"), Color.white];
arcaetanaDeathLaser.length = 290;
arcaetanaDeathLaser.ammoMultiplier = 4;
arcaetanaDeathLaser.width = 43;
arcaetanaDeathLaser.sideLength = 45;
arcaetanaDeathLaser.drawSize = (arcaetanaDeathLaser.length * 2) + 20;

const arcaetanaArtilleryCannon = new Weapon("unity-arcaetana-cannon");
arcaetanaArtilleryCannon.x = 32.5;
arcaetanaArtilleryCannon.y = -1.75;
arcaetanaArtilleryCannon.shootX = -7.5;
arcaetanaArtilleryCannon.shootY = 30.25;
arcaetanaArtilleryCannon.inaccuracy = 7.3;
arcaetanaArtilleryCannon.velocityRnd = 0.1;
arcaetanaArtilleryCannon.shots = 4;
arcaetanaArtilleryCannon.shotDelay = 7;
arcaetanaArtilleryCannon.shootSound = Sounds.artillery;
arcaetanaArtilleryCannon.rotate = false;
arcaetanaArtilleryCannon.reload = 130;
arcaetanaArtilleryCannon.shake = 6;
arcaetanaArtilleryCannon.recoil = 5;
arcaetanaArtilleryCannon.bullet = arcaetanaArtillery;

const arcaetanaSmallLaser = new Weapon("unity-gummy-main-sapper");
arcaetanaSmallLaser.x = 10.25;
arcaetanaSmallLaser.y = -23.25;
arcaetanaSmallLaser.shootY = 8;
arcaetanaSmallLaser.shootSound = Sounds.laser;
arcaetanaSmallLaser.reload = 30;
arcaetanaSmallLaser.rotate = true;
arcaetanaSmallLaser.bullet = arcaetanaLaser;

const arcaetanaSmallLaserB = arcaetanaSmallLaser.copy();
arcaetanaSmallLaserB.x = -17;
arcaetanaSmallLaserB.y = -18.5;
arcaetanaSmallLaserB.flipSprite = true;

const arcaetanaMainLaser = new Weapon("unity-arcaetana-main-laser");
arcaetanaMainLaser.x = 18.75;
arcaetanaMainLaser.y = -11;
arcaetanaMainLaser.shootY = 19;
arcaetanaMainLaser.rotateSpeed = 1;
arcaetanaMainLaser.shootSound = Sounds.laser;
arcaetanaMainLaser.reload = 60;
arcaetanaMainLaser.recoil = 4;
arcaetanaMainLaser.rotate = true;
arcaetanaMainLaser.occlusion = 15;
arcaetanaMainLaser.shake = 4;
arcaetanaMainLaser.bullet = arcaetanaDeathLaser;

const arcaetana = extendContent(UnitType, "arcaetana", {});
arcaetana.constructor = () => extend(BuilderLegsUnit, {});
arcaetana.weapons.add(arcaetanaArtilleryCannon);
arcaetana.weapons.add(arcaetanaSmallLaser);
arcaetana.weapons.add(arcaetanaSmallLaserB);
arcaetana.weapons.add(arcaetanaMainLaser);