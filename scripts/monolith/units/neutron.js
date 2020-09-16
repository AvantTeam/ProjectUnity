const neutronLightning = extend(LightningBulletType, {});
neutronLightning.damage = 10;

const neutronBullet = extend(BasicBulletType, {
	init(b){
		if(typeof(b) !== "undefined"){
			if(b == null) return;
			for(var i = 0; i < 3; i++){
				neutronLightning.create(b, b.x, b.y, b.vel.angle());
				Sounds.spark.at(b.x, b.y, Mathf.random(0.6, 0.8));
			};
		};
	}
});
neutronBullet.sprite = "shell";
neutronBullet.width = 20;
neutronBullet.height = 20;
neutronBullet.speed = 3;
neutronBullet.lifetime = 60;
neutronBullet.damage = 12;
neutronBullet.frontColor = Pal.lancerLaser;
neutronBullet.backColor = Pal.lancerLaser.cpy().mul(0.6);

const neutronWeap = extendContent(Weapon, "unity-neutron-gun", {});
neutronWeap.x = 10.75;
neutronWeap.y = 2.25;
neutronWeap.reload = 60;
neutronWeap.recoil = 3.2;
neutronWeap.shootEffect = Fx.lightningShoot;
neutronWeap.shootSound = Sounds.shootBig;
neutronWeap.bullet = neutronBullet;

const neutron = extendContent(UnitType, "neutron", {});
neutron.weapons.add(neutronWeap);
neutron.constructor = () => extend(MechUnit, {});
