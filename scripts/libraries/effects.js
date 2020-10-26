//Colored region

const coloredRailShootL = new Effect(24, e => {
	e.scaled(10, b => {
		Draw.color(Color.white, Color.lightGray, b.fin());
		Lines.stroke(b.fout() * 3 + 0.2);
		Lines.circle(b.x, b.y, b.fin() * 50);
	});
	
	Draw.color(e.color);
	
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		
		Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + (90 * sign));
	};
});

const coloredRailTrailL = new Effect(16, e => {
	Draw.color(e.color);
	
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		
		Drawf.tri(e.x, e.y, 10 * e.fout(), 24, e.rotation + 90 + (90 * sign));
	};
});

const coloredRailHitL = new Effect(18, e => {
	Draw.color(e.color);
	
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		
		Drawf.tri(e.x, e.y, 10 * e.fout(), 60, e.rotation + 90 + (90 * sign));
	};
});

const coloredHitSmallL = new Effect(14, e => {
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

//End region
//Imber region

const imberSparkCraftingEffect = new Effect(70, e => {
	Draw.color(Color.valueOf("fff566"), Color.valueOf("ffc266"), e.finpow());
	Draw.alpha(e.finpow());
	Angles.randLenVectors(e.id, 3, (1 - e.finpow()) * 24, e.rotation, 360, new Floatc2({get(x, y){
		Drawf.tri(e.x + x, e.y + y, e.fout() * 8, e.fout() * 10, e.rotation);
		Drawf.tri(e.x + x, e.y + y, e.fout() * 4, e.fout() * 6, e.rotation);
	}}));
	Draw.color();
});

const imberCircleSparkCraftingEffect = new Effect(30, e => {
	Draw.color(Pal.surge);

	Lines.stroke(e.fslope());
	Lines.circle(e.x, e.y, e.fin() * 20);
});

const imberOrbHit = new Effect(12, e => {
	Draw.color(Pal.surge);
	Lines.stroke(e.fout() * 1.5);
	Angles.randLenVectors(e.id, 8, e.finpow() * 17, e.rotation, 360, new Floatc2({get(x, y){
		var ang = Mathf.angle(x, y);
		Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1);
	}}));
});

const imberOrbShoot = new Effect(21, e => {
	Draw.color(Pal.surge);
	for(var i = 0; i < 2; i++){
		var l = Mathf.signs[i];
		Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 67 * l);
	};
});

const imberOrbTrail = new Effect(43, e => {
	var originalZ = Draw.z();
	
	Tmp.v1.trns(Mathf.randomSeed(e.id) * 360, Mathf.randomSeed(e.id * 341) * 12 * e.fin());

	Draw.z(Layer.bullet - 0.01);
    Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7 * e.fout() + 3, Pal.surge, 0.6);

	Draw.color(Pal.surge);
	Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7);

	Draw.z(originalZ);
});

const imberOrbShootSmoke = new Effect(26, e => {
	Draw.color(Pal.surge);
	Angles.randLenVectors(e.id, 7, 80, e.rotation, 0, new Floatc2({get(x, y){
		Fill.circle(e.x + x, e.y + y, e.fout() * 4);
	}}));
});

const imberOrbCharge = new Effect(38, e => {
	Draw.color(Pal.surge);
	Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({get(x, y){
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
	}}));
});

const imberOrbChargeBegin = new Effect(71, e => {
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fin() * 3);

	Draw.color();
	Fill.circle(e.x, e.y, e.fin() * 2);
});

const imberCurrentCharge = new Effect(32, e => {
	Draw.color(Pal.surge, Color.white, e.fin());

	Angles.randLenVectors(e.id, 8, 420 + Mathf.random(24, 28) * e.fout(), e.rotation, 4, new Floatc2({get(x, y){
		Lines.stroke(0.3 + e.fout() * 2);
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14 + 0.5);
	}}));
	
	Lines.stroke(e.fin() * 1.5);
	Lines.circle(e.x, e.y, e.fout() * 60);
});

const imberCurrentChargeBegin = new Effect(260, e => {
	Draw.color(Pal.surge);
	Fill.circle(e.x, e.y, e.fin() * 7);

	Draw.color();
	Fill.circle(e.x, e.y, e.fin() * 3);
});

const imberShieldBreakFx = new Effect(5, e => {
	Draw.z(Layer.shields);
	Draw.color(e.color);
	var radius = e.data * e.fout();

	if(Core.settings.getBool("animatedshields")){
		Fill.poly(e.x, e.y, 6, radius);
	}else{
		Lines.stroke(1.5);
		Draw.alpha(0.09);
		Fill.poly(e.x, e.y, 6, radius);
		Draw.alpha(1);
		Lines.poly(e.x, e.y, 6, radius);
		Draw.reset();
	}
	Draw.z(Layer.block);

	Draw.color();
});

const imberChargeTriangles = new Effect(96, e => {
	Draw.color(Pal.surge);
	
	Angles.randLenVectors(e.id, 5, (1 - e.finpow()) * 24, e.rotation, 360, new Floatc2({get(x, y){
		Drawf.tri(e.x + x, e.y + y, e.fout() * 10, e.fout() * 11, e.rotation);
		Drawf.tri(e.x + x, e.y + y, e.fout() * 8, e.fout() * 9, e.rotation);
	}}));
});

const imberChargeBeginTriangles = new Effect(250, e => {
	Draw.color(Pal.surge);
	
	Drawf.tri(e.x, e.y, e.fin() * 16, e.fin() * 20, e.rotation);
});

const imberShootTriangle = new Effect(36, e => {
	Draw.color(Pal.surge, Color.white, e.fin());
	
	Angles.randLenVectors(e.id, 8, e.fin() * 20 + 1, e.rotation, 40, new Floatc2({get(x, y){
		Drawf.tri(e.x + x, e.y + y, e.fout() * 14, e.fout() * 15, e.rotation);
		Drawf.tri(e.x + x, e.y + y, e.fout() * 8, e.fout() * 9, e.rotation);
	}}));
	
	Angles.randLenVectors(e.id, 4, e.fin() * 20 + 1, e.rotation, 40, new Floatc2({get(x, y){
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 18 + 3);
	}}));
});

const imberTriangleHit = new Effect(30, e => {
	Draw.color(Pal.surge);
	
	Lines.stroke(e.fout() * 2.8);
	Lines.circle(e.x, e.y, e.fout() * 50);
});

const imberPlasmaFragAppear = new Effect(12, e => {
	Draw.z(Layer.bullet - 0.01);
	
	Draw.color(Color.white);
	Drawf.tri(e.x, e.y, e.fin() * 12, e.fin() * 13, e.rotation);
	
	Draw.z();
});

const imberPlasmaFragDisappear = new Effect(12, e => {
	Draw.z(Layer.bullet - 0.01);
	
	Draw.color(Pal.surge, Color.white, e.fin());
	Drawf.tri(e.x, e.y, e.fout() * 10, e.fout() * 11, e.rotation);
	
	Draw.z();
});

//End region
//Scar region

const scarRailShootL = new Effect(24, e => {
	e.scaled(10, b => {
		Draw.color(Color.white, Color.lightGray, b.fin());
		Lines.stroke(b.fout() * 3 + 0.2);
		Lines.circle(b.x, b.y, b.fin() * 50);
	});
	
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		Draw.color(Color.valueOf("f53036"));
		Drawf.tri(e.x, e.y, 13 * e.fout(), 85, e.rotation + (90 * sign));
		
		Draw.color(Color.white);
		Drawf.tri(e.x, e.y, Math.max((13 * e.fout()) - 4, 0), 81, e.rotation + (90 * sign));
	};
});

const scarRailTrailL = new Effect(16, e => {
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		Draw.color(Color.valueOf("f53036"));
		Drawf.tri(e.x, e.y, 10 * e.fout(), 24, e.rotation + 90 + (90 * sign));
		
		Draw.color(Color.white);
		Drawf.tri(e.x, e.y, Math.max((10 * e.fout()) - 4, 0), 20, e.rotation + 90 + (90 * sign));
	};
});

const scarRailHitL = new Effect(18, e => {
	for(var i = 0; i < 2; i++){
		var sign = Mathf.signs[i];
		Draw.color(Color.valueOf("f53036"));
		Drawf.tri(e.x, e.y, 10 * e.fout(), 60, e.rotation + 90 + (90 * sign));
		
		Draw.color(Color.white);
		Drawf.tri(e.x, e.y, Math.max((10 * e.fout()) - 4, 0), 56, e.rotation + 90 + (90 * sign));
	};
});

//End region

module.exports = {
    coloredRailShoot: coloredRailShootL,
	coloredRailTrail: coloredRailTrailL,
	coloredRailHit: coloredRailHitL,
	coloredHitSmall: coloredHitSmallL,
	
	imberSparkCraftingEffect: imberSparkCraftingEffect,
	imberCircleSparkCraftingEffect: imberCircleSparkCraftingEffect,
	imberOrbHit: imberOrbHit,
	imberOrbShoot: imberOrbShoot,
	imberOrbTrail: imberOrbTrail,
	imberOrbShootSmoke: imberOrbShootSmoke,
	imberOrbCharge: imberOrbCharge,
	imberOrbChargeBegin: imberOrbChargeBegin,
	imberCurrentCharge: imberCurrentCharge,
	imberCurrentChargeBegin: imberCurrentChargeBegin,
	imberShieldBreakFx: imberShieldBreakFx,
	imberChargeTriangles: imberChargeTriangles,
	imberChargeBeginTriangles: imberChargeBeginTriangles,
	imberShootTriangle: imberShootTriangle,
	imberTriangleHit: imberTriangleHit,
	imberPlasmaFragAppear: imberPlasmaFragAppear,
	imberPlasmaFragDisappear: imberPlasmaFragDisappear,
	
	scarRailShoot: scarRailShootL,
	scarRailTrail: scarRailTrailL,
	scarRailHit: scarRailHitL
};