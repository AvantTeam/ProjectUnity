const tmpCol = new Color();

const pow6In = new Interp.PowIn(6);

const catastropheLaser = extendContent(ContinuousLaserBulletType, 140, {
	update(b){
		this.super$update(b);
		
		if(Mathf.chanceDelta(0.4)){
			Lightning.create(b.team, Color.valueOf("ff9c5a"), 23, b.x, b.y, b.rotation(), Mathf.round((this.length / 8) + Mathf.random(2, 7)));
		};
		
		if(Mathf.chanceDelta(0.9)){
			var lLength = Mathf.random(4, 9);
			Tmp.v2.trns(b.rotation(), Mathf.random(0, this.length - (lLength * 8)));
			Lightning.create(b.team, Color.valueOf("ff9c5a"), 23, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.rotation(), Mathf.round(lLength));
		};
		
		if(Mathf.chance(0.4)){
			Tmp.v2.trns(b.rotation(), Mathf.random(2.9, this.length));
			Damage.createIncend(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 7, 2);
		}
	}
});
catastropheLaser.length = 340;
catastropheLaser.strokes = [2 * 1.4, 1.5 * 1.4, 1 * 1.4, 0.3 * 1.4];

//TODO: research from dark alloy item.
const catastrophe = extendContent(LaserTurret, "catastrophe", {
	load(){
		this.super$load();
		
		this.baseRegion = Core.atlas.find("unity-block-" + this.size);
	}
});
catastrophe.shootType = catastropheLaser;
catastrophe.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.4 && liquid.flammability < 0.1, 1.3)).update(false);
catastrophe.heatDrawer = tile => {
	if(tile.heat <= 0.00001) return;
	var r = Interp.pow2Out.apply(tile.heat);
	var g = Interp.pow3In.apply(tile.heat) + ((1 - Interp.pow3In.apply(tile.heat)) * 0.12);
	var b = pow6In.apply(tile.heat);
	var a = Interp.pow2Out.apply(tile.heat);
	tmpCol.set(r, g, b, a);
	Draw.color(tmpCol);
	
	Draw.blend(Blending.additive);
	Draw.rect(catastrophe.heatRegion, tile.x + catastrophe.tr2.x, tile.y + catastrophe.tr2.y, tile.rotation - 90);
	Draw.blend();
	Draw.color();
};