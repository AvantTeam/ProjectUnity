const amphibi = extendContent(UnitType, "amphibi-naval", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

var artillery = extend(ArtilleryBulletType, {});

artillery.hitEffect = Fx.blastExplosion;
artillery.knockback = 0.8;
artillery.speed = 2.1;
artillery.lifetime = 80;
artillery.width = artillery.height = 11;
artillery.collidesTiles = true;
artillery.ammoMultiplier = 4;
artillery.splashDamageRadius = 35;
artillery.splashDamage = 25;
artillery.backColor = Color.valueOf("d4816b");
artillery.frontColor = artillery.lightningColor = Color.valueOf("ffd37f");
artillery.smokeEffect = Fx.shootBigSmoke2;
artillery.shake = 4.5;
artillery.statusDuration = 60 * 10;

var artilleryWeapon = new Weapon("artillery");

artilleryWeapon.reload = 35;
artilleryWeapon.x = 3.5;
artilleryWeapon.y = -4;
artilleryWeapon.shots = 2;
artilleryWeapon.shotDelay = 3;
artilleryWeapon.inaccuracy = 5;
artilleryWeapon.rotate = true;
artilleryWeapon.shake = 3;
artilleryWeapon.rotateSpeed = 4;
artilleryWeapon.bullet = artillery;

var transformTime = 10

amphibi.constructor = () => {
	var time = transformTime
	const unit = extend(CommanderUnitWaterMove, {
		update() {
			this.super$update();
			if (!(unit.floorOn().isLiquid) || unit.floorOn() == Blocks.sandWater || unit.floorOn() == Blocks.darksandTaintedWater || unit.floorOn() == Blocks.darksandWater) {
				
				if (time < 0 || time > transformTime) {
					var GroundUnit = amphibiGround.create(unit.team);GroundUnit.set(unit.x,unit.y);GroundUnit.add();
					unit.kill()
				} else {
					time = time - 1
				}
			}
		}
	});

	return unit;
};

amphibi.weapons.add(artilleryWeapon);

const amphibiGround = extendContent(UnitType, "amphibi", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

amphibiGround.constructor = () => {
	var time = transformTime
	const unit = extend(BuilderLegsUnit, {
		update() {
			this.super$update();
			if (unit.floorOn().isLiquid && !(unit.floorOn() == Blocks.sandWater || unit.floorOn() == Blocks.darksandTaintedWater || unit.floorOn() == Blocks.darksandWater)) {
				if (time < 0 || time > transformTime) {
					var GroundUnit = amphibi.create(unit.team);GroundUnit.set(unit.x,unit.y);GroundUnit.add();
					unit.kill()
				} else {
					time = time - 1
				}
			}
		}
	});

	return unit;
};

amphibiGround.weapons.add(artilleryWeapon);
