//Colored Effects
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
//End Region
//Scar Effects
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
//End Region

module.exports = {
	scarRailShoot: scarRailShootL,
	scarRailTrail: scarRailTrailL,
	scarRailHit: scarRailHitL,
	
	coloredRailShoot: coloredRailShootL,
	coloredRailTrail: coloredRailTrailL,
	coloredRailHit: coloredRailHitL,
	coloredHitSmall: coloredHitSmallL
};