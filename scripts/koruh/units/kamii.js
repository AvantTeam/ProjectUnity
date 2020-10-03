/*const simpleShoot = (bulletType, team, x, y, rotation, offset) => {
	
};*/

const difficulty = 1.2;

const testBullet = new BasicBulletType();
testBullet.damage = 60;
testBullet.speed = 4;
testBullet.lifetime = 170;
testBullet.shrinkY = 0;
testBullet.width = 12;
testBullet.height = 12;

const tempVec2 = new Vec2();

const bulletSurround = (x, y, rotation, amount, shootObject) => {
	var amountA = Mathf.round(amount);
	for(var i = 0; i < amountA; i++){
		var angle = 360 / amountA * i + rotation;
		
		shootObject.rotation += angle;
		shootObject.handleLoop(i);
		shootObject.x += x;
		shootObject.y += y;
		shootObject.shoot();
		
		//otherFunc(bulletType, team, x, y, angle, 0);
	}
};

function SimpleShoot(owner, bulletType){
	this.x = 0;
	this.y = 0;
	this.rotation = 0;
	this.bulletType = bulletType;
	this.owner = owner;
	this.handleLoop = function(value){
		
	};
	this.shoot = function(){
		if(this.bulletType == null || this.owner == null) return null;
		return this.bulletType.create(this.owner, this.owner.team, this.x, this.y, this.rotation);
	}
};

const kamiController = prov(tmp => {
	tmp = extend(FlyingAI, {
		setEffects(){
			this._startResetting = true;
			this._targetPosition = new Vec2();
			this._nextPosition = new Vec2();
			this._sequence = 0;
			this._sequenceTime = 0;
			this._sequenceProgress = 0;
			this._noShootSpacing = 0;
			this._shootSpace = 0;
			this._currentReload = [0, 0, 0, 0, 0, 0];
			this._otherEffects = [];
			this._reloadTime = [0, 0, 0, 0, 0, 0];
		},
		
		updateUnit(){
			this.super$updateUnit();
			//bulletSequenceA();
		},
		
		updateBulletHell(){
			bulletSequenceA();
		},
		
		updateMovement(){
			if(this.target != null){
				this.moveTo(this.target, 190);
				if(this.unit.within(this.target, 210)){
					this.updateBulletHell();
				}
			}
		},
		
		bulletSequenceA(){
			if(this._startResetting){
				this._currentReload[0] = 6;
				this._otherEffects[0] = 0;
				
				this._startResetting = false;
			};
			
			var unit = this.unit;
			
			this._otherEffects[0] += Time.delta;
			this._otherEffects[0] = this._otherEffects[0] % 360;
			this._reloadTime[0] += Time.delta;
			if(this._reloadTime[0] > this._currentReload[0]){
				bulletSurround(unit.x, unit.y, this._otherEffects[0], 23 * difficulty, new SimpleShoot(unit, unit.team));
				this._reloadTime[0] = 0;
			}
		}
		/*findTarget(x, y, range, air, ground){
			var result = Units.closestEnemy(this.unit.team, x, y, 2500, unit => unit.controller instanceof Player);
			if(ground) result = this.targetFlag(x, y, BlockFlag.producer, true);
			if(result != null) return result;
		}*/
	});
	tmp.setEffects();
	
	return tmp;
});

const kami = extendContent(UnitType, "kami-mkii", {});
kami.flying = true;
kami.health = 1200000;
kami.range = 520;
kami.ammoCapacity = 1280000000;
kami.defaultController = kamiController;
kami.ammoType = new AmmoTypes.PowerAmmoType(3000);