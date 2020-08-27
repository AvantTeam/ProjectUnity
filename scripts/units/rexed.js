const rexed = extendContent(UnitType, "rexed", {

	load() {

		this.super$load();
		this.region = Core.atlas.find(this.name);

	}
	
});

var missile = extend(MissileBulletType, {})

missile.speed = 3;
missile.damage = 30;
missile.despawnEffect = Fx.blastExplosion;
missile.width = 7;
missile.height = 7;

var mainCannon = new Weapon("main-cannon");

mainCannon.reload = 120;
mainCannon.x = 0;
mainCannon.mirror = false;
mainCannon.y = -6;
mainCannon.rotate = false;
mainCannon.shake = 3;
mainCannon.rotateSpeed = 1;
mainCannon.bullet =  Bullets.artilleryPlastic;

var missiles = new Weapon("missiles-mount");

missiles.reload = 35;
missiles.x = 3.5;
missiles.y = 5;
missiles.rotate = true;
missiles.shake = 3;
mainCannon.rotateSpeed = 4;
missiles.bullet =  missile

rexed.constructor = () => {

	const unit = extend(CommanderUnitWaterMove, {})

	return unit;

}

rexed.abilities.add(new ForceFieldAbility(65, 0.1, 300, 450));
rexed.weapons.add(mainCannon);
rexed.weapons.add(missiles);
