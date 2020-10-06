const orbHit = new Effect(12, e => {
	Draw.color(Pal.surge);
	Lines.stroke(e.fout() * 1.5);
	Angles.randLenVectors(e.id, 8, e.finpow() * 17, e.rotation, 360, new Floatc2({get(x, y){
		var ang = Mathf.angle(x, y);
		Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1);
	}}));
});

const orbShoot = new Effect(21, e => {
	Draw.color(Pal.surge);
	for(var i = 0; i < 2; i++){
		var l = Mathf.signs[i];
		Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 67 * l);
	};
});

const orbTrail = new Effect(43, e => {
	var originalZ = Draw.z();
	
	Tmp.v1.trns(Mathf.randomSeed(e.id) * 360, Mathf.randomSeed(e.id * 341) * 12 * e.fin());

	Draw.z(Layer.bullet - 0.01);
    Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7 * e.fout() + 3, Pal.surge, 0.6);

	Draw.color(Pal.surge);
	Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7);

	Draw.z(originalZ);
});

const orbShootSmoke = new Effect(26, e => {
	Draw.color(Pal.surge);
	Angles.randLenVectors(e.id, 7, 80, e.rotation, 0, new Floatc2({get(x, y){
		Fill.circle(e.x + x, e.y + y, e.fout() * 4);
	}}));
});

const orbCharge = new Effect(38, e => {
	Draw.color(Pal.surge);
	Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({get(x, y){
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
	}}));
});

const orbChargeBegin = new Effect(71, e => {
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fin() * 3);

	Draw.color();
	Fill.circle(e.x, e.y, e.fin() * 2);
});

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
orb.hitEffect = orbHit;
orb.color = Pal.surge;
orb.trailEffect = orbTrail;
orb.trailChance = 0.4;
orb.scanRadius = 5 * Vars.tilesize;

const orbTurret = extendContent(ChargeTurret, "orb", {});
orbTurret.shootType = orb;
orbTurret.shootSound = Sounds.laser;
orbTurret.heatColor = Pal.turretHeat;
orbTurret.shootEffect = orbShoot;
orbTurret.smokeEffect = Fx.none;
orbTurret.chargeEffect = orbCharge;
orbTurret.chargeBeginEffect = orbChargeBegin;
