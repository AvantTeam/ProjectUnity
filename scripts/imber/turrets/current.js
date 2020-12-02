const effects = this.global.unity.effects;

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
current.colors = [Pal.surge.cpy(), Pal.surge, Color.white];

const currentTurret = extendContent(PowerTurret, "current", {});
currentTurret.shootType = current;
currentTurret.shootSound = Sounds.laserbig;
currentTurret.chargeEffect = effects.imberCurrentCharge;
currentTurret.chargeBeginEffect = effects.imberCurrentChargeBegin;
currentTurret.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();
currentTurret.buildType = () => extendContent(PowerTurret.PowerTurretBuild, currentTurret, {
	shoot(type){
        let block = currentTurret;
        let tr = block.tr;
        
		this.useAmmo();

        tr.trns(this.rotation, block.size * Vars.tilesize / 2 - 8);
		block.chargeBeginEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);

		for(var i = 0; i < block.chargeEffects; i++){
			Time.run(Mathf.random(block.chargeMaxDelay), () => {
				if(!this.isValid()) return;
				
				block.tr.trns(this.rotation, block.size * Vars.tilesize / 2);
				block.chargeEffect.at(this.x + tr.x, this.y + tr.y, this.rotation);
			});
		};

		this.charging = true;

		Time.run(block.chargeTime, () => {
			if(!this.isValid()) return;
			
			block.tr.trns(this.rotation, block.size * Vars.tilesize / 2);
			block.recoil = block.recoilAmount;
			this.heat = 1;
			this.bullet(type, this.rotation + Mathf.range(block.inaccuracy));
			this.effects();
			this.charging = false;
		});
	},
    
    bullet(type, angle){
        let tr = currentTurret.tr;
        
        type.create(this, this.team, this.x + tr.x, this.y + tr.y, angle, 1 + Mathf.range(currentTurret.velocityInaccuracy), 1);
    }
});