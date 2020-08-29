const storm = extendContent(UnitType, "storm", {
	load(){

		this.super$load();
		this.region = Core.atlas.find(this.name);

	}
});

// Bullets

var missile = extend(MissileBulletType, {})

missile.speed = 3.5;
missile.damage = 45;
missile.splashDamageRadius = 35;
missile.splashDamage = 30;
missile.despawnEffect = Fx.blastExplosion;
missile.width = 7;
missile.height = 7;

var artillery = extend(ArtilleryBulletType, {});

artillery.hitEffect = Fx.blastExplosion;
artillery.knockback = 1.5;
artillery.speed = 2.9;
artillery.lifetime = 120;
artillery.width = artillery.height = 23;
artillery.collidesTiles = true;
artillery.ammoMultiplier = 3;
artillery.splashDamageRadius = 135;
artillery.splashDamage = 75;
artillery.backColor =  Color.valueOf("d4816b");
artillery.frontColor = artillery.lightningColor =  Color.valueOf("ffd37f");
artillery.smokeEffect = Fx.shootBigSmoke2;
artillery.shake = 4.5;
artillery.statusDuration = 60 * 10;

var laser = extend(LaserBulletType, {});
laser.damage = 125;
laser.sideAngle = 25;
laser.sideWidth = 2;
laser.sideLength = 25;
laser.width = 25;
laser.length = 220;
laser.shootEffect = Fx.shockwave;
laser.colors = [Color.valueOf("d4816b"), Color.valueOf("ffd37f"), Color.white];

// Weapons

var igniter = new Weapon("storm-igniter");

igniter.shootSound = Sounds.laser;
igniter.occlusion = 20;
igniter.reload = 170;
igniter.x = 0;
igniter.y = -2;
igniter.rotate = true;
igniter.shake = 5;
igniter.rotateSpeed = 1;
igniter.mirror = false;
igniter.bullet = laser;


var mainCannons = new Weapon("storm-main");

mainCannons.reload = 120;
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

var missiles2 = new Weapon("missiles-mount");

missiles2.reload = 35;
missiles2.x = 12;
missiles2.y = -8;
missiles2.rotate = true;
missiles2.shake = 2;
missiles2.rotateSpeed = 4;
missiles2.bullet = missile;


storm.constructor = () => {
	const unit = extend(CommanderUnitWaterMove, {
		update(unit) {
			this.super$update();
		}
	})

	return unit;
}

storm.weapons.add(igniter)
storm.weapons.add(mainCannons);
storm.weapons.add(missiles);
storm.weapons.add(missiles2);
