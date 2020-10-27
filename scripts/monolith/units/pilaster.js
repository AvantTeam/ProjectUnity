const lightning = extend(LightningBulletType, {});
lightning.damage = 15;
lightning.lightningLength = 15;

const laser = extendContent(LaserBulletType, 100, {});

const weaponSmall = extendContent(Weapon, "unity-monolith-medium-weapon-mount", {});
weaponSmall.rotate = true;
weaponSmall.x = 4;
weaponSmall.y = 7.5;
weaponSmall.shootY = 6;
weaponSmall.recoil = 2.5;
weaponSmall.reload = 25;
weaponSmall.shots = 3;
weaponSmall.spacing = 3;
weaponSmall.shootSound = Sounds.spark;
weaponSmall.bullet = lightning;

const weaponBig = extendContent(Weapon, "unity-monolith-large-weapon-mount", {});
weaponBig.rotate = true;
weaponBig.rotateSpeed = 10;
weaponBig.x = 13;
weaponBig.y = 2;
weaponBig.shootY = 10.5
weaponBig.recoil = 3;
weaponBig.reload = 40;
weaponBig.shootSound = Sounds.laser;
weaponBig.bullet = laser;

const pilaster = extendContent(UnitType, "pilaster", {});
pilaster.weapons.add(weaponBig, weaponSmall);
pilaster.constructor = () => extend(MechUnit, {});
