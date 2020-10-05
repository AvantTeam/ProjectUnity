const sEffect = this.global.unity.status;

const offsetA = 0.15;

const coloredHitSmall = new Effect(14, e => {
	Draw.color(Color.white, e.color, e.fin());
	
	e.scaled(7, s => {
		Lines.stroke(0.5 + s.fout());
		Lines.circle(e.x, e.y, s.fin() * 5);
	});
	
	Lines.stroke(0.5 + e.fout());
	
	Angles.randLenVectors(e.id, 5, e.fin() * 15, (x, y) => {
		var ang = Mathf.angle(x, y);
		Lines.lineAngle(e.x + x, e.y + y, ang, (e.fout() * 3) + 1);
	});
});

//TODO move this to an effect library
const trailProv = func(length => {
	var a = extendContent(Trail, length, {
		setC(length){
			this._pnt = new Seq(length);
		},
		draw(color, width){
			var points = this._pnt;
			
			Draw.color(color);

			for(var i = 0; i < points.size - 1; i++){
				var c = points.get(i);
				var n = points.get(i + 1);
				var size = width * 1 / this.length;

				var cx = Mathf.sin(c.z) * i * size;
				var cy = Mathf.cos(c.z) * i * size;
				var nx = Mathf.sin(n.z) * (i + 1) * size;
				var ny = Mathf.cos(n.z) * (i + 1) * size;
				Fill.quad(c.x - cx, c.y - cy, c.x + cx, c.y + cy, n.x + nx, n.y + ny, n.x - nx, n.y - ny);
			};

			Draw.reset();
		},
		updateC(x, y, rotation){
			if(this._pnt.size > length){
				//Pools.free(points.first());
				this._pnt.remove(0);
			};
			
			this._pnt.add(new Vec3(x, y, -rotation * Mathf.degRad));
		}
	});
	a.setC(length);
	
	return a;
});

const jetstreamMissile = extend(MissileBulletType, {});
jetstreamMissile.speed = 5;
jetstreamMissile.height = 12;
jetstreamMissile.width = 7;
jetstreamMissile.shrinkY = 0;
jetstreamMissile.backColor = Color.valueOf("f53036");
jetstreamMissile.frontColor = Color.valueOf("ff786e");
jetstreamMissile.trailColor = Color.valueOf("f53036");
jetstreamMissile.splashDamage = 40;
jetstreamMissile.splashDamageRadius = 20;
jetstreamMissile.weaveMag = 3;
jetstreamMissile.weaveScale = 4;

const jetstreamLaser = extend(ContinuousLaserBulletType, {
	update(b){
		//var lengthC = this.length;
		if(b.data == null){
			b.data = [0, trailProv.get(3), b.rotation(), new WindowedMean(10)];
			b.data[3].fill(0);
		};
		//var angDst = Angles.angleDist(b.rotation(), b.data[2]);
		var angDst = Angles.angleDist(b.rotation(), b.data[2]);
		b.data[3].add(angDst);
		angDst = b.data[3].rawMean();
		if(b.owner != null){
			//var valA = Math.max(b.owner.vel.len() * 19, b.fdata * 0.95);
			//lengthC = Mathf.clamp(valA, 0, this.length);
			b.data[0] = Mathf.clamp(b.data[0] + (b.owner.vel.len() / 2), 0, this.length) + (angDst * 7);
		};
		//this.super$update(b);
		var realLength = Damage.findLaserLength(b, b.data[0]);
		var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
		var baseLen = realLength * fout;
		
		//var angDst = Angles.angleDist(b.rotation(), b.data[2]);
		
		var lenRanged = baseLen + Mathf.range(16);
		
		if(b.timer.get(1, 5)){
			Damage.collideLine(b, b.team, Fx.none, b.x, b.y, b.rotation(), b.data[0], this.largeHit);
		};
		
		if(Mathf.chanceDelta(0.1 + Mathf.clamp(angDst / 25)) && Mathf.round(lenRanged / 8) >= 1){
			Lightning.create(b.team, Color.valueOf("f53036"), 6 + (angDst * 1.7), b.x, b.y, b.rotation(), Mathf.round(lenRanged / 8));
		};
		
		b.data[2] = b.rotation();
		//b.data[3].add(angDst);
		
		Tmp.v2.trns(b.rotation(), baseLen / 2);
		b.data[1].updateC(b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.rotation() + 90);
	},
	
	draw(b){
		if(b.data == null) return;
		//var lengthC = b.data[0];
		/*if(b.owner != null){
			var valA = Math.max(b.owner.vel.len() * 19, b.fdata * 0.95);
			lengthC = Mathf.clamp(valA, 0, this.length);
		};*/
		
		//b.data[1].draw(Color.valueOf("f53036"), b.data[0]);
		
		var realLength = Damage.findLaserLength(b, b.data[0]);
		var fout = Mathf.clamp(b.time > b.lifetime - this.fadeTime ? 1 - (b.time - (this.lifetime - this.fadeTime)) / this.fadeTime : 1);
		var baseLen = realLength * fout;
		
		b.data[1].draw(Color.valueOf("f53036"), baseLen * 0.5);

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
jetstreamLaser.largeHit = false;
jetstreamLaser.damage = 35;
jetstreamLaser.lifetime = 15 * 60;
jetstreamLaser.length = 150;
jetstreamLaser.width = 5;
jetstreamLaser.incendChance = 0;
jetstreamLaser.hitEffect = coloredHitSmall;
jetstreamLaser.lightColor = Color.valueOf("f5303690");
jetstreamLaser.hitColor = Color.valueOf("f5303690");
jetstreamLaser.colors = [Color.valueOf("f5303690"), Color.valueOf("ff786e"), Color.white];
jetstreamLaser.strokes = [1.5, 1, 0.3];
jetstreamLaser.lenscales = [1 - offsetA, 1.12 - offsetA, 1.15 - offsetA, 1.17 - offsetA];

const jetstreamMissileWeap = new Weapon("unity-small-scar-weapon");
jetstreamMissileWeap.rotate = true;
jetstreamMissileWeap.x = 7.25;
jetstreamMissileWeap.y = -3.5;
jetstreamMissileWeap.reload = 50;
jetstreamMissileWeap.inaccuracy = 1.1;
jetstreamMissileWeap.shots = 6;
jetstreamMissileWeap.shotDelay = 4;
jetstreamMissileWeap.bullet = jetstreamMissile;

const jetstreamLaserWeap = new Weapon();
jetstreamLaserWeap.mirror = false;
jetstreamLaserWeap.x = 0;
jetstreamLaserWeap.y = 7;
jetstreamLaserWeap.minShootVelocity = 5;
jetstreamLaserWeap.continuous = true;
jetstreamLaserWeap.reload = (2.5 * 60) + jetstreamLaser.lifetime;
jetstreamLaserWeap.shootStatus = sEffect.reloadFatigue;
jetstreamLaserWeap.shootStatusDuration = jetstreamLaser.lifetime;
jetstreamLaserWeap.bullet = jetstreamLaser;
jetstreamLaserWeap.shootCone = 30;

const jetstream = extendContent(UnitType, "jetstream", {});
jetstream.constructor = () => extend(UnitEntity, {});
jetstream.weapons.add(jetstreamLaserWeap);
jetstream.weapons.add(jetstreamMissileWeap);
jetstream.health = 670;
jetstream.rotateSpeed = 12.5;
jetstream.faceTarget = true;
jetstream.flying = true;
jetstream.speed = 9.2;
jetstream.drag = 0.019;
jetstream.accel = 0.028;
jetstream.hitSize = 11;
jetstream.engineOffset = 11;