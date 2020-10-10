const fLib = this.global.unity.funclib;

const tempVec = new Vec2();

const tempSeq = new Seq(256);

const ringProgresses = [0.01, 0.03, 0.02, 0.04];

const fakeBullet = new BasicBulletType();
fakeBullet.damage = Number.MAX_VALUE;

const endgame = extendContent(PowerTurret, "endgame", {
	load(){
		this.super$load();
		this.bottomRegion = Core.atlas.find(this.name + "-bottom");
	}
});
endgame.health = 68000;
endgame.shootType = fakeBullet;
endgame.outlineIcon = false;
endgame.powerUse = 320;
endgame.shootShake = 1.2;
endgame.reloadTime = 300;
endgame.eyeTime = endgame.timers++;
endgame.buildType = () => {
	var endgameEntity = extendContent(Turret.TurretBuild, endgame, {
		damage(amount){
			var trueAmount = Mathf.clamp(amount, 0, 360);
			this.super$damage(trueAmount);
		},
		setEff(){
			this._ringProgress = [0, 0, 0, 0];
			this._targetsB = [];
			this._eyeReloads = [0, 0];
			this._eyeSequenceA = 0;
			this._eyeSequenceB = 0;
			this._eyesAlpha = 0;
			this._lightsAlpha = 0;
			this._eyesTargetOffset = new Vec2();
			this._eyesOffset = new Vec2();
			for(var i = 0; i < 16; i++){
				this._targetsB[i] = null;
				//this._eyeReloads[i] = 0;
			};
		},
		validateEyeTarget(unit){
			return !Units.invalidateTarget(unit, this.team, this.x, this.y);
		},
		updateEyeTargeting(){
			for(var i = 0; i < 16; i++){
				if(!this.validateEyeTarget(this._targetsB[i])){
					this._targetsB[i] = null;
				};
			};
			if(this.timer.get(endgame.eyeTime, 20)){
				tempSeq.clear();
				for(var i = 0; i < 16; i++){
					var tmpTarget = fLib.targetUnique(this.team, this.x, this.y, endgame.range, tempSeq);
					if(tmpTarget != null){
						tempSeq.add(tmpTarget);
					}else{
						tmpTarget = Units.findEnemyTile(this.team, this.x, this.y, endgame.range, build => true);
					};
					this._targetsB[i] = tmpTarget;
				};
			};
		},
		updateEyes(){
			this._eyesOffset.lerp(this._eyesTargetOffset, Mathf.clamp(0.07 * Time.delta));
			this._eyeReloads[0] += this.delta();
			this._eyeReloads[1] += this.delta();
			
			this.updateEyeTargeting();
			
			if(this._eyeReloads[0] >= 50){
				this._eyeReloads[0] = 0;
				this._eyeSequenceA = (this._eyeSequenceA + 1) % 8;
			};
			if(this._eyeReloads[1] >= 35){
				this._eyeReloads[1] = 0;
				this._eyeSequenceB = (this._eyeSequenceB + 1) % 8;
			};
		},
		/*updateShooting(){
			this.super$updateShooting();
		},*/
		updateTile(){
			this.updateEyes();
			if(this.power.status >= 0.0001){
				this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 1, 0.06);
			}else{
				this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 0, 0.06);
			};
			if(this.consValid()){
				this.super$updateTile();
			};
			if(this.target != null){
				for(var i = 0; i < 4; i++){
					this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 360, ringProgresses[i]);
				};
			}else{
				for(var i = 0; i < 4; i++){
					this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 0, ringProgresses[i]);
				};
			};
		},
		shoot(type){
			this.consume();
			//this.super$shoot(type);
		}
	});
	endgameEntity.setEff();
	return endgameEntity;
};