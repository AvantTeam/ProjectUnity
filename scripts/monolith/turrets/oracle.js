const laser = extendContent(LaserBulletType, 64, {});
laser.length = 180;
laser.sideAngle = 45;
laser.inaccuracy = 8;

const charge = new Effect(30, e => {
	Draw.color(Pal.lancerLaser);
	
	var angle = Mathf.randomSeed(e.id, 360) + Time.time();
	var dist = (1 - e.finpow()) * 20;
	
	Tmp.v1.trns(angle, dist);
	
	Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 5);
});

const chargeBegin = new Effect(40, e => {
	Draw.color(Pal.lancerLaser);
	
	Fill.circle(e.x, e.y, e.fin() * 6);
});

const oracle = extendContent(ChargeTurret, "oracle", {});
oracle.chargeEffect = charge;
oracle.chargeBeginEffect = chargeBegin;

oracle.entityType = () => extendContent(ChargeTurret.ChargeTurretBuild, oracle, {
	shoot(ammo){
		this.useAmmo();
		
		oracle.tr.trns(this.rotation, oracle.size * 4);
		oracle.chargeBeginEffect.at(this.x + oracle.tr.x, this.y + oracle.tr.y, this.rotation);
		
		for(var i = 0; i < oracle.chargeEffects; i++){
			Time.run(Mathf.random(oracle.chargeMaxDelay), () => {
				if(!this.isValid()) return;
				
				oracle.tr.trns(this.rotation, oracle.size * 4);
				oracle.chargeEffect.at(this.x + oracle.tr.x, this.y + oracle.tr.y, this.rotation);
			});
		};
		this.shooting = true;
		
		Time.run(oracle.chargeTime, () => {
			if(!this.isValid()) return;
			
			oracle.tr.trns(this.rotation, oracle.size * 4);
			
			this.recoil = oracle.recoilAmount;
			this.heat = 1;
			
			for(var i = 0; i < oracle.shots; i++){
				Time.run(i * 2, () => {
					this.bullet(ammo, this.rotation + Mathf.range(oracle.inaccuracy));
				});
			};
			for(var i = 0; i < 3; i++){
				Time.run(i, () => {
					this.bullet(laser, this.rotation + Mathf.range(laser.inaccuracy));
					
					Fx.hitLancer.at(this.x + oracle.tr.x, this.y + oracle.tr.y, this.rotation);
					Sounds.laser.at(this.tile, Mathf.random(0.7, 0.9));
				});
			};
			
			this.effects();
			
			this.shooting = false;
		});
	}
});
