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
railgunBullet.speed = 500;
railgunBullet.hitShake = 6;
railgunBullet.trailSpacing = 35;
railgunBullet.frontColor = Color.white;
railgunBullet.backColor = Pal.lancerLaser;
railgunBullet.shootEffect = new Effect(48, e => {
    for(let i = 0; i < 2; i++){
        Draw.color(i < 1 ? railgunBullet.backColor : railgunBullet.frontColor);
        Draw.blend(Blending.additive);
        Draw.alpha(e.fout() * 0.8);

        Fill.circle(e.x, e.y, e.finpow() * 30 * (i + 1));
        
        Draw.blend();
    }
});
railgunBullet.hitEffect = Fx.hitLancer;
railgunBullet.smokeEffect = Fx.blastExplosion;
railgunBullet.trailEffect = new Effect(48, e => {
    let len = railgunBullet.trailSpacing - 12;
    let rot = e.rotation;
    Tmp.v1.trns(rot, len);

    for(let i = 0; i < 2; i++){
        Draw.color(i < 1 ? railgunBullet.backColor : railgunBullet.frontColor);
        let scl = i < 1 ? 1 : 0.5;

        Lines.stroke(e.fout() * 15 * scl);
        Lines.lineAngle(e.x, e.y, rot, len, false);
        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Lines.getStroke() * 1.22, 12 * scl, rot);
        Drawf.tri(e.x, e.y, Lines.getStroke() * 1.22, 12 * scl, rot + 180);
    }
});
railgunBullet.despawnEffect = Fx.cloudsmoke;

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
railgun.shootSound = global.unity.sounds.railgunbig;
railgun.bullet = railgunBullet;

const monument = extendContent(UnitType, "monument", {});
monument.ammoType = AmmoTypes.powerHigh;
monument.groundLayer = Layer.legUnit;
monument.weapons.add(railgun, laserGun, laserGun2);
monument.constructor = () => {
    return extend(LegsUnit, {});
};
