const effects = this.global.unity.effects;

const orb = extend(BulletType, {
	draw(b){
        Drawf.light(b.x, b.y, 16, this.color, 0.6);

		Draw.color(this.color);
		Fill.circle(b.x, b.y, 4);

		Draw.color();
		Fill.circle(b.x, b.y, 2.5);
	},

	update(b){
		this.super$update(b);
		if(b.timer.get(1, 7)){
			Units.nearbyEnemies(b.team, b.x - this.scanRadius, b.y - this.scanRadius, this.scanRadius * 2, this.scanRadius * 2, cons(unit => {
				Lightning.create(b.team, Pal.surge, Mathf.random(17, 33), b.x, b.y, b.angleTo(unit), Mathf.random(7, 13));
			}));
		};
	},

    drawLight(b){

    }
});
orb.lifetime = 240;
orb.speed = 1.24;
orb.damage = 23;
orb.pierce = true;
orb.hittable = false;
orb.hitEffect = effects.imberOrbHit;
orb.color = Pal.surge;
orb.trailEffect = effects.imberOrbTrail;
orb.trailChance = 0.4;
orb.scanRadius = 5 * Vars.tilesize;

const orbTurret = extendContent(ChargeTurret, "orb", {});
orbTurret.shootType = orb;
orbTurret.shootSound = Sounds.laser;
orbTurret.heatColor = Pal.turretHeat;
orbTurret.shootEffect = effects.imberOrbShoot;
orbTurret.smokeEffect = Fx.none;
orbTurret.chargeEffect = effects.imberOrbCharge;
orbTurret.chargeBeginEffect = effects.imberOrbChargeBegin;
