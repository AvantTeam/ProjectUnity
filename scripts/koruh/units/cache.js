const missileJavelin = new MissileBulletType(5, 21, "missile");
missileJavelin.width = 8;
missileJavelin.height = 8;
missileJavelin.shrinkY = 0;
missileJavelin.drag = -0.003;
missileJavelin.keepVelocity = false;
missileJavelin.splashDamageRadius = 20;
missileJavelin.splashDamage = 1;
missileJavelin.lifetime = 60;
missileJavelin.trailColor = Color.valueOf("b6c6fd");
missileJavelin.hitEffect = Fx.blastExplosion;
missileJavelin.despawnEffect = Fx.blastExplosion;
missileJavelin.backColor = Pal.bulletYellowBack;
missileJavelin.frontColor = Pal.bulletYellow;
missileJavelin.weaveScale = 8;
missileJavelin.weaveMag = 2;

const missilesWep = new Weapon("cache-missile");

missilesWep.y = 1.5;
missilesWep.reload = 90;
missilesWep.shots = 4;
missilesWep.alternate = true;
missilesWep.ejectEffect = Fx.none;
missilesWep.velocityRnd = 0.2;
missilesWep.spacing = 1;
missilesWep.bullet = missileJavelin;
missilesWep.shootSound = Sounds.missile;
missilesWep.flipSprite = true;

const cache = extendContent(UnitType, "cache", {
    mass(){
        return 2 * (this.hitSize * this.hitSize * Mathf.pi);
    },
    load(){
        this.super$load();
        this.shield = Core.atlas.find(this.name + "-shield");
    },
    draw(u){
        this.super$draw(u);
        var scl = this.scld(u);
        if(scl < 0.01) return;
        Draw.color(Pal.lancerLaser);
        Draw.alpha(scl / 2);
        Draw.blend(Blending.additive);
        Draw.rect(this.shield, u.x + Mathf.range(scl / 2), u.y + Mathf.range(scl / 2), u.rotation - 90);
        Draw.blend();
        Draw.reset();
    },
    scld(u){
        return Mathf.clamp((u.vel.len() - this.minV) / (this.maxV - this.minV));
    }
});
cache.constructor = () => extend(UnitEntity, {});
cache.minV = 3.6;
cache.maxV = 6;

cache.mineTier = -1;
cache.speed = 7;
cache.drag = 0.001;
cache.health = 560;
cache.flying = true;
cache.armor = 6;
cache.accel = 0.02;
cache.weapons.add(missilesWep);

const cacheAbil = extend(MoveLightningAbility, 10 * Vars.state.rules.unitDamageMultiplier, 14, 0.15, cache.minV, cache.maxV, Pal.lancerLaser, {
    update(unit){
        var scl = Mathf.clamp((unit.vel.len() - this.minSpeed) / (this.maxSpeed - this.minSpeed));
        this.super$update(unit);
        Effect.shake(Mathf.random(scl * 1.5), Mathf.random(scl * 1.5), unit);
    }
});
cache.abilities.add(cacheAbil);
