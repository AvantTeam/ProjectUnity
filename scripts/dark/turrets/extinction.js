const fLib = this.global.unity.funclib;
const sEffect = this.global.unity.status;
const soundLib = this.global.unity.sounds;
const exefLib = this.global.unity.extraeffects;
const tempVec = new Vec2();
const tempSeq = new Seq();
const tempColor = new Color();
const pow6In = new Interp.PowIn(6);

const evaporateDeath = new Effect(64, 800, e => {
	var oz = Draw.z();
	var unit = e.data[0];
	//var offsetx = (e.data[1]) * e.fin();
	//var offsety = (e.data[2]) * e.fin();
	//Draw.z(Layer.flyingUnit + 0.01);
	var curve = Interp.exp5In.apply(e.fin());
	tempColor.set(Color.black);
	tempColor.a = e.fout();
	Draw.color(tempColor);
	Draw.rect(unit.type.region, unit.x + (e.data[1].x * curve), unit.y + (e.data[1].y * curve), unit.rotation - 90);
	//fLib.simpleUnitDrawer(unit, false);
	//Draw.z(oz);
});
evaporateDeath.layer = Layer.flyingUnit + 0.01;

const extinctionLaser = extendContent(ContinuousLaserBulletType, 770, {
	update(b){
		this.super$update(b);
		var realLength = Damage.findLaserLength(b, this.length);
		
		for(var i = 0; i < 2; i++){
			if(Mathf.chanceDelta(0.7)){
				Lightning.create(b.team, Color.valueOf("ff9c5a"), 76, b.x, b.y, b.rotation(), Mathf.round((this.length / 8) + Mathf.random(2, 7)));
			};
		};

		for(var i = 0; i < 4; i++){
			if(Mathf.chanceDelta(0.8)){
				var lLength = Mathf.random(5, 12);
				Tmp.v2.trns(b.rotation(), Mathf.random(0, Math.max(realLength - lLength * 8, 4)));
				Lightning.create(b.team, Color.valueOf("ff9c5a"), 46, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.rotation(), Mathf.round(lLength));
			};
		};

		if(Mathf.chance(0.7)){
			Tmp.v2.trns(b.rotation(), Mathf.random(2.9, realLength));
			Damage.createIncend(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 9, 2);
		};
		
		if(b.timer.get(2, 15)){
			fLib.castCone(b.x, b.y, this.length * 0.8, b.rotation(), 70, (tile, build, dst, angD) => {
				//Damage.createIncend()
				if(Mathf.chance(angD * 0.2 * Mathf.clamp(dst * 1.7))) Fires.create(tile);
				if(build != null){
					if(b.team != build.team){
						build.damage(angD * 23.3 * Mathf.clamp(dst * 1.7));
						exefLib.addMoltenBlock(build);
					};
					//var size = build.block.size;
					//Puddles.deposit(tile, Liquids.slag, ((size * size) * 2) + 6);
					//tempSeq.add(build);
				};
			}, null);
			//tempSeq.each(build => build.remove());
		};
		fLib.castCone(b.x, b.y, this.length * 0.8, b.rotation(), 70, null, (e, dst, angD) => {
			if(!e.dead){
				var damageMulti = e.team != b.team ? 0.25 : 1;
				e.damage(28 * angD * dst * damageMulti);
				tempVec.trns(Angles.angle(b.x, b.y, e.x, e.y), angD * Mathf.clamp(dst * 1.7) * 160 * ((e.hitSize / 20) + (1 - (1 / 20))));
				e.impulse(tempVec.x, tempVec.y);
				if(Mathf.chanceDelta(Mathf.clamp(angD * dst * 12) * 0.9)) exefLib.createEvaporation(e.x, e.y, e, b.owner);
				//e.apply(sEffect.darkBurn, 10);
				e.apply(sEffect.radiation, angD * damageMulti * 3800.3 * Mathf.clamp(dst * 1.7));
				e.apply(StatusEffects.melting, angD * damageMulti * 240.3 * Mathf.clamp(dst * 1.7));
			}else{
				tempSeq.add(e);
				tempVec.trns(Angles.angle(b.x, b.y, e.x, e.y), (angD * Mathf.clamp(dst * 1.7) * 130) / Math.max((e.mass() / 120) + (1 - (1 / 120)), 1));
				tempVec.scl(12);
				//print(tempVec.len());
				//var x = tempVec.x;
				//var y = tempVec.y;
				evaporateDeath.at(e.x, e.y, 0, [e, tempVec.cpy()]);
				for(var i = 0; i < 12; i++){
					exefLib.createEvaporation(e.x, e.y, e, b.owner);
				};
			}
		});
		tempSeq.each(e => {
			//tempVec.trns(Angles.angle(b.x, b.y, e.x, e.y), 32 / e.mass());
			//var x = tempVec.x;
			//var y = tempVec.y;
			//evaporateDeath.at(e.x, e.y, 0, [e, x, y]);
			e.remove();
		});
		tempSeq.clear();
	},
	
	init(b){
		if(b == undefined) return;
		//wx, wy, range, angle, cone, consTile, consUnit
		fLib.castCone(b.x, b.y, this.length * 1.3, b.rotation(), 80, (tile, build, dst, angD) => {
			//Damage.createIncend()
			if(Mathf.chance(angD * 0.9 * Mathf.clamp(dst * 1.7))) Fires.create(tile);
			if(build != null){
				if(b.team != build.team) build.damage(angD * 258.3 * Mathf.clamp(dst * 1.7));
			};
		}, (e, dst, angD) => {
			var damageMulti = e.team != b.team ? 0.25 : 1;
			e.apply(StatusEffects.melting, angD * damageMulti * 1200.3 * Mathf.clamp(dst * 1.7));
		});
	},
	
	draw(b){
		var realLength = Damage.findLaserLength(b, this.length);
		var baseLen = realLength * b.fout();

		//Lines.lineAngle(b.x, b.y, b.rotation(), baseLen);
		for(var s = 0; s < this.colors.length; s++){
			Draw.color(Tmp.c1.set(this.colors[s]).mul(1 + Mathf.absin(Time.time(), 1, 0.1)));
			for(var i = 0; i < this.tscales.length; i++){
				Tmp.v1.trns(b.rotation() + 180, ((this.lenscales[i] * 2) - 2) * 35);
				Lines.stroke((9 + Mathf.absin(Time.time(), 0.8, 1.5)) * b.fout() * this.strokes[s] * this.tscales[i]);
				Lines.lineAngle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.rotation(), baseLen * this.lenscales[i], false);
			};
		};

		Tmp.v1.trns(b.rotation(), baseLen * 1.12);

		Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 90, Color.orange, 0.7);
		Draw.reset();
	}
});
extinctionLaser.length = 560;
extinctionLaser.strokes = [2 * 1.9, 1.5 * 1.9, 1 * 1.9, 0.3 * 1.9];

const extinction = extendContent(LaserTurret, "extinction", {
	load(){
		this.super$load();

		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
extinction.loopSound = soundLib.beamIntenseHighpitchTone;
extinction.shootType = extinctionLaser;
extinction.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.27 && liquid.flammability < 0.1, 2.9)).update(false);
extinction.heatDrawer = tile => {
	if(tile.heat <= 0.00001) return;
	var r = Interp.pow2Out.apply(tile.heat);
	var g = Interp.pow3In.apply(tile.heat) + ((1 - Interp.pow3In.apply(tile.heat)) * 0.12);
	var b = pow6In.apply(tile.heat);
	var a = Interp.pow2Out.apply(tile.heat);
	tempColor.set(r, g, b, a);
	Draw.color(tempColor);

	Draw.blend(Blending.additive);
	Draw.rect(extinction.heatRegion, tile.x + extinction.tr2.x, tile.y + extinction.tr2.y, tile.rotation - 90);
	Draw.blend();
	Draw.color();
};