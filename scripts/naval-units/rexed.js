const rexed = extendContent(UnitType, "rexed", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

var missile = extend(MissileBulletType, {});

missile.speed = 3;
missile.lifetime = 49;
missile.damage = 3;
missile.splashDamageRadius = 35;
missile.splashDamage = 20;
missile.weaveScale = 8;
missile.weaveMag = 1;
missile.despawnEffect = Fx.blastExplosion;
missile.width = 7;
missile.height = 7;
missile.backColor = Pal.bulletYellowBack;
missile.frontColor = Pal.bulletYellow;
missile.width = missile.height = 9;
missile.trailColor = Color.gray;

var artillery = extend(ArtilleryBulletType, {});
artillery.hitEffect = Fx.blastExplosion;
artillery.knockback = 1.2;
artillery.speed = 2.9;
artillery.lifetime = 128;
artillery.width = artillery.height = 19;
artillery.collidesTiles = true;
artillery.ammoMultiplier = 3;
artillery.splashDamageRadius = 75;
artillery.splashDamage = 95;
artillery.backColor = Color.valueOf("d4816b");
artillery.frontColor = artillery.lightningColor = Color.valueOf("ffd37f");
artillery.smokeEffect = Fx.shootBigSmoke2;
artillery.shake = 4.5;
artillery.statusDuration = 60 * 10;

var mainCannon = new Weapon("unity-rexed-main");

mainCannon.reload = 120;
mainCannon.shootY = 7;
mainCannon.x = 0;
mainCannon.y = -6;
mainCannon.rotate = true;
mainCannon.shake = 3;
mainCannon.rotateSpeed = 1.6;
mainCannon.mirror = false;
mainCannon.inaccuracy = 3;
mainCannon.bullet = artillery;

var missiles = new Weapon("missiles-mount");

missiles.reload = 35;
missiles.x = 3.5;
missiles.y = 5;
missiles.shots = 3;
missiles.shotDelay = 3;
missiles.inaccuracy = 5;
missiles.rotate = true;
missiles.shake = 3;
missiles.rotateSpeed = 4;
missiles.bullet = missile;

rexed.constructor = () => {

	const unit = extend(CommanderUnitWaterMove, {})

	return unit;

};

rexed.abilities.add(new ForceFieldAbility(65, 0.1, 300, 450));
rexed.weapons.add(mainCannon);
rexed.weapons.add(missiles);

//print(rexed.weapons.lenght);