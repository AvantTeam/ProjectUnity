const pylonLightning = extend(LightningBulletType, {});
pylonLightning.lightningLength = 32;
pylonLightning.lightningLengthRand = 12;
pylonLightning.damage = 28;

const mainLaser = extend(LaserBulletType, {
    init(b){
        if(typeof(b) !== "undefined"){
            this.super$init(b);

            for(let i = 0; i < 12; i++){
                Time.run(3 * i, () => {
                    pylonLightning.create(b, b.x, b.y, b.vel.angle());

                    Sounds.spark.at(b.x, b.y, Mathf.random(0.6, 0.9));
                });
            };
        };
    }
});
mainLaser.length = 520;
mainLaser.damage = 420;
mainLaser.width = 60;
mainLaser.lifetime = 72;
mainLaser.largeHit = true;
mainLaser.sideAngle = 45;

const mainLaserWeapon = extendContent(Weapon, "unity-pylon-laser", {});
mainLaserWeapon.shootSound = Sounds.laserblast;
mainLaserWeapon.chargeSound = Sounds.lasercharge;
mainLaserWeapon.soundPitchMin = 1;
mainLaserWeapon.top = false;
mainLaserWeapon.mirror = false;
mainLaserWeapon.shake = 15;
mainLaserWeapon.shootY = 5;
mainLaserWeapon.x = mainLaserWeapon.y = 0;
mainLaserWeapon.reload = 280;
mainLaserWeapon.recoil = 0;
mainLaserWeapon.cooldownTime = 280;
mainLaserWeapon.bullet = mainLaser;
mainLaserWeapon.firstShotDelay = Fx.greenLaserCharge.lifetime;

const pylon = extendContent(UnitType, "pylon", {});
pylon.ammoType = AmmoTypes.powerHigh;
pylon.weapons.add(mainLaserWeapon);
pylon.constructor = () => {
    return extend(LegsUnit, {});
};
