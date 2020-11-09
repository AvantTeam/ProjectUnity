const tempVec = new Vec2();

const consumeInterval = 5;

const healLaser = new Effect(60, e => {
	if(e.data == null) return;
	const reduction = [0, 1.5];
	var a = e.data[0];
	var b = e.data[1];
	
	for(var i = 0; i < 2; i++){
		Draw.color(i == 0 ? Pal.heal : Color.white);
		Lines.stroke((3 - reduction[i]) * e.fout());
		Lines.line(a.x, a.y, b.x, b.y);
		
		Fill.circle(a.x, a.y, (2.5 - reduction[i]) * e.fout());
		Fill.circle(b.x, b.y, (2.5 - reduction[i]) * e.fout());
	};
});

const angel = extendContent(UnitType, "angel", {});
angel.health = 90;
angel.engineOffset = 9.7;
angel.flying = true;
angel.speed = 4.3;
angel.accel = 0.08;
angel.drag = 0.01;
angel.range = 80;
angel.commandLimit = 0;
angel.ammoType = AmmoTypes.power;
angel.constructor = () => {
	return extend(UnitEntity, {});
};
angel.defaultController = () => {
	var ai = extend(FlyingAI, {
		invalid(target){
			return target == null || target.team != this.unit.team || target.health >= target.maxHealth || target.dead;
		},
		updateMovement(){
			if(this.target != null && this.target instanceof Unit){
				//this.unit.lookAt(this.target);
				tempVec.trns(this.unit.angleTo(this.target) + 180, this.unit.type.range + this.target.hitSize);
				tempVec.add(this.target.x, this.target.y);
				tempVec.sub(this.unit.x, this.unit.y);
				tempVec.scl(1 / 100);
				tempVec.limit(1);
				tempVec.scl(this.unit.realSpeed());
				//tempVec.setLength(tempVec.len() * this.unit.realSpeed());
				
				this.unit.moveAt(tempVec);
				this.unit.lookAt(this.target);
			};
		},
		updateWeapons(){
			if(this.target != null && (this.unit.ammo > 0.0001 || !Vars.state.rules.unitAmmo) && this.target instanceof Unit){
				if(this.timer.get(3, consumeInterval) && this.unit.within(this.target, this.unit.type.range + this.target.hitSize + 10)){
					if(Vars.state.rules.unitAmmo) this.unit.ammo--;
					healLaser.at(this.unit.x, this.unit.y, 0, [this.unit, this.target]);
					this.target.heal(10);
				};
			};
		},
		updateTargeting(){
			//if(this.target != null) return;
			if(this.retarget()){
				var score = 0;
				var tmp = null;
				
				Groups.unit.each(x => x.team == this.unit.team, e => {
					var scoreB = ((1 - (e.health / e.maxHealth)) * 200) + ((1000000 - this.unit.dst(e)) / 500);
					if(scoreB > score && e.health < e.maxHealth - 0.0001 && e != this.unit && !e.dead){
						score = scoreB;
						tmp = e;
					};
				});
				
				this.target = tmp;
			};
			this.updateWeapons();
		}
	});
	return ai;
};