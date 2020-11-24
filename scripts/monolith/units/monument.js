const laserGun = extendContent(Weapon, "unity-monolith-large2-weapon-mount", {});
laserGun.x = 14;
laserGun.y = 12;
laserGun.shootY = 14;
laserGun.rotate = true;
laserGun.rotateSpeed = 3.5;
laserGun.reload = 48;
laserGun.recoil = 5;
laserGun.shake = 5;
laserGun.shootSound = Sounds.laser;
laserGun.bullet = extendContent(LaserBulletType, 140, {});;

const laserGun2 = laserGun.copy();
laserGun2.x = 20;
laserGun2.y = 3;
laserGun2.reload = 60;

const railgunBullet = extend(PointBulletType, {});
railgunBullet.damage = 1000;
railgunBullet.tileDamageMultiplier = 0.7;
railgunBullet.speed = railgunBullet.maxRange = 500;
railgunBullet.lifetime = 1;
railgunBullet.hitShake = 6;
railgunBullet.trailSpacing = 35;
railgunBullet.frontColor = Color.white;
railgunBullet.backColor = Pal.lancerLaser;
railgunBullet.shootEffect = new Effect(48, e => {
    Draw.color(Color.white, Pal.lancerLaser, Color.cyan, e.fin());

    Angles.randLenVectors(e.id, 12, e.finpow() * 64, e.rotation, 16, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 1 + e.fout() * 5);
    });
});
railgunBullet.despawnEffect = new Effect(32, e => {
    e.scaled(15, i => {
        Draw.color(Pal.lancerLaser);

        Lines.stroke(i.fout() * 5);
        Lines.circle(e.x, e.y, 4 + i.finpow() * 26);
    });

    Angles.randLenVectors(e.id, 25, 5 + e.fin() * 80, e.rotation, 60, (x, y) => {
        Fill.circle(e.x + x, e.y + y, e.fout() * 3);
    });
});
railgunBullet.smokeEffect = Fx.blastExplosion;
railgunBullet.trailEffect = new Effect(32, e => {
    let len = railgunBullet.trailSpacing - 12;
    let rot = e.rotation;
    Tmp.v1.trns(rot, len);

    for(let i = 0; i < 2; i++){
        Draw.color(i < 1 ? railgunBullet.backColor : railgunBullet.frontColor);
        let scl = i < 1 ? 1 : 0.5;

        Lines.stroke(e.fout() * 10 * scl);
        Lines.lineAngle(e.x, e.y, rot, len, false);
        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Lines.getStroke() * 1.22, 12 * scl, rot);
        Drawf.tri(e.x, e.y, Lines.getStroke() * 1.22, 12 * scl, rot + 180);
    }
});
railgunBullet.shootEffect = Fx.blastExplosion;

const railgun = extendContent(Weapon, "unity-monolith-railgun-big", {});
railgun.x = 0;
railgun.y = 12;
railgun.rotate = true;
railgun.rotateSpeed = 1.6;
railgun.mirror = false;
railgun.shootY = 35;
railgun.reload = 240;
railgun.cooldownTime = 210;
railgun.recoil = 8;
railgun.shake = 8;
railgun.occlusion = 30;
railgun.shootSound = Sounds.railgun;
railgun.bullet = railgunBullet;

const monument = extendContent(UnitType, "monument", {});
monument.ammoType = AmmoTypes.powerHigh;
monument.groundLayer = Layer.legUnit;
monument.weapons.add(railgun, laserGun, laserGun2);
monument.constructor = () => {
    return extend(LegsUnit, {});
};
