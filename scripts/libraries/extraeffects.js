const tempVec = new Vec2();
const tempVec2 = new Vec2();
const timeProgress = 2;

const collidedPlast = (a, b, team) => {
	//Tmp.v1.trns(b.rotation(), length);

	var furthest = null;

	//var found = world.raycast(b.tileX(), b.tileY(), World.toTile(b.x + Tmp.v1.x), World.toTile(b.y + Tmp.v1.y),
	//(x, y) => (furthest = world.tile(x, y)) != null && furthest.team() != b.team && furthest.block().absorbLasers);
	var found = Vars.world.raycast(World.toTile(a.x), World.toTile(a.y), World.toTile(b.x), World.toTile(b.y), (x, y) => {
		furthest = Vars.world.tile(x, y);
		return furthest != null && furthest.team != team && furthest.block().absorbLasers;
	});
	//if(found && furthest != null) node.score = node.range + 1;
	//return found && furthest != null ? Math.max(6, a.dst(furthest.worldx(), furthest.worldy())) : b.dst(a);
	return found && furthest != null;
	//return found && furthest != null ? Math.max(6, b.dst(furthest.worldx(), furthest.worldy())) : length;
};

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
	this.range = 300;
	this.altTime = 0;
	this.setC = (origin, team, damage, colorF, colorT, score, range) => {
		this.damage = damage;
		//this.rotation = rotation;
		this.colorFrom = colorF;
		this.colorTo = colorT;
		this.team = team;
		this.score = score;
		this.origin = origin;
		this.origin.addNode(this);
		//this.timerC = time;
		this.range = range;
		this.init();
	};
	this.draw = () => {
		Draw.color(this.colorFrom, this.colorTo, this.effectC / 16);
		Lines.stroke(Mathf.clamp(this.origin.fout() * 20) * 2);
		tempVec2.set(this.fromPos);
		tempVec2.lerp(this.toPos, this.altTime / timeProgress);
		Lines.line(this.fromPos.x, this.fromPos.y, tempVec2.x, tempVec2.y);
		//Draw.reset();
	};
	this.update = () => {
		if(this.effectC < 16){
			this.effectC += Time.delta;
			this.effectC = Mathf.clamp(this.effectC, 0, 16);
		};
		if(this.timerC >= timeProgress){
			var chance = Mathf.chance(0.03) ? 2 : 1;
			for(var i = 0; i < chance; i++){
				var rand = chance == 2 ? Mathf.range(50) : Mathf.range(15);
				tempVec.trns(this.rotation + rand, 17);
				tempVec.add(this.toPos);
				
				//collidedPlast(this, this.toPos, tempVec, this.team);
				var collided = collidedPlast(this.toPos, tempVec, this.team);
				if(collided) this.score = this.range + 1;
				//print(collided);
				
				if(this.score < this.range){
					tempVec.trns(this.rotation + rand, 17);
					var nScore = tempVec.len();
					nScore += this.score;
					tempVec.add(this.toPos);
					
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
		Damage.damage(this.team, this.toPos.x, this.toPos.y, 24, this.damage);
	};
};

const customLightningA = prov(() => {
	var a = extend(EffectState, {
		setCC(){
			this._nodes = new Seq();
			this._colorF = Pal.lancerLaser;
			this._colorT = Color.white;
			this._team = Team.derelict;
			this._damage = 12;
			this._range = 120;
		},
		clipSize(){
			return (this._range * 2) + 12;
		},
		setCCC(x, y, rotation, lifetime, colorF, colorT, team, damage, range){
			this.x = x;
			this.y = y;
			this.rotation = rotation;
			this.lifetime = lifetime;
			this._team = team;
			this._damage = damage;
			this._range = range;
			this._colorF = colorF;
			this._colorT = colorT;
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
			tempVec.trns(this.rotation, 17);
			tempVec.add(this.x, this.y);
			var l = new LightningNode(this.x, this.y, tempVec.x, tempVec.y);
			l.setC(this, this._team, this._damage, this._colorF, this._colorT, 0, this._range);
		},
		remove(){
			if (!this.added) return; 
			Groups.all.remove(this);
			Groups.draw.remove(this);
			this.added = false;
			this._nodes.clear();
		}
	});
	a.setCC();
	return a;
});

module.exports = {
	createLightning(x, y, rotation, lifetime, colorF, colorT, team, damage, range){
		var l = customLightningA.get();
		l.setCCC(x, y, rotation, lifetime, colorF, colorT, team, damage, range);
		l.add();
	}
};