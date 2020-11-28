const effects = this.global.unity.effects;

const plasmaFrag = Vars.content.getByName(ContentType.block, "unity-plasma").shootType.fragBullet;

const surgeBomb = new Effect(40, 100, e => {
	Draw.color(Pal.surge);
	Lines.stroke(e.fout() * 2);
	Lines.circle(e.x, e.y, 4 + e.finpow() * 65);

	Draw.color(Pal.surge);

	for(var i = 0; i < 4; i++){
		Drawf.tri(e.x, e.y, 6, 100 * e.fout(), i*90);
	}

	Draw.color();

	for(var i = 0; i < 4; i++){
		Drawf.tri(e.x, e.y, 3, 35 * e.fout(), i*90);
	}
});

const bullet = extend(BasicBulletType, {
	despawned(b){
		this.super$despawned(b);

		for(var i = 0; i < 10; i++){
			Lightning.create(b, Pal.surge, 680 / 5, b.x, b.y, Mathf.random(0, 360), 20);
		}
	}
});

bullet.sprite = Core.atlas.find("large-bomb");
bullet.width = bullet.height = 30;
bullet.maxRange = 30;
bullet.ignoreRotation = true;
bullet.backColor = Pal.surge;
bullet.frontColor = Color.white;
bullet.mixColorTo = Color.white;
bullet.hitSound = Sounds.plasmaboom;
bullet.shootCone = 180;
bullet.ejectEffect = Fx.none;
bullet.despawnShake = 4;
bullet.collidesAir = false;
bullet.lifetime = 70;
bullet.despawnEffect = surgeBomb;
bullet.hitEffect = Fx.massiveExplosion;
bullet.keepVelocity = false;
bullet.spin = 2;
bullet.shrinkX = bullet.shrinkY = 0.7;
bullet.speed = 7;
bullet.collides = false;
bullet.splashDamage = 680;
bullet.splashDamageRadius = 120;
bullet.fragBullet = plasmaFrag;
bullet.fragBullets = 8;
bullet.fragLifeMin = 0.8;
bullet.fragLifeMax = 1.1;
bullet.scaleVelocity = true;

const electrobombTurret = extendContent(ItemTurret, "electrobomb", {
	load(){
		this.super$load();
		this.baseRegion = Core.atlas.find("unity-block-5")
	},

	init(){
		this.super$init();

		this.ammo(
			Vars.content.getByName(ContentType.item, "unity-spark-alloy"), bullet
		);
	}
});

electrobombTurret.consumes.powerCond(10, boolf(b => b.isActive()))
electrobombTurret.shootSound = Sounds.laser;
electrobombTurret.shootEffect = Fx.none;
electrobombTurret.smokeEffect = Fx.none;