const standardDenseHeavy = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardDenseHeavy);
standardDenseHeavy.damage *= 1.6;
standardDenseHeavy.speed *= 1.3;
standardDenseHeavy.width *= 1.32;
standardDenseHeavy.height *= 1.32;
//standardDenseHeavy.lifetime *= 1.1;

const standardHomingHeavy = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardDenseBig, standardHomingHeavy);
standardHomingHeavy.damage *= 1.4;
standardHomingHeavy.reloadMultiplier = 1.3;
standardHomingHeavy.homingPower = 0.09;
standardHomingHeavy.speed *= 1.3;
standardHomingHeavy.width *= 1.19;
standardHomingHeavy.height *= 1.19;
//standardHomingHeavy.lifetime *= 1.1;

const standardIncendiaryHeavy = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardIncendiaryBig, standardIncendiaryHeavy);
standardIncendiaryHeavy.damage *= 1.6;
standardIncendiaryHeavy.speed *= 1.3;
standardIncendiaryHeavy.width *= 1.32;
standardIncendiaryHeavy.height *= 1.32;
//standardIncendiaryHeavy.lifetime *= 1.1;

const standardThoriumHeavy = new BasicBulletType();
JsonIO.json().copyFields(Bullets.standardThoriumBig, standardThoriumHeavy);
standardThoriumHeavy.damage *= 1.6;
standardThoriumHeavy.speed *= 1.3;
standardThoriumHeavy.width *= 1.32;
standardThoriumHeavy.height *= 1.32;
//standardThoriumHeavy.lifetime *= 1.1;

const ghost = extendContent(ItemTurret, "ghost", {
	load(){
		this.super$load();
		
		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
ghost.spread = 21;
ghost.spreadB = 4;
ghost.secondReloadTime = 6;
ghost.buildType = () => {
	var build = extendContent(ItemTurret.ItemTurretBuild, ghost, {
		setEffects(){
			this._reloadB = 0;
			this._shotCounterB = 0;
		},
		shootB(type){
			//print("AAAAAAAAA");
			this.recoil = Mathf.clamp(this.recoil + (ghost.recoilAmount / 2), 0, ghost.recoilAmount);
			
			var i = Mathf.sign((this._shotCounterB % 2) - 0.5);
			ghost.tr.trns(this.rotation - 90, ghost.spreadB * i + Mathf.range(ghost.xRand), 18.75);
			
			this.bullet(type, this.rotation + Mathf.range(ghost.inaccuracy));
			
			this._shotCounterB++;
			this.effects();
			this.useAmmo();
		},
		updateShooting(){
			this.super$updateShooting();
			//print(this.hasAmmo());
			if(this.hasAmmo()){
				if(this._reloadB >= ghost.secondReloadTime){
					var typeB = this.peekAmmo();
					
					this.shootB(typeB);
					
					this._reloadB = 0;
				}else{
					this._reloadB += this.delta() * this.peekAmmo().reloadMultiplier * this.baseReloadSpeed();
				};
			};
		}
	});
	build.setEffects();
	return build;
};
ghost.ammo(
	Items.graphite, standardDenseHeavy,
	Items.silicon, standardHomingHeavy,
	Items.pyratite, standardIncendiaryHeavy,
	Items.thorium, standardThoriumHeavy
);