const storm = extendContent(UnitType, "storm", {
	load(){

		this.super$load();
		this.region = Core.atlas.find(this.name);

	}
});

// Bullets

var missile = extend(MissileBulletType, {})

missile.speed = 3.5;
missile.damage = 5;
missile.lifetime = 49;
missile.splashDamageRadius = 45;
missile.splashDamage = 30;
missile.weaveScale = 8;
missile.weaveMag = 1;
missile.despawnEffect = Fx.blastExplosion;
missile.width = 7;
missile.height = 7;
missile.backColor = Pal.bulletYellowBack;
missile.frontColor = Pal.bulletYellow;
missile.width = missile.height = 10;
missile.trailColor = Color.gray;

var artillery = extend(ArtilleryBulletType, {});

artillery.hitEffect = Fx.blastExplosion;
artillery.knockback = 1.5;
artillery.speed = 2.9;
artillery.lifetime = 129;
artillery.width = artillery.height = 23;
artillery.collidesTiles = true;
artillery.ammoMultiplier = 3;
artillery.splashDamageRadius = 135;
artillery.splashDamage = 75;
artillery.backColor = Color.valueOf("d4816b");
artillery.frontColor = artillery.lightningColor = Color.valueOf("ffd37f");
artillery.smokeEffect = Fx.shootBigSmoke2;
artillery.shake = 4.5;
artillery.statusDuration = 60 * 10;

var laser = extend(LaserBulletType, {});
laser.damage = 155;
laser.sideAngle = 25;
laser.sideWidth = 2;
laser.sideLength = 25;
laser.width = 25;
laser.length = 220;
laser.shootEffect = Fx.shockwave;
laser.colors = [Color.valueOf("d4816b"), Color.valueOf("ffd37f"), Color.white];

// Weapons

var igniter = new Weapon("unity-storm-igniter");

igniter.shootSound = Sounds.laser;
igniter.occlusion = 20;
igniter.shootY = 10;
igniter.reload = 170;
igniter.x = 0;
igniter.y = -2;
igniter.rotate = true;
igniter.shake = 5;
igniter.rotateSpeed = 1;
igniter.mirror = false;
igniter.bullet = laser;


var mainCannons = new Weapon("unity-storm-main");

mainCannons.reload = 120;
mainCannons.shootY = 7;
mainCannons.x = 17;
mainCannons.y = -5;
mainCannons.rotate = true;
mainCannons.shake = 3;
mainCannons.rotateSpeed = 1;
mainCannons.mirror = true;
mainCannons.bullet = artillery;

var missiles = new Weapon("missiles-mount");

missiles.reload = 45;
missiles.x = 15;
missiles.y = 12;
missiles.rotate = true;
missiles.shake = 2;
missiles.rotateSpeed = 4;
missiles.bullet = missile;
missiles.shots = 4;
missiles.shotDelay = 3;
missiles.inaccuracy = 5;

var missiles2 = new Weapon("missiles-mount");

missiles2.reload = 35;
missiles2.x = 12;
missiles2.y = -8;
missiles2.rotate = true;
missiles2.shake = 2;
missiles2.rotateSpeed = 4;
missiles2.bullet = missile;
missiles2.shots = 4;
missiles2.shotDelay = 3;
missiles2.inaccuracy = 5;


storm.constructor = () => {
	const unit = extend(UnitWaterMove, {
		/*update(unit) {
			this.super$update();
		}*/
	});
	return unit;
}

storm.weapons.add(igniter)
storm.weapons.add(mainCannons);
storm.weapons.add(missiles);
storm.weapons.add(missiles2);
