const lib = this.global.unity.exp;

const shootSmallBlaze = new Effect(32, e => {
    Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());

    Angles.randLenVectors(e.id, 16, e.finpow() * 60, e.rotation, 18, new Floatc2({get: function(x, y){
        Fill.circle(e.x + x, e.y + y, 0.85 + e.fout() * 3.5);
    }}));
});

const shootPyraBlaze = new Effect(32, e => {
    Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Color.gray, e.fin());

    Angles.randLenVectors(e.id, 16, e.finpow() * 60, e.rotation, 18, new Floatc2({get: function(x, y){
        Fill.circle(e.x + x, e.y + y, 0.85 + e.fout() * 3.5);
    }}));
});

const coalBlaze = extend(BulletType, {
    hit(b, x, y){
        this.super$hit(b, x, y);
        b.owner.incExp(0.5);
    }
});
coalBlaze.ammoMultiplier = 3;
coalBlaze.hitSize = 7;
coalBlaze.lifetime = 24;
coalBlaze.damage = 32;
coalBlaze.speed = 3.35;
coalBlaze.pierce = true;
coalBlaze.statusDuration = 60 * 4;
coalBlaze.shootEffect = shootSmallBlaze;
coalBlaze.hitEffect = Fx.hitFlameSmall;
coalBlaze.despawnEffect = Fx.none;
coalBlaze.status = StatusEffects.burning;
coalBlaze.keepVelocity = false;
coalBlaze.hittable = false;

const pyraBlaze = extend(BulletType, {
    hit(b, x, y){
        this.super$hit(b, x, y);
        b.owner.incExp(0.5);
    }
});
pyraBlaze.ammoMultiplier = 3;
pyraBlaze.hitSize = 7;
pyraBlaze.lifetime = 24;
pyraBlaze.damage = 46;
pyraBlaze.speed = 3.35;
pyraBlaze.pierce = true;
pyraBlaze.statusDuration = 60 * 4;
pyraBlaze.shootEffect = shootPyraBlaze;
pyraBlaze.hitEffect = Fx.hitFlameSmall;
pyraBlaze.despawnEffect = Fx.none;
pyraBlaze.status = StatusEffects.burning;
pyraBlaze.keepVelocity = false;
pyraBlaze.hittable = false;

const inferno = lib.extend(ItemTurret, ItemTurret.ItemTurretBuild, "inferno", {
    maxLevel: 10,
    expFields: [{
        type: "exp",
        field: "useless",
        start: 0,
        intensity: 2
    }]
}, {});

inferno.ammo(Items.coal, coalBlaze, Items.pyratite, pyraBlaze);
