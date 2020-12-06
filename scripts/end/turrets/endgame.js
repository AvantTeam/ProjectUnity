const sEffect = this.global.unity.status;
const fLib = this.global.unity.funclib;
const soundLib = this.global.unity.sounds;
const exefLib = this.global.unity.extraeffects;

const tempVec = new Vec2();
const tempColor = new Color();

const tempSeq = new Seq();
const bulletSeq = new Seq(1023);

const ringProgresses = [0.013, 0.035, 0.024];
const ringDirection = [1, -1, 1];

const getDamage = (type) => {
	return type.damage + type.splashDamage + (Math.max(type.lightningDamage, 0) * type.lightning * type.lightningLength);
};

const offsetSin = (offset, scl) => {
	return Mathf.absin(Time.time + (offset * Mathf.radDeg), scl, 0.5) + 0.5;
};

const offsetSinB = (offset, scl) => {
	return Mathf.absin(Time.time + (offset * Mathf.radDeg), scl, 0.25);
};

const endGameShoot = new Effect(45, 820 * 2, e => {
	var curve = Mathf.curve(e.fin(), 0, 0.2) * 820;
	var curveB = Mathf.curve(e.fin(), 0, 0.7);
	
	Draw.color(Color.red, Color.valueOf("ff000000"), curveB);
	Draw.blend(Blending.additive);
	
	Fill.poly(e.x, e.y, Lines.circleVertices(curve), curve);
	
	Draw.blend();
});
endGameShoot.layer = 110.99;

const darkShockWave = new Effect(56, 820 * 2, e => {
	const sides = 148;
	var poly = [];
	
	Draw.color(Color.black);
	Lines.stroke(e.fout() * 1.5 + 0.5);
	
	for(var i = 0; i < sides; i++){
		tempVec.trns(360 / sides * i, e.finpow() * 790 + (Mathf.clamp(Mathf.randomSeed(Mathf.round(Time.time * 270 + (e.id * 1241) + Mathf.round(i / 3)), -390, 390), -340, 0) * e.fin())).add(e.x, e.y);
		
		for(var b = 0; b < 2; b++){
			poly[(i * 2 + b)] = b == 0 ? tempVec.x : tempVec.y;
		};
		if(Mathf.randomSeed(Mathf.round(Time.time * 270 + (e.id * 2311) + ((1 + i) * 342)), 0, 20) / 20 >= 0.9){
			Lines.line(e.x, e.y, tempVec.x, tempVec.y);
		};
	};
	Lines.polyline(poly, sides * 2, true);
});

const vaporizeTile = new Effect(126, 512, e => {
	Draw.mixcol(Color.red, 1);
	Draw.blend(Blending.additive);
	
	Fill.square(e.x, e.y, e.fout() * (e.rotation * (Vars.tilesize / 2)));
	
	Draw.blend();
	Draw.mixcol();
});
vaporizeTile.layer = 111;

const vaporize = new Effect(126, 512, e => {
	Draw.mixcol(Color.red, 1);
	Draw.color(1, 1, 1, e.fout());
	Draw.blend(Blending.additive);
	
	fLib.simpleUnitDrawer(e.data, false);
	
	Draw.blend();
	Draw.color();
	Draw.mixcol();
});
vaporize.layer = 111;

const endgameLaser = new Effect(76, 820 * 2, e => {
	//const colors = ["f5303680", "f53036", "ffffff"];
	const colors = [Color.valueOf("f53036"), Color.valueOf("ff786e"), Color.white];
	const strokes = [2, 1.3, 0.6];
	var a = e.data[0];
	var b = e.data[1];
	var width = e.data[2];
	//Draw.blend(Blending.additive);
	tempVec.set(a).lerp(b, Mathf.curve(e.fin(), 0, 0.09));
	var originZ = Draw.z();
	for(var i = 0; i < 3; i++){
		Draw.z(originZ + (i / 1000));
		if(i != 2){
			Draw.color(tempColor.set(colors[i]).mul(1, 1 + offsetSinB(0, 5), 1 + offsetSinB(90, 5), 1));
		}else{
			Draw.color(Color.white);
		};
		//Draw.color(Color.valueOf(tempColor, colors[i]).mul(1, offsetSin(0, 5), offsetSin(90, 5), 1));
		
		Fill.circle(a.x, a.y, strokes[i] * 4 * width * e.fout());
		
		Fill.circle(tempVec.x, tempVec.y, strokes[i] * 4 * width * e.fout());
		
		Lines.stroke(strokes[i] * 4 * width * e.fout());
		Lines.line(a.x, a.y, tempVec.x, tempVec.y, false);
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
endgame.shootShake = 2.2;
endgame.reloadTime = 300;
endgame.absorbLasers = true;
endgame.eyeTime = endgame.timers++;
endgame.bulletTime = endgame.timers++;
endgame.buildType = () => {
	var endgameEntity = extendContent(PowerTurret.PowerTurretBuild, endgame, {
		damage(amount){
			if(this.verify()) return;
			var trueAmount = Mathf.clamp(amount / this._resist, 0, 360);
			this.super$damage(trueAmount);
			//this._inv = false;
			this._resist += 0.25 + Math.max((Mathf.clamp(amount, 0, 2147483648) - 560) / 80, 0);
			if(Number.isNaN(this._resist)) this._resist = 9223372036854775806;
			this._resistTime = 0;
		},
		deltaB(){
			return this.delta() * this.power.status;
		},
		setEff(){
			//this._inv = true;
			this._resist = 1;
			this._resistTime = 10;
			this._threatLevel = 1;
			this._ringProgress = [0, 0, 0];
			this._lastHealth = 0;
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
			this._eyesOffsetB = new Vec2();
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
			this.updateTreats();
			if(this.timer.get(endgame.eyeTime, 30) && this.target != null && !this.isControlled()){
				tempSeq.clear();
				//var tileTarget = Units.findEnemyTile(this.team, this.x, this.y, endgame.range, build => !build.dead);
				var lowest = endgame.range + 999;
				var dstC = endgame.range + 999;
                
                Vars.indexer.eachBlock(null, this.x, this.y, endgame.range, build => build.team != this.team, build => {
                    var dstD = Mathf.dst(this.x, this.y, build.x, build.y);
					if(build.team != this.team && !build.dead){
						//dstC = Mathf.dst(this.x, this.y, build.x, build.y);
						if(dstD < dstC){
							lowest = Math.min(lowest, dstD);
							dstC = dstD;
							if(tempSeq.size >= 16){
								tempSeq.remove(0);
							};
							tempSeq.add(build);
						}else if(Mathf.equal(lowest, dstD, 32)){
							if(tempSeq.size >= 16){
								tempSeq.remove(0);
							};
							tempSeq.add(build);
						};
					};
                });
				for(var i = 0; i < 16; i++){
					var tmpTarget = fLib.targetUnique(this.team, this.x, this.y, endgame.range, new Seq(this._targetsB));
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
		updateTreats(){
			this._threatLevel = 1;
			var rnge = endgame.range / 1.5;
			Units.nearbyEnemies(this.team, this.x - rnge, this.y - rnge, rnge * 2, rnge * 2, e => {
				if(Mathf.within(this.x, this.y, e.x, e.y, rnge) && !e.dead){
					this._threatLevel += (e.maxHealth / 440);
				};
				if(e.vel.len() >= 13){
					e.vel.setLength(0);
					e.apply(sEffect.endgameDisable);
					//sEffect
				};
			});
		},
		updateEyes(){
			this.updateEyeOffset();
			this._eyesOffsetB.lerp(this._eyesTargetOffset, Mathf.clamp(0.12 * Time.delta));
			//this._eyesOffset.lerp(this._eyesTargetOffset, Mathf.clamp(0.12 * Time.delta));
			this._eyesOffset.set(this._eyesOffsetB);
			this._eyesOffset.add(Mathf.range(this.reload / endgame.reloadTime) / 2, Mathf.range(this.reload / endgame.reloadTime) / 2);
			this._eyesOffset.limit(2);
			if(((this.target != null && !this.isControlled()) || (this.isControlled() && this.unit.isShooting)) && this.consValid() && this.power.status >= 0.0001){
				this._eyeReloads[0] += this.deltaB();
				this._eyeReloads[1] += this.deltaB();
			};
			
			if(this.consValid() && this.power.status >= 0.0001){
				this.updateEyeTargeting();
			};
			
			if(this._eyeReloads[0] >= 15){
				this._eyeReloads[0] = 0;
				if(!this.isControlled()){
					if(this._targetsB[this._eyeSequenceA] != null) this.eyeShoot(this._eyeSequenceA);
				}else{
					if(this.unit.isShooting) this.playerEyeShoot(this._eyeSequenceA);
				};
				this._eyeSequenceA = (this._eyeSequenceA + 1) % 8;
			};
			if(this._eyeReloads[1] >= 5){
				this._eyeReloads[1] = 0;
				if(!this.isControlled()){
					if(this._targetsB[this._eyeSequenceB + 8] != null) this.eyeShoot(this._eyeSequenceB + 8);
				}else{
					if(this.unit.isShooting) this.playerEyeShoot(this._eyeSequenceB + 8);
				};
				this._eyeSequenceB = (this._eyeSequenceB + 1) % 8;
			};
		},
		hasAmmo(){
			return true;
		},
		peekAmmo(){
            return endgame.shootType;
        },
		shouldActiveSound(){
			return this.power.status >= 0.0001;
		},
		effects(){
			endgame.shootSound.at(this.x, this.y, Mathf.random(0.9, 1.1));
		},
		killUnitsC(){
			var rnge = endgame.range;
			bulletSeq.clear();
			Units.nearbyEnemies(this.team, this.x - rnge, this.y - rnge, rnge * 2, rnge * 2, e => {
				if(Mathf.within(this.x, this.y, e.x, e.y, rnge) && !e.dead){
					endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.y * 2)), new Vec2(e.x, e.y), 1]);
					vaporize.at(e.x, e.y, 0, e);
					e.kill();
					if(e.isFlying()) bulletSeq.add(e);
				};
			});
			bulletSeq.each(e => {
                Groups.unit.remove(e);
                Groups.all.remove(e);
                Groups.draw.remove(e);
            });
			bulletSeq.clear();
		},
		killTilesC(){
			var shouldLaser = 0;
			fLib.trueEachBlock(this.x, this.y, endgame.range, build => {
				if(build.team != this.team && build != this && !build.dead && build.block != null){
					if(build.block.size >= 3) vaporizeTile.at(build.x, build.y, build.block.size);
					if(shouldLaser % 5 == 0 || build.block.size >= 5) endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.y * 2)), new Vec2(build.x, build.y), 1]);
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
		playerEyeShoot(index){
			var rnge = 15;
			var ux = this.unit.aimX;
			var uy = this.unit.aimY;
			if(!Mathf.within(this.x, this.y, ux, uy, endgame.range * 1.5)) return;
			fLib.trueEachBlock(this.unit.aimX, this.unit.aimY, 15, build => {
				if(!build.dead && build.team != this.team){
					build.damage(490);
					endgameLaser.at(this.x, this.y, 0, [new Vec2(ux, uy), new Vec2(build.x, build.y), 0.525]);
				};
			});
			Units.nearbyEnemies(this.team, ux - rnge, uy - rnge, rnge * 2, rnge * 2, e => {
				if(Mathf.within(ux, uy, e.x, e.y, rnge + e.hitSize) && !e.dead){
					e.damage(490 * this._threatLevel);
					if(e.dead) vaporize.at(e.x, e.y, 0, e);
					endgameLaser.at(this.x, this.y, 0, [new Vec2(ux, uy), new Vec2(e.x, e.y), 0.525]);
				};
			});
			tempVec.set(this._eyesVecArray[index]);
			tempVec.add(ux, uy);
			tempVec.scl(0.5);
			//endgameLaser.at(this.x, this.y, 0, [this._eyesVecArray[index], new Vec2(ux, uy), 0.625]);
			endgameLaser.at(tempVec.x, tempVec.y, 0, [this._eyesVecArray[index], new Vec2(ux, uy), 0.625]);
			soundLib.endgameSmallShoot.at(this.x, this.y, Mathf.random(0.9, 1.1));
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
				e.damage(350 * this._threatLevel);
				if(e.dead){
                    vaporize.at(e.x, e.y, 0, e);
                    Groups.unit.remove(e);
                    Groups.all.remove(e);
                    Groups.draw.remove(e);
                };
				this._eyesVecArray[index].set(tempVec);
				this._eyesVecArray[index].add(this.x, this.y);
				endgameLaser.at(this.x, this.y, 0, [this._eyesVecArray[index], new Vec2(e.x, e.y), 0.625]);
				soundLib.endgameSmallShoot.at(this.x, this.y, Mathf.random(0.9, 1.1));
			};
		},
		updateAntiBullets(){
			bulletSeq.clear();
			if(this.power.status >= 0.0001 && this.timer.get(endgame.bulletTime, 4)){
				var damageFull = 0;
				Groups.bullet.intersect(this.x - endgame.range, this.y - endgame.range, endgame.range * 2, endgame.range * 2, b => {
					if(Mathf.within(this.x, this.y, b.x, b.y, endgame.range) && b.team != this.team){
						damageFull += getDamage(b.type);
						var currentBullet = b.type;
						var totalFragBullets = 1;
						
						for(var f = 0; f < 16; f++){
							if(currentBullet.fragBullet == null) break;
							
							var frag = currentBullet.fragBullet;
							
							totalFragBullets *= currentBullet.fragBullets;
							
							damageFull += getDamage(frag) * totalFragBullets;
							
							currentBullet = currentBullet.fragBullet;
						};
					};
				});
				Groups.bullet.intersect(this.x - endgame.range, this.y - endgame.range, endgame.range * 2, endgame.range * 2, b => {
					if(Mathf.within(this.x, this.y, b.x, b.y, endgame.range) && b.team != this.team){
						var damageB = 0;
						var currentBullet = b.type;
						var damageV = getDamage(b.type);
						var totalFragBullets = 1;
						
						for(var f = 0; f < 16; f++){
							if(currentBullet.fragBullet == null) break;
							
							var frag = currentBullet.fragBullet;
							
							totalFragBullets *= currentBullet.fragBullets;
							
							damageB += getDamage(frag) * totalFragBullets;
							
							currentBullet = currentBullet.fragBullet;
						};
						
						/*if((b.getShieldDamage() + damageB > 1000 || b.getBulletType().splashDamageRadius > 120 || damageFull > 12000) && b != null){
							b.remove();
						}*/
						if(damageV + damageB > 1500 || b.type.splashDamageRadius > 120 || damageFull > 12000 || (b.owner != null && !this.within(b.owner, endgame.range))){
							//b.remove();
							bulletSeq.add(b);
							endgameLaser.at(this.x, this.y, 0, [new Vec2(this.x + (this._eyesOffset.x * 2), this.y + (this._eyesOffset.y * 2)), new Vec2(b.x, b.y), 0.625]);
						}
					};
				});
				//if(!bulletSeq.isEmpty()) endgame.shootSound.at(this.x, this.y, Mathf.random(0.9, 1.1));
				if(!bulletSeq.isEmpty()) soundLib.endgameSmallShoot.at(this.x, this.y, Mathf.random(0.9, 1.1));
				bulletSeq.each(b => b.remove());
				bulletSeq.clear();
			};
		},
		verify(){
			return (this.health < this._lastHealth - 860) || Number.isNaN(this.health);
		},
		/*updateShooting(){
			this.super$updateShooting();
		},*/
		updateTile(){
			if(this.verify()) this.health = this._lastHealth;
			this._lastHealth = this.health;
			if(this._resistTime >= 10){
				//this._resist = 1;
				this._resist = Math.max(1, this._resist - Time.delta);
			}else{
				this._resistTime += Time.delta;
			};
			this.updateEyes();
			if(this.power.status >= 0.0001){
				var value = this._eyesAlpha > this.power.status ? 1 : this.power.status;
				this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, this.power.status, 0.06 * value);
				//this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 1, 0.07);
			}else{
				this._eyesAlpha = Mathf.lerpDelta(this._eyesAlpha, 0, 0.06);
				//this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 0, 0.07);
			};
			if(this.consValid()){
				this.updateAntiBullets();
				this.super$updateTile();
			};
			if(this.isControlled()){
				var con = this.unit.controller;
				this._eyesTargetOffset.trns(Angles.angle(this.x, this.y, con.mouseX, con.mouseY), Mathf.dst(this.x, this.y, con.mouseX, con.mouseY) / (endgame.range / 3));
			};
			if(((this.target != null && !this.isControlled()) || (this.isControlled() && this.unit.isShooting)) && this.power.status >= 0.0001){
				this._eyeResetTime = 0;
				if(!this.isControlled()){
					this._eyesTargetOffset.trns(Angles.angle(this.x, this.y, this.targetPos.x, this.targetPos.y), Mathf.dst(this.x, this.y, this.targetPos.x, this.targetPos.y) / (endgame.range / 3));
					//tempVec.set(this.targetPos.x, this.targetPos.y).sub(this.x, this.y).div(new Vec2(endgame.range / 3, endgame.range / 3));
					//this._eyesOffset.set(tempVec);
				};
				this._eyesTargetOffset.limit(2);
				this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, this.power.status, 0.07 * this.power.status);
				for(var i = 0; i < 3; i++){
					this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 360 * ringDirection[i], ringProgresses[i] * this.power.status);
				};
				const offsetF = 0.90;
				var chance = (((this.reload / endgame.reloadTime) * offsetF) + (1 - offsetF)) * this.power.status;
				var randomAngle = Mathf.random(360);
				tempVec.trns(randomAngle, 18.5);
				if(Mathf.chanceDelta(0.75 * chance)) exefLib.createLightning(this.x + tempVec.x, this.y + tempVec.y, randomAngle, 80, Color.red, Color.black, this.team, 520 * this.power.status, 520, this.targetPos);
			}else{
				//this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 0, 0.07);
				if(this._eyeResetTime >= 60){
					this._lightsAlpha = Mathf.lerpDelta(this._lightsAlpha, 0, 0.07);
					for(var i = 0; i < 3; i++){
						this._ringProgress[i] = Mathf.lerpDelta(this._ringProgress[i], 0, ringProgresses[i] * this.power.status);
					};
				}else{
					this._eyeResetTime += Time.delta;
				};
			};
		},
		kill(){
			if(this._lastHealth < 10){
				Call.tileDestroyed(this);
			};
		},
		collision(other){
			//const tempRange = 10;
			var amount = other.owner != null && !this.within(other.owner, endgame.range) ? 0 : other.damage * other.type.tileDamageMultiplier;
			this.damage(amount);
			if(other.owner != null && !this.within(other.owner, endgame.range)){
				other.owner.damage(0.5 * other.owner.maxHealth * Math.max(this._resist / 10, 1));
				other.remove();
			};
			return true;
		},
		shoot(type){
			this.consume();
			this.killTilesC();
			this.killUnitsC();
			endgame.shootSound.at(this.x, this.y, Mathf.random(0.9, 1.1));
			//this.effects();
			darkShockWave.at(this.x, this.y);
			endGameShoot.at(this.x, this.y);
			//if(this.target != null && this.target instanceof Healthc) this.target.kill();
			//this.super$shoot(type);
		}
	});
	endgameEntity.setEff();
	return endgameEntity;
};