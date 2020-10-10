const artillery = extend(ArtilleryBulletType, {});

artillery.hitEffect = Fx.blastExplosion;
artillery.knockback = 0.8;
artillery.speed = 2.1;
artillery.lifetime = 80;
artillery.width = artillery.height = 11;
artillery.collidesTiles = true;
artillery.ammoMultiplier = 4;
artillery.splashDamageRadius = 35;
artillery.splashDamage = 25;
artillery.backColor = Color.valueOf("d4816b");
artillery.frontColor = artillery.lightningColor = Color.valueOf("ffd37f");
artillery.smokeEffect = Fx.shootBigSmoke2;
artillery.shake = 4.5;
artillery.statusDuration = 60 * 10;

const artilleryWeapon = new Weapon("artillery");

artilleryWeapon.reload = 35;
artilleryWeapon.x = 5.5;
artilleryWeapon.y = -4;
artilleryWeapon.shots = 2;
artilleryWeapon.shotDelay = 3;
artilleryWeapon.inaccuracy = 5;
artilleryWeapon.rotate = true;
artilleryWeapon.shake = 3;
artilleryWeapon.rotateSpeed = 4;
artilleryWeapon.bullet = artillery;

var transformTime = 10;

const amphibi = extendContent(UnitType, "amphibi-naval", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

amphibi.constructor = () => {
	//var time = transformTime;
	var unit = extend(UnitWaterMove, {
		setTransTimeC(a){
			this._timeTrnsC = a;
		},
		update(){
			this.super$update();
			if(!(this.floorOn().isLiquid) || (this.floorOn() instanceof ShallowLiquid)){
				
				if(this._timeTrnsC < 0 || this._timeTrnsC > transformTime){
					var groundUnit = amphibiGround.create(this.team);
					groundUnit.set(this.x, this.y);
					groundUnit.rotation = this.rotation;
					groundUnit.add();
					groundUnit.vel.set(this.vel);
					if(this.isPlayer()){
						//groundUnit.controller(this.controller);
						groundUnit.controller = this.controller;
						if(groundUnit.controller.unit() != groundUnit.base()) groundUnit.controller.unit(groundUnit.base());
					};
					this.remove();
				}else{
					this._timeTrnsC -= Time.delta;
				}
			}
		}
	});
	unit.setTransTimeC(transformTime);

	return unit;
};

amphibi.weapons.add(artilleryWeapon);

const amphibiGround = extendContent(UnitType, "amphibi", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});

amphibiGround.constructor = () => {
	//var time = transformTime;
	var unit = extend(LegsUnit, {
		setTransTimeC(a){
			this._timeTrnsC = a;
		},
		update(){
			this.super$update();
			if(this.floorOn().isLiquid && !(this.floorOn() instanceof ShallowLiquid)){
				if(this._timeTrnsC < 0 || this._timeTrnsC > transformTime){
					var navalUnit = amphibi.create(this.team);
					navalUnit.set(this.x, this.y);
					navalUnit.rotation = this.rotation;
					navalUnit.add();
					navalUnit.vel.set(this.vel);
					if(this.isPlayer()){
						//navalUnit.controller(this.controller);
						navalUnit.controller = this.controller;
						if(navalUnit.controller.unit() != navalUnit.base()) navalUnit.controller.unit(navalUnit.base());
					};
					this.remove();
				}else{
					this._timeTrnsC -= Time.delta;
				}
			}
		}
	});
	unit.setTransTimeC(transformTime);

	return unit;
};

amphibiGround.weapons.add(artilleryWeapon);
