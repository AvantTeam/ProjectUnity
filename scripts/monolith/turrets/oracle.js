const laser = extendContent(LaserBulletType, 64, {});
laser.length = 180;
laser.sideAngle = 45;
laser.inaccuracy = 8;

const oracle = extendContent(ChargeTurret, "oracle", {});
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
				this.bullet(ammo, this.rotation + Mathf.range(oracle.inaccuracy));
			};
			for(var i = 0; i < 3; i++){
				this.bullet(laser, this.rotation + Mathf.range(laser.inaccuracy));
			};
			
			this.effects();
			Sounds.laser.at(this.tile, Mathf.random(0.9, 1.1));
			
			this.shooting = false;
		});
	}
});
