const fLib = this.global.unity.funclib;

const tempVec = new Vec2();
const tempVec2 = new Vec2();
const timeProgress = 1.2;
const lightningLength = 23;
const effectLength = 32;
const tempCol = new Color();

const collidedPlast = (a, b, team) => {
	//Tmp.v1.trns(b.rotation(), length);

	var furthest = null;

	//var found = world.raycast(b.tileX(), b.tileY(), World.toTile(b.x + Tmp.v1.x), World.toTile(b.y + Tmp.v1.y),
	//(x, y) => (furthest = world.tile(x, y)) != null && furthest.team() != b.team && furthest.block().absorbLasers);
	var found = Vars.world.raycast(World.toTile(a.x), World.toTile(a.y), World.toTile(b.x), World.toTile(b.y), (x, y) => {
		furthest = Vars.world.tile(x, y);
		//if(furthest != null) print(": " + furthest.team + " : " + team);
		return furthest != null && furthest.team() != team && furthest.block().absorbLasers;
	});
	//if(found && furthest != null) node.score = node.range + 1;
	//return found && furthest != null ? Math.max(6, a.dst(furthest.worldx(), furthest.worldy())) : b.dst(a);
	return found && furthest != null;
	//return found && furthest != null ? Math.max(6, b.dst(furthest.worldx(), furthest.worldy())) : length;
};

const vaporation = new Effect(23, e => {
	if(e.data == null) return;
	tempVec2.set(e.data[0]);
	tempVec2.lerp(e.data[1], e.fin());
	
	//Draw.z(Layer.flyingUnit + 0.012);
	Draw.color(Pal.darkFlame, Pal.darkerGray, e.fin());
	Fill.circle(tempVec2.x + e.data[2].x, tempVec2.y + e.data[2].y, e.fout() * 5);
});
vaporation.layer = Layer.flyingUnit + 0.012;

function LightningNode(x, y, xa, ya){
	this.damage = 0;
	this.fromPos = new Vec2(x, y);
	this.toPos = new Vec2(xa, ya);
	this.team = Team.derelict;
	this.colorFrom = Pal.lancerLaser;
	this.colorTo = Color.white;
	this.rotation = Angles.angle(x, y, xa, ya);
	this.origin = null;
	this.timerC = 0;
	//this.effectC = 16;
	this.effectC = 0;
	this.score = 0;
	//this.range = 300;
	this.altTime = 0;
	this.setC = (origin, team, damage, colorF, colorT, score) => {
		this.damage = damage;
		//this.rotation = rotation;
		this.colorFrom = colorF;
		this.colorTo = colorT;
		this.team = team;
		this.score = score;
		this.origin = origin;
		this.origin.addNode(this);
		//this.timerC = time;
		//this.range = range;
		this.init();
	};
	this.draw = () => {
		Draw.color(this.colorFrom, this.colorTo, this.effectC / effectLength);
		Lines.stroke(Mathf.clamp(this.origin.fout() * 5) * 2);
		tempVec2.set(this.fromPos);
		tempVec2.lerp(this.toPos, this.altTime / timeProgress);
		Lines.line(this.fromPos.x, this.fromPos.y, tempVec2.x, tempVec2.y);
		//Draw.reset();
	};
	this.update = () => {
		if(this.effectC < effectLength){
			this.effectC += Time.delta;
			this.effectC = Mathf.clamp(this.effectC, 0, effectLength);
		};
		if(this.timerC >= timeProgress){
			var chance = Mathf.chance(0.035) ? 2 : 1;
			for(var i = 0; i < chance; i++){
				var rand = chance == 2 ? Mathf.range(60) : Mathf.range(20);
				tempVec.trns(this.rotation + rand, lightningLength);
				tempVec.add(this.toPos);
				
				//collidedPlast(this, this.toPos, tempVec, this.team);
				//print(this.team);
				var collided = collidedPlast(this.toPos, tempVec, this.team);
				if(collided) this.score = this.range + 1;
				//print(collided);
				
				if(this.score < this.origin.getRange()){
					var inf = this.origin.getInfluence();
					var rotationC = (inf != null && !this.origin.intersected()) ? Angles.moveToward(this.rotation + rand, Angles.angle(this.toPos.x, this.toPos.y, inf.x, inf.y) + (rand / 1.12), 17 * (Mathf.clamp((600 - Mathf.dst(this.toPos.x, this.toPos.y, inf.x, inf.y)) / 600))) : this.rotation + rand;
					tempVec.trns(rotationC, lightningLength);
					var nScore = tempVec.len();
					nScore += this.score;
					tempVec.add(this.toPos);
					if(inf != null && inf.within(tempVec, 43) && Angles.within(this.toPos.angleTo(tempVec), tempVec.angleTo(inf), 90)) this.origin.setIntersected();
					
					var l = new LightningNode(this.toPos.x, this.toPos.y, tempVec.x, tempVec.y);
					l.setC(this.origin, this.team, this.damage, this.colorFrom, this.colorTo, nScore, this.range, 0);
				};
			};
			this.timerC = -2;
		}else if(this.timerC > -1){
			this.altTime = Math.min(Time.delta + this.altTime, timeProgress);
			this.timerC += Time.delta;
		};
	};
	this.init = () => {
		Damage.damage(this.team, this.toPos.x, this.toPos.y, lightningLength * 1.5, this.damage);
	};
};

const vapourize = prov(() => {
	var b = extend(EffectState, {
		setCC(){
			//this._altLifetime = 30;
			//this._host = null;
			this.lifetime = 40;
			this._influence = null;
		},
		setCCC(x, y, parent, influence){
			this.x = x;
			this.y = y;
			this.parent = parent;
			this._influence = influence;
		},
		add(){
			if(this.added == true) return; 
			//this.super$add();
			Groups.all.add(this);
			Groups.draw.add(this);
			this.added = true;
		},
		update(){
			//this._nodes.each(e => e.update());
			if(Mathf.chanceDelta(0.2 * (1 - this.fin()) * (this.parent.type.hitSize / 10))){
				tempVec.trns(Angles.angle(this.x, this.y, this._influence.x, this._influence.y) + 180, 65 + Mathf.range(0.3));
				tempVec.add(this.parent);
				tempVec2.trns(Mathf.random(360), Mathf.random(this.parent.type.hitSize / 1.25));
				vaporation.at(this.parent.x, this.parent.y, 0, [this.parent, tempVec.cpy(), tempVec2.cpy()]);
			};
			this.super$update();
		},
		clipSize(){
			return this.parent.hitSize * 2;
		},
		draw(){
			var oz = Draw.z();
			var slope = (0.5 - Math.abs(this.fin() - 0.5)) * 2;
			Draw.z(Layer.flyingUnit + 0.01);
			tempCol.set(Color.black);
			tempCol.a = slope * 0.25;
			Draw.color(tempCol);
			fLib.simpleUnitDrawer(this.parent, false);
			Draw.z(oz);
		},
		remove(){
			if (!this.added) return; 
			Groups.all.remove(this);
			Groups.draw.remove(this);
			this.added = false;
		}
	});
	b.setCC();
	return b;
});

const customLightningA = prov(() => {
	var a = extend(EffectState, {
		setCC(){
			this._nodes = new Seq();
			this._colorF = Pal.lancerLaser;
			this._colorT = Color.white;
			this._team = Team.derelict;
			this._damage = 12;
			this._range = 120;
			this._hasIntersected = false;
			this._influence = null;
		},
		setIntersected(){
			this._hasIntersected = true;
		},
		intersected(){
			return this._hasIntersected;
		},
		clipSize(){
			return (this._range * 2) + 12;
		},
		getInfluence(){
			return this._influence;
		},
		getRange(){
			return this._range;
		},
		setCCC(x, y, rotation, lifetime, colorF, colorT, team, damage, range, influence){
			this.x = x;
			this.y = y;
			this.rotation = rotation;
			this.lifetime = lifetime;
			this._team = team;
			this._damage = damage;
			this._range = range;
			this._colorF = colorF;
			this._colorT = colorT;
			this._influence = influence;
		},
		addNode(node){
			this._nodes.add(node);
		},
		update(){
			this._nodes.each(e => e.update());
			this.super$update();
		},
		draw(){
			var z = Draw.z();
			Draw.z(Layer.effect - 1);
			this._nodes.each(e => e.draw());
			Draw.reset();
			Draw.z(z);
		},
		add(){
			if(this.added == true) return; 
			this.super$add();
			tempVec.trns(this.rotation, lightningLength);
			tempVec.add(this.x, this.y);
			var l = new LightningNode(this.x, this.y, tempVec.x, tempVec.y);
			l.setC(this, this._team, this._damage, this._colorF, this._colorT, 0);
		},
		remove(){
			if (!this.added) return; 
			Groups.all.remove(this);
			Groups.draw.remove(this);
			this.added = false;
			this._nodes.each(n => {
				n.toPos = null;
				n.fromPos = null;
			});
			this._nodes.clear();
		}
	});
	a.setCC();
	return a;
});

module.exports = {
	createEvaporation(x, y, host, influence){
		if(host == null || influence == null) return;
		var l = vapourize.get();
		l.setCCC(x, y, host, influence);
		l.add();
	},
	createLightning(x, y, rotation, lifetime, colorF, colorT, team, damage, range, influence){
		var l = customLightningA.get();
		l.setCCC(x, y, rotation, lifetime, colorF, colorT, team, damage, range, influence);
		l.add();
	}
};