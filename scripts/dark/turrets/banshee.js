const tempVec = new Vec2();

const standardDenseMassive = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardDenseMassive);
standardDenseMassive.damage *= 1.7;
standardDenseMassive.speed *= 1.3;
standardDenseMassive.width *= 1.34;
standardDenseMassive.height *= 1.34;
standardDenseMassive.lifetime *= 1.1;

const standardHomingMassive = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardHomingMassive);
standardHomingMassive.damage *= 1.5;
standardHomingMassive.reloadMultiplier = 1.3;
standardHomingMassive.homingPower = 0.09;
standardHomingMassive.speed *= 1.3;
standardHomingMassive.width *= 1.21;
standardHomingMassive.height *= 1.21;
standardDenseMassive.lifetime *= 1.1;

const standardIncendiaryMassive = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardIncendiaryBig, standardIncendiaryMassive);
standardIncendiaryMassive.damage *= 1.7;
standardIncendiaryMassive.speed *= 1.3;
standardIncendiaryMassive.width *= 1.34;
standardIncendiaryMassive.height *= 1.34;
standardIncendiaryMassive.lifetime *= 1.1;

const standardThoriumMassive = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardThoriumBig, standardThoriumMassive);
standardThoriumMassive.damage *= 1.7;
standardThoriumMassive.speed *= 1.3;
standardThoriumMassive.width *= 1.34;
standardThoriumMassive.height *= 1.34;
standardThoriumMassive.lifetime *= 1.1;

const banshee = extendContent(ItemTurret, "banshee", {
	load(){
		this.super$load();
		
		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
banshee.spread = 37;
banshee.shots = 2;
banshee.spreadA = 18.5;
banshee.spreadB = 11.75;
banshee.spreadC = 4.25;
banshee.reloadTimeB = 9;
banshee.reloadTimeC = 6;
banshee.buildType = () => {
	var build = extendContent(ItemTurret.ItemTurretBuild, banshee, {
		setEffects(){
			//this._reloadB = 0;
			this._shotCounterB = 0;
			this._reloadArray = [0, 0];
			
			//this._reloadC = 0;
			//this._shotCounterC = 0;
			this._shotCounterC = [0, 0];
		},
		shootC(type, indexx){
			var w = [banshee.spreadB, banshee.spreadC];
			var y = [36.5, 24.5];
			//var rot = this.rotation;
			this.recoil = Mathf.clamp(this.recoil + (banshee.recoilAmount / 2), 0, banshee.recoilAmount);
			
			var i = Mathf.sign((this._shotCounterC[indexx] % 2) - 0.5);
			banshee.tr.trns(this.rotation - 90, w[indexx] * i + Mathf.range(banshee.xRand), y[indexx]);
			
			tempVec.trns(this.rotation, Math.max(Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y), banshee.size * Vars.tilesize));
			var rot = Angles.angle(banshee.tr.x, banshee.tr.y, tempVec.x, tempVec.y);
			
			this.bullet(type, rot + Mathf.range(banshee.inaccuracy));
			
			this._shotCounterC[indexx] = this._shotCounterC[indexx] + 1;
			this.effects();
			this.useAmmo();
		},
		shoot(type){
			//var rot = this.rotation;
			this.recoil = banshee.recoilAmount;
			this.heat = 1;
			
			var i = Mathf.sign((this.shotCounter % 2) - 0.5);
			banshee.tr.trns(this.rotation - 90, banshee.spreadA * i + Mathf.range(banshee.xRand), banshee.size * Vars.tilesize / 2);
			
			tempVec.trns(this.rotation, Math.max(Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y), banshee.size * Vars.tilesize));
			var rot = Angles.angle(banshee.tr.x, banshee.tr.y, tempVec.x, tempVec.y);
			
			this.bullet(type, rot + Mathf.range(banshee.inaccuracy));
			
			this.shotCounter++;
			this.effects();
			this.useAmmo();
		},
		updateShooting(){
			this.super$updateShooting();
			//print(this.hasAmmo());
			var rel = [banshee.reloadTimeB, banshee.reloadTimeC];
			for(var h = 0; h < 2; h++){
				if(this.hasAmmo()){
					if(this._reloadArray[h] >= rel[h]){
						var typeB = this.peekAmmo();
						
						this.shootC(typeB, h);
						
						this._reloadArray[h] = 0;
					}else{
						this._reloadArray[h] = this._reloadArray[h] + this.delta() * this.peekAmmo().reloadMultiplier * this.baseReloadSpeed();
					};
				};
			};
		}
	});
	build.setEffects();
	return build;
};
banshee.ammo(
	Items.graphite, standardDenseMassive,
	Items.silicon, standardHomingMassive,
	Items.pyratite, standardIncendiaryMassive,
	Items.thorium, standardThoriumMassive
);