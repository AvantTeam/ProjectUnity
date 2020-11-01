const pedestalLightning = extend(LightningBulletType, {});
pedestalLightning.damage = 10;

const pedestalBullet = extend(BasicBulletType, {
    init(b){
        if(typeof(b) !== "undefined"){
			if(b == null) return;

            for(var i = 0; i < 3; i++){
                pedestalLightning.create(b, b.x, b.y, b.vel.angle());
                Sounds.spark.at(b.x, b.y, Mathf.random(0.6, 0.8));
            };
        };
    }
});
pedestalBullet.sprite = "shell";
pedestalBullet.width = 20;
pedestalBullet.height = 20;
pedestalBullet.speed = 3;
pedestalBullet.lifetime = 60;
pedestalBullet.damage = 12;
pedestalBullet.shootEffect = Fx.lightningShoot;
pedestalBullet.frontColor = Pal.lancerLaser;
pedestalBullet.backColor = Pal.lancerLaser.cpy().mul(0.6);

const pedestalWeap = extendContent(Weapon, "unity-pedestal-gun", {});
pedestalWeap.x = 10.75;
pedestalWeap.y = 2.25;
pedestalWeap.reload = 60;
pedestalWeap.recoil = 3.2;
pedestalWeap.shootSound = Sounds.shootBig;
pedestalWeap.bullet = pedestalBullet;

const pedestal = extendContent(UnitType, "pedestal", {});
pedestal.weapons.add(pedestalWeap);
pedestal.constructor = () => extend(MechUnit, {});
