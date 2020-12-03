const tempVec = new Vec2();

//colored effects region

const coloredSpark1L = new Effect(23, e => {
	tempVec.trns(e.rotation, e.fin() * 32, Mathf.randomSeedRange(Mathf.round((e.id * 322.563) + Time.time), 8) * e.fslope());
	tempVec.add(e.x, e.y);
	
	Draw.color(e.color);
	Fill.square(tempVec.x, tempVec.y, 3 * e.fout(), 45);
});

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

//end region
//imber faction region

const imberSparkCraftingEffect = new Effect(70, e => {
    Draw.color(Pal.surge, Color.white, e.finpow());
    
    Angles.randLenVectors(e.id, 3, (1 - e.finpow()) * 24, e.rotation, 360, (x, y) => {
        Fill.poly(e.x + x, e.y + y, 3, e.fslope() * 8, e.rotation);
    });
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
        let ang = Mathf.angle(x, y);
        Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1);
    }}));
});

const imberOrbShoot = new Effect(21, e => {
    Draw.color(Pal.surge);
    for(let i = 0; i < 2; i++){
        let l = Mathf.signs[i];
        Drawf.tri(e.x, e.y, 4 * e.fout(), 29, e.rotation + 67 * l);
    };
});

const imberOrbTrail = new Effect(43, e => {
    let originalZ = Draw.z();
	
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
    Fill.circle(e.x, e.y, e.fin() * 6);

    Draw.color();
    Fill.circle(e.x, e.y, e.fin() * 3);
});

const imberShieldBreakFx = new Effect(5, e => {
    Draw.z(Layer.shields);
    Draw.color(e.color);
    let radius = e.data * e.fout();

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
    Lines.square(e.x, e.y, e.fin() * 75, 45);
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

//end region
//scar faction region

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

//end region
//advance faction region

const advanceA = Pal.lancerLaser;
const advanceB = Color.valueOf("4f72e1");

const hitAdvanceFlameL = new Effect(15, e => {
	Draw.color(advanceA, advanceB, e.fin());
	
	Angles.randLenVectors(e.id, 2, e.finpow() * 17.0, e.rotation, 60.0, (x, y) => {
        Fill.poly(e.x + x, e.y + y, 6, 3 + e.fout() * 3, e.rotation);
    });
});

const advanceFlameTrailL = new Effect(27, e => {
	Draw.color(advanceA, advanceB, e.fin());
	
	Fill.poly(e.x, e.y, 6, e.fout() * 4.1, e.rotation + e.fin() * 270);
});

const advanceFlameSmokeL = new Effect(13, e => {
	Draw.color(Color.valueOf("4d668f77"), Color.valueOf("35455f00"), e.fin());
	
	//Fill.poly(e.x, e.y, 6, e.fout() * 7.1, e.rotation + e.fin() * 270);
	
	Angles.randLenVectors(e.id, 2, e.finpow() * 13.0, e.rotation, 60.0, (x, y) => {
        Fill.poly(e.x + x, e.y + y, 6, e.fout() * 4.1, e.rotation + e.fin() * 270);
    });
});

//end region

module.exports = {
	coloredSpark1: coloredSpark1L,
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
	scarRailHit: scarRailHitL,
    
    hitAdvanceFlame: hitAdvanceFlameL,
    advanceFlameTrail: advanceFlameTrailL,
    advanceFlameSmoke: advanceFlameSmokeL
};