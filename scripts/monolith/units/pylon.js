const pylonLaserSmall = extendContent(LaserBulletType, 48, {});
pylonLaserSmall.length = 180;
pylonLaserSmall.width = 24;

const pylonLaserSmallWeapon = extendContent(Weapon, "unity-monolith-large2-weapon-mount", {});
pylonLaserSmallWeapon.rotate = true;
pylonLaserSmallWeapon.rotateSpeed = 3.5;
pylonLaserSmallWeapon.shootSound = Sounds.laser;
pylonLaserSmallWeapon.shake = 5;
pylonLaserSmallWeapon.shootY = 14;
pylonLaserSmallWeapon.x = 14;
pylonLaserSmallWeapon.y = 5;
pylonLaserSmallWeapon.reload = 60;
pylonLaserSmallWeapon.recoil = 4;
pylonLaserSmallWeapon.bullet = pylonLaserSmall;

const pylonLaserCharge = new Effect(80, 150, e => {
    let cwidth = pylonLaser.width;

    for(let i = 0; i < pylonLaser.colors.length; i++){
        cwidth *= pylonLaser.lengthFalloff;

        let color = pylonLaser.colors[i];
        Draw.color(color);
        Fill.circle(e.x, e.y, cwidth * e.fin());

        for(let j = 0; j < 2; j++){
            Lines.stroke(e.fin() * 1.5 * i);
            Lines.square(e.x, e.y, e.fout() * pylonLaser.width * i, Time.time() * 4 * Mathf.signs[j]);
        }
    }
});

const pylonLightning = extend(LightningBulletType, {});
pylonLightning.lightningLength = 32;
pylonLightning.lightningLengthRand = 12;
pylonLightning.damage = 28;

const pylonLaser = extendContent(LaserBulletType, 280, {
    init(b){
        if(typeof(b) !== "undefined"){
            this.super$init(b);

            for(let i = 0; i < 24; i++){
                Time.run(2 * i, () => {
                    pylonLightning.create(b, b.x, b.y, b.vel.angle());

                    Sounds.spark.at(b.x, b.y, Mathf.random(0.6, 0.9));
                });
            };
        };
    }
});
pylonLaser.length = 520;
pylonLaser.width = 60;
pylonLaser.lifetime = 72;
pylonLaser.largeHit = true;
pylonLaser.sideLength = pylonLaser.sideWidth = 0;
pylonLaser.shootEffect = pylonLaserCharge;

const pylonLaserWeapon = extendContent(Weapon, "unity-pylon-laser", {});
pylonLaserWeapon.shootSound = Sounds.laserblast;
pylonLaserWeapon.chargeSound = Sounds.lasercharge;
pylonLaserWeapon.soundPitchMin = 1;
pylonLaserWeapon.top = false;
pylonLaserWeapon.mirror = false;
pylonLaserWeapon.shake = 15;
pylonLaserWeapon.shootY = 11;
pylonLaserWeapon.x = pylonLaserWeapon.y = 0;
pylonLaserWeapon.reload = 420;
pylonLaserWeapon.recoil = 0;
pylonLaserWeapon.cooldownTime = 280;
pylonLaserWeapon.bullet = pylonLaser;
pylonLaserWeapon.shootStatusDuration = 60 * 1.8;
pylonLaserWeapon.shootStatus = StatusEffects.unmoving;
pylonLaserWeapon.firstShotDelay = pylonLaserCharge.lifetime;

const pylon = extendContent(UnitType, "pylon", {});
pylon.ammoType = AmmoTypes.powerHigh;
pylon.groundLayer = Layer.legUnit;
pylon.weapons.add(pylonLaserWeapon, pylonLaserSmallWeapon);
pylon.constructor = () => {
    return extend(LegsUnit, {});
};
