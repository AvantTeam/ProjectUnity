const railgunBullet = extend(PointBulletType, {});
railgunBullet.damage = 1000;
railgunBullet.tileDamageMultiplier = 0.7;
railgunBullet.speed = 500;
railgunBullet.hitShake = 6;
railgunBullet.trailSpacing = 35;
railgunBullet.frontColor = Color.white;
railgunBullet.backColor = Pal.lancerLaser;
railgunBullet.shootEffect = new Effect(60, 100, e => {
    Draw.color(Pal.lancerLaser);
    Draw.alpha(e.fout() * 0.6);

    Fill.circle(e.finpow() * 100);
});
railgunBullet.hitEffect = Fx.hitLancer;
railgunBullet.smokeEffect = Fx.blastExplosion;
railgunBullet.trailEffect = new Effect(48, e => {
    let len = railgunBullet.trailSpacing - 8;
    Tmp.v1.trns(e.rotation, len);

    for(let i = 0; i < 2; i++){
        Draw.color(i < 1 ? railgunBullet.backColor : railgunBullet.frontColor);
        let scl = i < 1 ? 1 : 0.5;

        Lines.stroke(e.fout() * 4 * scl);
        Lines.lineAngle(e.x, e.y, e.rotation, len);
        Drawf.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Lines.getStroke(), 8 * scl, e.rotation);
        Drawf.tri(e.x, e.y, Lines.getStroke(), 8 * scl, -e.rotation);
    }
});
railgunBullet.despawnEffect = Fx.cloudsmoke;

const railgun = extendContent(Weapon, "unity-monolith-railgun-big", {});
railgun.x = 0;
railgun.y = 12;
railgun.rotate = true;
railgun.rotateSpeed = 2;
railgun.mirror = false;
railgun.shootY = 35;
railgun.reload = 240;
railgun.recoil = 8;
railgun.shake = 8;
railgun.occlusion = 30;
railgun.shootSound = global.sounds.railgunbig;
railgun.bullet = railgunBullet;

const monument = extendContent(UnitType, "monument", {});
monument.constructor = () => {
    return extend(LegsUnit, {});
};
