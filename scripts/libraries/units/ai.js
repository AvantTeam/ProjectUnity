const tempVec = new Vec2();

const distanceGroundAIL = prov(() => {
	var u = extend(GroundAI, {
		setEffectsC(){
			this._lockTarget = false;
			this._lockTimer = 60;
		},
		updateMovement(){
			var core = this.unit.closestEnemyCore();
			
			if(core != null && unit.within(core, this.unit.range() / 1.1 + core.block.size * Vars.tilesize / 2)){
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
				var targetR = targetFlag(this.unit.x, this.unit.y, BlockFlag.rally, false);

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
				if(this.unit.within(this.target, this.unit.range() / 1.72)) this.unit.moveAt(tempVec.trns(this.unit.angleTo(this.target) + 180, this.unit.type.speed));
			};
		}
	});
	u.setEffectsC();
	return u;
});

module.exports = {
	distanceGroundAI: distanceGroundAIL
};