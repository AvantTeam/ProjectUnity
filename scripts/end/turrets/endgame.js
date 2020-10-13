const fLib = this.global.unity.funclib;

const tempVec = new Vec2();
const tempColor = new Color();

const tempSeq = new Seq();
const bulletSeq = new Seq(1023);

const ringProgresses = [0.013, 0.035, 0.024];
const ringDirection = [1, -1, 1];

const offsetSin = (offset, scl) => {
	return Mathf.absin(Time.time() + (offset * Mathf.radDeg), scl, 0.5) + 0.5;
};

const endgameLaser = new Effect(76, 820 * 2, e => {
	//const colors = ["f5303680", "f53036", "ffffff"];
	const colors = [Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white];
	const strokes = [2, 1.3, 0.6];
	var a = e.data[0];
	var b = e.data[1];
	var width = e.data[2];
	//Draw.blend(Blending.additive);
	var originZ = Draw.z();
	for(var i = 0; i < 3; i++){
		Draw.z(originZ + (i / 1000));
		if(i != 2){
			Draw.color(tempColor.set(colors[i]).mul(1, offsetSin(0, 5), offsetSin(90, 5), 1));
		}else{
			Draw.color(Color.white);
		};
		//Draw.color(Color.valueOf(tempColor, colors[i]).mul(1, offsetSin(0, 5), offsetSin(90, 5), 1));
		
		Fill.circle(a.x, a.y, strokes[i] * 4 * width * e.fout());
		
		Fill.circle(b.x, b.y, strokes[i] * 4 * width * e.fout());
		
		Lines.stroke(strokes[i] * 4 * width * e.fout());
		Lines.line(a.x, a.y, b.x, b.y, false);
	};
	Draw.z(originZ);
	//Draw.blend();
});

const fakeBullet = new BasicBulletType();
fakeBullet.damage = Number.MAX_VALUE;

const endgame = extendContent(PowerTurret, "endgame", {
	load(){
		this.super$load();
		this.baseRegion = Core.atlas.find(this.name + "-base");
		this.baseLightsRegion = Core.atlas.find(this.name + "-base-lights");
		this.bottomLightsRegion = Core.atlas.find(this.name + "-bottom-lights");
		this.eyeMainRegion = Core.atlas.find(this.name + "-eye");
		
		this.ringABottomRegion = Core.atlas.find(this.name + "-ring1-bottom");
		this.ringAEyesRegion = Core.atlas.find(this.name + "-ring1-eyes");
		this.ringARegion = Core.atlas.find(this.name + "-ring1");
		this.ringALightsRegion = Core.atlas.find(this.name + "-ring1-lights");
		
		this.ringBBottomRegion = Core.atlas.find(this.name + "-ring2-bottom");
		this.ringBEyesRegion = Core.atlas.find(this.name + "-ring2-eyes");
		this.ringBRegion = Core.atlas.find(this.name + "-ring2");
		this.ringBLightsRegion = Core.atlas.find(this.name + "-ring2-lights");
		
		this.ringCRegion = Core.atlas.find(this.name + "-ring3");
		this.ringCLightsRegion = Core.atlas.find(this.name + "-ring3-lights");
	}
});
endgame.health = 68000;
endgame.shootType = fakeBullet;
endgame.outlineIcon = false;
endgame.powerUse = 320;
endgame.shootShake = 1.2;
endgame.reloadTime = 300;
endgame.eyeTime = endgame.timers++;
endgame.bulletTime = endgame.timers++;
endgame.buildType = () => {
	var endgameEntity = extendContent(Turret.TurretBuild, endgame, {
		damage(amount){
			var trueAmount = Mathf.clamp(amount, 0, 360);
			this.super$damage(trueAmount);
		},
		setEff(){
			this._ringProgress = [0, 0, 0];
			this._targetsB = [];
			this._eyeReloads = [0, 0];
			this._eyeResetTime = 0;
			this._eyeSequenceA = 0;
			this._eyeSequenceB = 0;
			this._eyesAlpha = 0;
			this._lightsAlpha = 0;
			this._eyesTargetOffset = new Vec2();
			this._eyesVecArray = [];
			this._eyesOffset = new Vec2();
			for(var i = 0; i < 16; i++){
				this._targetsB[i] = null;
				this._eyesVecArray[i] = new Vec2();
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
			if(this.timer.get(endgame.eyeTime, 30) && this.target != null){
				tempSeq.clear();
				//var tileTarget = Units.findEnemyTile(this.team, this.x, this.y, endgame.range, build => !build.dead);
				var dstC = endgame.range + 999;
				fLib.trueEachBlock(this.x, this.y, endgame.range / 2, build => {
					if(build.team != this.team && !build.dead && Mathf.dst(this.x, this.y, build.x, build.y) < dstC){
						dstC = Mathf.dst(this.x, this.y, build.x, build.y);
						if(tempSeq.size >= 16){
							tempSeq.remove(0);
						};
						tempSeq.add(build);
					};
				});
				for(var i = 0; i < 16; i++){
					var tmpTarget = fLib.targetUnique(this.team, this.x, this.y, endgame.range / 2, new Seq(this._targetsB));
					if(tmpTarget == null && tempSeq.size > 0.0001){
						//tmpTarget = Units.findEnemyTile(this.team, this.x, this.y, endgame.range, build => !build.dead);
						//tmpTarget = tileTarget;
						//tmpTarget = tempSeq.get(Mathf.round(i % (tempSeq.items.length - 1)));
						//var value = i % (tempSeq.size - 1);
						//tmpTarget = tempSeq.items[value];
						tmpTarget = tempSeq.random();
					};
					this._targetsB[i] = tmpTarget;
				};
			};
		},
		draw(){
			var originZ = Draw.z();
			Draw.rect(endgame.baseRegion, this.x, this.y);
			Draw.z(originZ + 0.01);
			Draw.rect(endgame.ringABottomRegion, this.x, this.y, this._ringProgress[0]);
			Draw.rect(endgame.ringBBottomRegion, this.x, this.y, this._ringProgress[1]);
			Draw.z(originZ + 0.02);
			Draw.rect(endgame.ringARegion, this.x, this.y, this._ringProgress[0]);
			Draw.rect(endgame.ringBRegion, this.x, this.y, this._ringProgress[1]);
			Draw.rect(endgame.ringCRegion, this.x, this.y, this._ringProgress[2]);
			
			Draw.blend(Blending.additive);
			//Draw.color(1, offsetSin(0, 5), offsetSin(90, 5), this._lightsAlpha * offsetSin(0, 12));
			Draw.z(originZ + 0.005);
			Draw.color(1, offsetSin(0, 5), offsetSin(90, 5), this._eyesAlpha);
			Draw.rect(endgame.bottomLightsRegion, this.x, this.y);
			Draw.z(originZ + 0.015);
			/*Draw.rect(endgame.ringAEyesRegion, this.x, this.y);
			Draw.rect(endgame.ringBEyesRegion, this.x, this.y);
			Draw.rect(endgame.eyeMainRegion, this.x, this.y);*/
			const regions = [endgame.ringAEyesRegion, endgame.ringBEyesRegion, endgame.eyeMainRegion];
			const regionsB = [endgame.ringALightsRegion, endgame.ringBLightsRegion, endgame.ringCLightsRegion];
			const trnScl = [1, 0.9, 2];
			for(var i = 0; i < 3; i++){
				var h = 1 + i;
				Draw.color(1, offsetSin(0 + (10 * h), 5), offsetSin(90 + (10 * h), 5), this._eyesAlpha);
				Draw.rect(regions[i], this.x + (this._eyesOffset.x * trnScl[i]), this.y + (this._eyesOffset.y * trnScl[i]), this._ringProgress[i]);
			};
			Draw.z(originZ + 0.005);
			Draw.color(1, offsetSin(0, 5), offsetSin(90, 5), this._lightsAlpha * offsetSin(0, 12));
			Draw.rect(endgame.baseLightsRegion, this.x, this.y);
			Draw.z(originZ + 0.025);
			for(var i = 0; i < 3; i++){
				var h = 1 + i;
				Draw.color(1, offsetSin(0 + (10 * h), 5), offsetSin(90 + (10 * h), 5), this._lightsAlpha * offsetSin(0 + (5 * h), 12));
				Draw.rect(regionsB[i], this.x, this.y, this._ringProgress[i]);
			};
			Draw.blend();
			Draw.z(originZ);
		},
		updateEyes(){
			this.updateEyeOffset();
			this._eyesOffset.lerp(this._eyesTargetOffset, Mathf.clamp(0.08 * Time.delta));
			if(this.target != null){
				this._eyeReloads[0] += this.delta();
				this._eyeReloads[1] += this.delta();
			};
			
			if(this.consValid() && this.power.status >= 0.0001){
				this.updateEyeTargeting();
			};
			
			if(this._eyeReloads[0] >= 15){
				this._eyeReloads[0] = 0;
				if(this._targetsB[this._eyeSequenceA] != null) this.eyeShoot(this._eyeSequenceA);
				this._eyeSequenceA = (this._eyeSequenceA + 1) % 8;
			};
			if(this._eyeReloads[1] >= 5){
				this._eyeReloads[1] = 0;
				if(this._targetsB[this._eyeSequenceB + 8] != null) this.eyeShoot(this._eyeSequenceB + 8);
				this._eyeSequenceB = (this._eyeSequenceB + 1) % 8;
			};
		},
		hasAmmo(){
			return true;
		},
		peekAmmo(){
            return endgame.shootType;
        },
		effects(){
			
		},
		killUnitsC(){
			var rnge = endgame.range;
			Units.nearbyEnemies(this.team, this.x - rnge, this.y - rnge, rnge * 2, rnge * 2, e => {
				if(Mathf.within(this.x, this.y, e.x, e.y, rnge) && !e.dead){
					endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.x * 2)), new Vec2(e.x, e.y), 1]);
					e.kill();
				};
			});
		},
		killTilesC(){
			var shouldLaser = 0;
			fLib.trueEachBlock(this.x, this.y, endgame.range, build => {
				if(build.team != this.team && build != this && !build.dead && build.block != null){
					if(shouldLaser % 5 == 0 || build.block.size >= 5) endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.x * 2)), new Vec2(build.x, build.y), 1]);
					build.kill();
					shouldLaser++;
				};
			});
		},
		updateEyeOffset(){
			for(var i = 0; i < 16; i++){
				var angleC = (360 / 8) * (i % 8);
				if(i >= 8){
					tempVec.trns(angleC + 22.5 + this._ringProgress[1], 25.75);
				}else{
					tempVec.trns(angleC + this._ringProgress[0], 36.75);
				};
				this._eyesVecArray[i].set(tempVec);
				this._eyesVecArray[i].add(this.x, this.y);
			};
		},
		eyeShoot(index){
			//var angOffset = 0;
			var angleC = (360 / 8) * (index % 8);
			if(index >= 8){
				tempVec.trns(angleC + 22.5 + this._ringProgress[1], 25.75);
			}else{
				tempVec.trns(angleC + this._ringProgress[0], 36.75);
			};
			var e = this._targetsB[index];
			if(e != null){
				e.damage(100);
				this._eyesVecArray[index].set(tempVec);
				this._eyesVecArray[index].add(this.x, this.y);
				endgameLaser.at(this.x, this.y, 0, [this._eyesVecArray[index], new Vec2(e.x, e.y), 0.625]);
			}
		},
		updateAntiBullets(){
			bulletSeq.clear();
			if(this.power.status >= 0.0001 && this.timer.get(endgame.bulletTime, 4)){
				var damageFull = 0;
				Groups.bullet.intersect(this.x - endgame.range, this.y - endgame.range, endgame.range * 2, endgame.range * 2, b => {
					if(Mathf.within(this.x, this.y, b.x, b.y, endgame.range) && b.team != this.team){
						damageFull += b.type.damage + b.type.splashDamage;
						var currentBullet = b.type;
						var totalFragBullets = 1;
						
						for(var f = 0; f < 16; f++){
							if(currentBullet.fragBullet == null) break;
							
							var frag = currentBullet.fragBullet;
							
							totalFragBullets *= currentBullet.fragBullets;
							
							damageFull += (frag.damage + frag.splashDamage) * totalFragBullets;
							
							currentBullet = currentBullet.fragBullet;
						};
					};
				});
				Groups.bullet.intersect(this.x - endgame.range, this.y - endgame.range, endgame.range * 2, endgame.range * 2, b => {
					if(Mathf.within(this.x, this.y, b.x, b.y, endgame.range) && b.team != this.team){
						var damageB = 0;
						var currentBullet = b.type;
						var damageV = b.type.damage + b.type.splashDamage;
						var totalFragBullets = 1;
						
						for(var f = 0; f < 16; f++){
							if(currentBullet.fragBullet == null) break;
							
							var frag = currentBullet.fragBullet;
							
							totalFragBullets *= currentBullet.fragBullets;
							
							damageB += (frag.damage + frag.splashDamage) * totalFragBullets;
							
							currentBullet = currentBullet.fragBullet;
						};
						
						/*if((b.getShieldDamage() + damageB > 1000 || b.getBulletType().splashDamageRadius > 120 || damageFull > 12000) && b != null){
							b.remove();
						}*/
						if(damageV + damageB > 1000 || b.type.splashDamageRadius > 120 || damageFull > 12000){
							//b.remove();
							bulletSeq.add(b);
							endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.x * 2)), new Vec2(b.x, b.y), 0.625]);
						}
					};
				});
				bulletSeq.each(b => b.remove());
				bulletSeq.clear();
			};
		},
		/*updateShooting(){
			this.super$updateShooting();
		},*/
		updateTile(){
			this.updateEyes();
			if(this.power.status >= 0.0001){
				//this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 1, 0.06);
				this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 1, 0.07);
			}else{
				//this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 0, 0.06);
				this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 0, 0.07);
			};
			if(this.consValid()){
				this.updateAntiBullets();
				this.super$updateTile();
			};
			if(this.target != null){
				this._eyeResetTime = 0;
				this._eyesTargetOffset.trns(Angles.angle(this.x, this.y, this.target.x, this.target.y), Mathf.dst(this.x, this.y, this.target.x, this.target.y) / (endgame.range / 3));
				this._eyesTargetOffset.limit(2);
				//this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 1, 0.07);
				this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 1, 0.06);
				for(var i = 0; i < 3; i++){
					this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 360 * ringDirection[i], ringProgresses[i]);
				};
			}else{
				//this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 0, 0.07);
				if(this._eyeResetTime >= 60){
					this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 0, 0.06);
					for(var i = 0; i < 3; i++){
						this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 0, ringProgresses[i]);
					};
				}else{
					this._eyeResetTime += Time.delta;
				};
			};
		},
		shoot(type){
			this.consume();
			this.killTilesC();
			this.killUnitsC();
			//if(this.target != null && this.target instanceof Healthc) this.target.kill();
			//this.super$shoot(type);
		}
	});
	endgameEntity.setEff();
	return endgameEntity;
};