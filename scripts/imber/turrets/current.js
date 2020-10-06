const currentCharge = new Effect(32, e => {
	Draw.color(Pal.surge, Color.white, e.fin());

	Angles.randLenVectors(e.id, 8, 420 + Mathf.random(24, 28) * e.fout(), e.rotation, 4, new Floatc2({get(x, y){
		Lines.stroke(0.3 + e.fout() * 2);
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14 + 0.5);
	}}));
	
	Lines.stroke(e.fin() * 1.5);
	Lines.circle(e.x, e.y, e.fout() * 60);
});

const currentChargeBegin = new Effect(260, e => {
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fin() * 7);

	Draw.color();
	Fill.circle(e.x, e.y, e.fin() * 3);
});

const current = extend(LaserBulletType, {});
current.length = 430;
current.damage = 450;
current.width = 20;
current.lifetime = 65;
current.lightningSpacing = 35;
current.lightningLength = 5;
current.lightningDelay = 1.1;
current.lightningLengthRand = 15;
current.lightningDamage = 50;
current.lightningAngleRand = 40;
current.largeHit = true;
current.lightColor = current.lightningColor = Pal.surge;
current.sideAngle = 15;
current.sideWidth = 0;
current.sideLength = 0;
current.colors = [Pal.surge.cpy(), Pal.surge, Color.white];

const currentTurret = extendContent(ChargeTurret, "current", {
	shoot(ammo){
		this.useAmmo();

		this.tr.trns(this.rotation, this.size * Vars.tilesize / 2);
		this.chargeBeginEffect.at(this.x + this.tr.x, this.y + this.tr.y, this.rotation);

		for(var i = 0; i < this.chargeEffects; i++){
			Time.run(Mathf.random(this.chargeMaxDelay), () => {
				if(!this.isValid()) return;
				
				this.tr.trns(this.rotation, this.size * Vars.tilesize / 2);
				this.chargeEffect.at(this.x + this.tr.x, this.y + this.tr.y, this.rotation);
			});
		};

		this.shooting = true;

		Time.run(this.chargeTime, () => {
			if(!this.isValid()) return;
			
			this.tr.trns(this.rotation, this.size * this.tilesize / 2);
			this.recoil = this.recoilAmount;
			this.heat = 1;
			this.bullet(ammo, this.rotation + Mathf.range(this.inaccuracy));
			this.effects();
			this.shooting = false;
		});
	}
});
currentTurret.shootType = current;
currentTurret.shootSound = Sounds.laserbig;
currentTurret.chargeEffect = currentCharge;
currentTurret.chargeBeginEffect = currentChargeBegin;
currentTurret.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();