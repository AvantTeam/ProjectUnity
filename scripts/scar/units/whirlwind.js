const sEffect = this.global.unity.status;
const effects = this.global.unity.effects;

const whirlWindMissile = extend(MissileBulletType, {});
whirlWindMissile.speed = 5;
whirlWindMissile.height = 10;
whirlWindMissile.shrinkY = 0;
whirlWindMissile.backColor = Color.valueOf("f53036");
whirlWindMissile.frontColor = Color.valueOf("ff786e");
whirlWindMissile.trailColor = Color.valueOf("f53036");
whirlWindMissile.splashDamage = 15;
whirlWindMissile.splashDamageRadius = 20;
whirlWindMissile.weaveMag = 3;
whirlWindMissile.weaveScale = 4;

const whirlWindLaser = extend(ContinuousLaserBulletType, {
	update(b){
		var lengthC = this.length;
		if(b.owner != null){
			var valA = Math.max(b.owner.vel.len() * 19, b.fdata * 0.95);
			//b.fdata = Mathf.clamp(b.owner.vel.len() * 2300, 0, this.length);
			//lengthC = Mathf.clamp(b.owner.vel.len() * 1200, 0, this.length);
			lengthC = Mathf.clamp(valA, 0, this.length);
			//print(lengthC);
		};
		//this.super$update(b);
		if(b.timer.get(1, 5)){
			//Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), b.fdata, this.largeHit);
			Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), lengthC, this.largeHit);
		};
		
		//if(b.fdata < 5) b.time = this.lifetime;
	},
	
	draw(b){
		var lengthC = this.length;
		if(b.owner != null){
			//b.fdata = Mathf.clamp(b.owner.vel.len() * 2300, 0, this.length);
			//lengthC = Mathf.clamp(b.owner.vel.len() * 1200, 0, this.length);
			var valA = Math.max(b.owner.vel.len() * 19, b.fdata * 0.95);
			lengthC = Mathf.clamp(valA, 0, this.length);
			//print(lengthC);
		};
		
		var realLength = Damage.findLaserLength(b, lengthC);
		var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
		var baseLen = realLength * fout;

		Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
		for(var s = 0; s < this.colors.length; s++){
			Draw.color(Tmp.c1.set(this.colors[s]).mul(1 + Mathf.absin(Time.time(), 1, 0.1)));
			for(var i = 0; i < this.tscales.length; i++){
				Tmp.v1.trns(b.rotation() + 180, (this.lenscales[i] - 1) * 35);
				Lines.stroke((this.width + Mathf.absin(Time.time(), this.oscScl, this.oscMag)) * fout * this.strokes[s] * this.tscales[i]);
				Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
			}
		};

		Tmp.v1.trns(b.rotation(), baseLen * 1.1);

		Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 40, this.lightColor, 0.7);
		Draw.reset();
    }
});
whirlWindLaser.largeHit = false;
whirlWindLaser.damage = 21;
whirlWindLaser.lifetime = 10 * 60;
whirlWindLaser.length = 160;
whirlWindLaser.width = 5;
whirlWindLaser.incendChance = 0;
whirlWindLaser.hitEffect = effects.coloredHitSmall;
whirlWindLaser.lightColor = Color.valueOf("f5303690");
whirlWindLaser.hitColor = Color.valueOf("f5303690");
whirlWindLaser.colors = [Color.valueOf("f5303690"), Color.valueOf("ff786e"), Color.white];
whirlWindLaser.strokes = [1.5, 1, 0.3];

const whirlWindMissileWeap = new Weapon();
whirlWindMissileWeap.rotate = true;
whirlWindMissileWeap.x = 4.2;
whirlWindMissileWeap.reload = 50;
whirlWindMissileWeap.inaccuracy = 1.1;
whirlWindMissileWeap.shots = 5;
whirlWindMissileWeap.shotDelay = 3;
whirlWindMissileWeap.bullet = whirlWindMissile;

const whirlWindLaserWeap = new Weapon();
whirlWindLaserWeap.mirror = false;
whirlWindLaserWeap.x = 0;
whirlWindLaserWeap.y = 4;
whirlWindLaserWeap.minShootVelocity = 5;
whirlWindLaserWeap.continuous = true;
whirlWindLaserWeap.reload = (2 * 60) + whirlWindLaser.lifetime;
whirlWindLaserWeap.shootStatus = sEffect.reloadFatigue;
whirlWindLaserWeap.shootStatusDuration = whirlWindLaser.lifetime;
whirlWindLaserWeap.bullet = whirlWindLaser;
whirlWindLaserWeap.shootCone = 20;

const whirlWind = extendContent(UnitType, "whirlwind", {});
whirlWind.constructor = () => extend(UnitEntity, {});
whirlWind.weapons.add(whirlWindLaserWeap);
whirlWind.weapons.add(whirlWindMissileWeap);
whirlWind.health = 280;
whirlWind.rotateSpeed = 4.5;
//whirlWind.omniMovement = false;
whirlWind.faceTarget = false;
whirlWind.flying = true;
whirlWind.speed = 8;
whirlWind.drag = 0.019;
whirlWind.accel = 0.028;
whirlWind.hitSize = 8;
whirlWind.engineOffset = 8;