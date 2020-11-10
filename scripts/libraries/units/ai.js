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

const tempVec = new Vec2();

const consumeInterval = 5;

const unitHealerAIL = prov(() => {
	var u = extend(FlyingAI, {
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
					if(typeof(this.unit.type.healStrength) == "function"){
						this.target.heal(this.unit.type.healStrength());
					}else{
						this.target.heal(10);
					}
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
	return u;
});

const distanceGroundAIL = prov(() => {
	var u = extend(GroundAI, {
		setEffectsC(){
			this._lockTarget = false;
			this._lockTimer = 60;
		},
		updateMovement(){
			var core = this.unit.closestEnemyCore();
			
			if(core != null && this.unit.within(core, this.unit.range() / 1.1 + core.block.size * Vars.tilesize / 2)){
				this.target = core;
				for(var i = 0; i < this.targets.length; i++){
					this.targets[i] = core;
				};
				//Arrays.fill(targets, core);
			};
			
			if(this.target != null && this.target.team != this.unit.team && this.unit.within(this.target, this.unit.range() / 1.7)){
				this._lockTarget = true;
				this._lockTimer = 0;
			};
			
			if(this._lockTimer >= 60){
				this._lockTarget = false;
			}else{
				this._lockTimer += Time.delta;
			};
			
			if((core == null || !this.unit.within(core, this.unit.range() * 0.5)) && this.command() == UnitCommand.attack && !this._lockTarget){
				var move = true;

				if(Vars.state.rules.waves && this.unit.team == Vars.state.rules.defaultTeam){
					var spawner = this.getClosestSpawner();
					if(spawner != null && this.unit.within(spawner, Vars.state.rules.dropZoneRadius + 120)) move = false;
				};

				if(move) this.pathfind(Pathfinder.fieldCore);
			};
			
			if(this.command() == UnitCommand.rally){
				var targetR = this.targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);

				if(targetR != null && !this.unit.within(targetR, 70)){
					this.pathfind(Pathfinder.fieldRally);
				};
			};
			
			if(!Units.invalidateTarget(this.target, this.unit, this.unit.range()) && this.unit.type.rotateShooting){
				if(this.unit.type.hasWeapons()){
					this.unit.lookAt(Predict.intercept(this.unit, this.target, this.unit.type.weapons.first().bullet.speed));
				};
			}else if(this.unit.moving()){
				this.unit.lookAt(this.unit.vel.angle());
			};
			
			if(this._lockTarget && this.target != null && this.target.team != this.unit.team && this.command() != UnitCommand.rally){
				if(this.unit.within(this.target, this.unit.range() / 1.72)) this.unit.moveAt(tempVec.trns(this.unit.angleTo(this.target) + 180, this.unit.realSpeed()));
			};
		}
	});
	u.setEffectsC();
	return u;
});

module.exports = {
	unitHealerAI: unitHealerAIL,
	distanceGroundAI: distanceGroundAIL
};