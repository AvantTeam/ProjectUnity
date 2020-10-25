const tmpCol = new Color();

const pow6In = new Interp.PowIn(6);

const calamityLaser = extendContent(ContinuousLaserBulletType, 580, {
	update(b){
		this.super$update(b);
		var realLength = Damage.findLaserLength(b, realLength);
		if(Mathf.chanceDelta(0.5)){
			Lightning.create(b.team, Color.valueOf("ff9c5a"), 34, b.x, b.y, b.rotation(), Mathf.round((this.length / 8) + Mathf.random(2, 7)));
		};

		for(var i = 0; i < 3; i++){
			if(Mathf.chanceDelta(0.8)){
				var lLength = Mathf.random(5, 12);
				Tmp.v2.trns(b.rotation(), Mathf.random(0, Math.max(realLength - lLength * 8, 4)));
				Lightning.create(b.team, Color.valueOf("ff9c5a"), 32, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.rotation(), Mathf.round(lLength));
			};
		};

		if(Mathf.chance(0.6)){
			Tmp.v2.trns(b.rotation(), Mathf.random(2.9, realLength));
			Damage.createIncend(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 9, 2);
		}
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

		Drawf.light(b.team, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, 70, Color.orange, 0.7);
		Draw.reset();
	}
});
calamityLaser.length = 450;
calamityLaser.strokes = [2 * 1.7, 1.5 * 1.7, 1 * 1.7, 0.3 * 1.7];

//TODO: research from catastrophe.
const calamity = extendContent(LaserTurret, "calamity", {
	load(){
		this.super$load();

		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
calamity.shootType = calamityLaser;
calamity.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.3 && liquid.flammability < 0.1, 2.1)).update(false);
calamity.heatDrawer = tile => {
	if(tile.heat <= 0.00001) return;
	var r = Interp.pow2Out.apply(tile.heat);
	var g = Interp.pow3In.apply(tile.heat) + ((1 - Interp.pow3In.apply(tile.heat)) * 0.12);
	var b = pow6In.apply(tile.heat);
	var a = Interp.pow2Out.apply(tile.heat);
	tmpCol.set(r, g, b, a);
	Draw.color(tmpCol);

	Draw.blend(Blending.additive);
	Draw.rect(calamity.heatRegion, tile.x + calamity.tr2.x, tile.y + calamity.tr2.y, tile.rotation - 90);
	Draw.blend();
	Draw.color();
};
