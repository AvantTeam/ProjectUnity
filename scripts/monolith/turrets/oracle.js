const laser = extendContent(LaserBulletType, 64, {});
laser.length = 180;
laser.sideAngle = 45;
laser.inaccuracy = 8;

const charge = new Effect(30, e => {
	Draw.color(Pal.lancerLaser);

	var angle = Mathf.randomSeed(e.id, 360) + Time.time;
	var dist = (1 - e.finpow()) * 20;

	Tmp.v1.trns(angle, dist);

	Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 4.5, 45);
});

const chargeBegin = new Effect(40, e => {
	Draw.color(Pal.lancerLaser);

	Fill.circle(e.x, e.y, e.fin() * 6);
});

const oracle = extendContent(PowerTurret, "oracle", {});
oracle.chargeEffect = charge;
oracle.chargeTime = 40;
oracle.chargeBeginEffect = chargeBegin;

oracle.buildType = () => {
	var oracleEntity = extendContent(PowerTurret.PowerTurretBuild, oracle, {
		setVecA(){
			this._vectorTemp = new Vec2();
		},
		shoot(ammo){
			this.useAmmo();

			var tr = this._vectorTemp;

			tr.trns(this.rotation, oracle.size * 4);
			oracle.chargeBeginEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);

			for(var i = 0; i < oracle.chargeEffects; i++){
				Time.run(Mathf.random(oracle.chargeMaxDelay), () => {
					if(!this.isValid()) return;

					tr.trns(this.rotation, oracle.size * 4);
					oracle.chargeEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
				});
			};
			this.charging = true;

			Time.run(oracle.chargeTime, () => {
				if(!this.isValid()) return;

				tr.trns(this.rotation, oracle.size * 4);

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

						Fx.hitLancer.at(this.x + tr.x, this.y + tr.y, this.rotation);
						Sounds.laser.at(this.tile, Mathf.random(0.7, 0.9));
					});
				};

				this.effects();

				this.charging = false;
			});
		},
		bullet(type, angle){
			//var lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y) / type.range(), oracle.minRange / type.range(), oracle.range / type.range()) : 1;

			type.create(this, this.team, this.x + this._vectorTemp.x, this.y + this._vectorTemp.y, angle, 1 + Mathf.range(oracle.velocityInaccuracy), 1);
		}
	});
	oracleEntity.setVecA();
	return oracleEntity;
};
