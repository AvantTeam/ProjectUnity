//Segmented Unit Library. by Eye of Darkness.
//UnitType must have the function: segmentRegionF, tailRegionF (TextureRegion) and segmentOffsetF (Number)
//TODO: clean up, remove unnessasary functions, test controlling, segment weapons, collision, test damage, and serverside testing
const tempVec1 = new Vec2();
const tempVec2 = new Vec2();

const setUndefined = (mount) => {
	if(mount.reload == undefined) mount.reload = 0.0;
	if(mount.rotation == undefined) mount.rotation = 0.0;
	if(mount.targetRotation == undefined) mount.targetRotation = 0.0;
	if(mount.aimX == undefined) mount.aimX = 0.0;
	if(mount.aimY == undefined) mount.aimY = 0.0;
	if(mount.side == undefined) mount.side = false;
};

/*const updateSegmentVLocal = (unitBase, baseVelocity) => {
	for(var j = 0; j < unitBase.getSegmentLength(); j++){
		var seg = unitBase.getSegmentPositions();
		var segV = unitBase.getSegmentVelocities();
		var segU = unitBase.getSegments();
		
		segV[j].limit(unitBase.type.speed);
		
		var angleB = j != 0 ? Angles.angle(seg[j].x, seg[j].y, seg[j - 1].x, seg[j - 1].y) : Angles.angle(seg[j].x, seg[j].y, unitBase.x, unitBase.y);
		var velocity = j != 0 ? segV[j - 1].len() : baseVelocity.len();
		
		var trueVel = Math.max(velocity, segV[j].len());
		
		tempVec1.trns(angleB, trueVel);
		
		segU[j].vel.set(tempVec1);
		segV[j].add(tempVec1);
		segV[j].setLength(trueVel);
	}
	for(var p = 0; p < unitBase.getSegmentLength(); p++){
		unitBase.getSegmentVelocities()[p].scl(Time.delta);
	}
};

const updateSegmentsLocal = (unitBase) => {
	//var parent = unitBase;
	var segmentOffset = (typeof(unitBase.type.segmentOffsetF) == "function") ? unitBase.type.segmentOffsetF() : unitBase.type.hitsize * 2;
	tempVec1.trns(Angles.angle(unitBase.getSegmentPositions()[0].x, unitBase.getSegmentPositions()[0].y, unitBase.x, unitBase.y) + 180, segmentOffset);
	tempVec1.add(unitBase.x, unitBase.y);
	unitBase.getSegmentPositions()[0].set(tempVec1);
	for(var i = 1; i < unitBase.getSegmentLength(); i++){
		var seg = unitBase.getSegmentPositions();
		
		var angle = Angles.angle(seg[i].x, seg[i].y, seg[i - 1].x, seg[i - 1].y);
		tempVec1.trns(angle, segmentOffset);
		seg[i].set(seg[i - 1]);
		seg[i].sub(tempVec1);
	};
	for(var v = 0; v < unitBase.getSegmentLength(); v++){
		var seg = unitBase.getSegmentPositions();
		var segV = unitBase.getSegmentVelocities();
		var segU = unitBase.getSegments();
		
		seg[v].add(segV[v]);
		var angleD = v == 0 ? Angles.angle(seg[v].x, seg[v].y, unitBase.x, unitBase.y) : Angles.angle(seg[v].x, seg[v].y, seg[v - 1].x, seg[v - 1].y);
		segV[v].scl(Mathf.clamp(1 - unitBase.drag * Time.delta));
		segU[v].set(seg[v].x, seg[v].y);
		segU[v].rotation = angleD;
		segU[v].updateCustom();
	}
};*/

/*const setSegmentsLocal = (unitType, unitBase, segmentLength) => {
	//unitBase.resetSegments(segmentLength);
	//unitBase._segmentVelocities = [];
	//unitBase._segmentUnits = [];
	//unitBase._segmentLength = segmentLength;
	var parent = unitBase;
	for(var i = 0; i < segmentLength; i++){
		var typeS = i == segmentLength - 1 ? 1 : 0;
		//unitBase._segments[i] = new Vec2(unitBase.x, unitBase.y);
		//unitBase._segmentVelocities[i] = new Vec2();
		//unitBase.getSegmentPositions()[i] = new Vec2(unitBase.x, unitBase.y);
		//unitBase.getSegmentVelocities()[i] = new Vec2();
		//unitBase.getSegments()[i] = segmentUnit.get();
		//unitBase._segmentUnits[i].add();
		unitBase.getSegments()[i].setSegmentType(typeS);
		unitBase.getSegments()[i].type = unitType;
		unitBase.getSegments()[i].team = unitBase.team;
		unitBase.getSegments()[i].afterSync();
		//unitBase.getSegments()[i].setTrueParent(unitBase);
		unitBase.getSegments()[i].setParent(parent);
		unitBase.getSegments()[i].add();
		unitBase.getSegments()[i].heal();
		parent = unitBase.getSegments()[i];
	}
};*/

const segmentUnit = prov(() => {
	return extend(UnitEntity, {
		collides(other){
			if(this.getTrueParent() == null) return true;
			for(var i = 0; i < this.getSegmentLength(); i++){
				var entity = this.getSegments()[i];
				if(entity == other) return false;
			};
			return true;
		},
		
		add(){
			if(this.added == true) return;
			
			Groups.all.add(this);
			Groups.unit.add(this);
			Groups.sync.add(this);
			Groups.draw.add(this);
			
			this.added = true;
			
			this.updateLastPosition();
		},
		
		remove(){
			if(!this.added) return;
			
			Groups.all.remove(this);
			Groups.unit.remove(this);
			Groups.sync.remove(this);
			Groups.draw.remove(this);
			
			this.added = false;
			
			this.controller.removed(this.base());
			
			if(Vars.net.client()){
				Vars.netClient.addRemovedEntity(this.id);
			}
		},
		
		damage(amount, effect){
			if(this.getTrueParent() == null) return;
			
			if(effect == undefined){
				this.getTrueParent().damage(amount);
			}else{
				this.getTrueParent().damage(amount, effect);
			}
		},
		
		//both the setter and the getter!
		controller(next){
			if(next == undefined) return this.controller;
			if(!(next instanceof Player)){
				this.controller = next;
				if(this.controller.unit() != this.base()) this.controller.unit(this.base());
			}else if(this.getTrueParent() != null){
				this.getTrueParent().controller = next;
				if(this.getTrueParent().controller.unit() != this.getTrueParent().base()) this.getTrueParent().controller.unit(this.getTrueParent().base());
			}
		},
		
		setSegmentType(val){
			this._segmentType = val;
		},
		
		/*setupWeapons(def){
			
		},*/
		
		setupWeapons(def){
			this.super$setupWeapons(def);
			for(var i = 0; i < this.mounts.length; i++){
				var mount = this.mounts[i];
				if(mount == null) continue;
				print(mount);
				//setUndefined(mount);
			}
		},
		
		setWeaponsB(weaponSeq){
			this.mounts = [];
			weaponSeq.each(w => {
				this.mounts.push(new WeaponMount(w));
			});
		},
		
		//dont save this unit
		serialize(){
			return false;
		},
		
		update(){
			if(this.getParent() == null || this.getParent().dead){
				this.deactivated = true;
				this.dead = true;
				this.remove();
			}
		},
		
		updateCustom(){
			if(this.getTrueParent() != null){
				this.health = this.getTrueParent().health;
				this.maxHealth = this.getTrueParent().maxHealth;
				this.hitTime = this.getTrueParent().hitTime;
			};
			//print(this.dead + ":" + this.deactivated);
			
			if(!Vars.net.client() && !this.dead && !this.deactivated && this.controller != null){
				this.controller.updateUnit();
			};
			
			if(this.controller == null){
				this.resetController();
			};
			
			if(!this.controller.isValidController()){
				this.resetController();
			};
			
			this.updateWeaponsC();
			
			this.updateStatusC();
		},
		
		updateStatusC(){
			if(this.getTrueParent() == null || this.getTrueParent().dead) return;
			if(!this.statuses.isEmpty()){
				this.statuses.each(s => {
					//if(!this.getTrueParent().statuses.contains(s)) this.getTrueParent().statuses.add(s);
					this.getTrueParent().apply(s.effect, s.time);
				});
			};
			this.statuses.clear();
		},
		
		//pain
		updateWeaponsC(){
			var can = this.canShoot();
			for(var i = 0; i < this.mounts.length; i++){
				var mount = this.mounts[i];
				if(mount == null) continue;
				
				var weapon = mount.weapon;
				//print("PRINT:" + mount.reload + ":" + Time.delta + ":" + this.reloadMultiplier);
				//setUndefined(mount);
				mount.reload = Math.max(mount.reload - Time.delta * this.reloadMultiplier, 0);
				if(weapon.otherSide != -1 && weapon.alternate && mount.side == weapon.flipSprite && mount.reload + Time.delta > weapon.reload / 2.0 && mount.reload <= weapon.reload / 2.0){
					this.mounts[weapon.otherSide].side = !(this.mounts[weapon.otherSide].side);
					mount.side = !mount.side;
				};
				if(weapon.rotate && (mount.rotate || mount.shoot) && can){
					var axisX = this.x + Angles.trnsx(this.rotation - 90.0, weapon.x, weapon.y);
					var axisY = this.y + Angles.trnsy(this.rotation - 90.0, weapon.x, weapon.y);
					mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - this.rotation;
					mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, weapon.rotateSpeed * Time.delta);
				}else if(!weapon.rotate){
					mount.rotation = 0.0;
					mount.targetRotation = this.angleTo(mount.aimX, mount.aimY);
				};
				if(mount.shoot && can && (this.ammo > 0.0 || !Vars.state.rules.unitAmmo || this.team.rules().infiniteAmmo) && (!weapon.alternate || mount.side == weapon.flipSprite) && (this.vel.len() >= mount.weapon.minShootVelocity || (Vars.net.active() && !this.isLocal())) && mount.reload <= 0.0001 && Angles.within(weapon.rotate ? mount.rotation : this.rotation, mount.targetRotation, mount.weapon.shootCone)){
					var rotation = this.rotation - 90.0;
					var weaponRotation = rotation + (weapon.rotate ? mount.rotation : 0.0);
					var mountX = this.x + Angles.trnsx(rotation, weapon.x, weapon.y);
					var mountY = this.y + Angles.trnsy(rotation, weapon.x, weapon.y);
					var shootX = mountX + Angles.trnsx(weaponRotation, weapon.shootX, weapon.shootY);
					var shootY = mountY + Angles.trnsy(weaponRotation, weapon.shootX, weapon.shootY);
					
					var shootAngle = weapon.rotate ? (weaponRotation + 90.0) : (Angles.angle(shootX, shootY, mount.aimX, mount.aimY) + this.rotation - this.angleTo(mount.aimX, mount.aimY));
					this.shoot(weapon, shootX, shootY, mount.aimX, mount.aimY, shootAngle, Mathf.sign(weapon.x));
					mount.reload = weapon.reload;
					this.ammo--;
					if(this.ammo < 0.0) this.ammo = 0.0;
				}
			}
		},
		
		shoot(weapon, x, y, aimX, aimY, rotation, side){
			weapon.shootSound.at(x, y, Mathf.random(0.8, 1.0));
			var ammo = weapon.bullet;
			var lifeScl = ammo.scaleVelocity ? Mathf.clamp(Mathf.dst(x, y, aimX, aimY) / ammo.range()) : 1.0;
			//this.sequenceNum = 0;
			this._shootSequence = 0;
			var as = this;
			if(weapon.shotDelay > 0.01){
				Angles.shotgun(weapon.shots, weapon.spacing, rotation, f => {
					Time.run(this._shootSequence * weapon.shotDelay, () => {
						//to prevent it from redefining, hopefully.
						var qAs = as;
						var qW = weapon;
						var qX = x;
						var qY = y;
						var qF = f;
						var qIn = weapon.inaccuracy;
						var qLf = lifeScl;
						qAs.bullet(qW, qX, qY, qF + Mathf.range(qIn), qLf);
					});
					this._shootSequence++;
				});
			}else{
				Angles.shotgun(weapon.shots, weapon.spacing, rotation, f => {as.bullet(weapon, x, y, f + Mathf.range(weapon.inaccuracy), lifeScl)});
			};
			if(this instanceof Velc){
				this.vel.add(Tmp.v1.trns(rotation + 180.0, ammo.recoil));
			};
			var parentize = ammo.keepVelocity;
			Effect.shake(weapon.shake, weapon.shake, x, y);
			weapon.ejectEffect.at(x, y, rotation * side);
			ammo.shootEffect.at(x, y, rotation, parentize ? this : 0);
			ammo.smokeEffect.at(x, y, rotation, parentize ? this : 0);
		},
		
		bullet(weapon, x, y, angle, lifescl){
			weapon.bullet.create(this, this.team, x, y, angle, (1.0 - weapon.velocityRnd) + Mathf.random(weapon.velocityRnd), lifescl);
		},
		
		drawBodyC(){
			this.type.applyColor(this);
			
			var region = this._segmentType == 0 ? this.type.segmentRegionF() : this.type.tailRegionF();

			Draw.rect(region, this, this.rotation - 90);

			Draw.reset();
		},
		
		draw(){
			
		},
		
		trueDraw(){
			this.super$draw();
		},
		
		setTrueParent(parent){
			this._shootSequence = 0;
			this._trueParentUnit = parent;
		},
		
		getTrueParent(){
			return this._trueParentUnit;
		},
		
		setParent(parent){
			this._parentUnit = parent;
		},
		
		getParent(){
			return this._parentUnit;
		}
	});
});

module.exports = {
	setUniversal(unitType, baseClass, segmentLength, obj){
		//unitType.segmentRegion = null;
		//unitType.tailRegion = null;
		//unitType.segmentRegionF = () => segmentRegion;
		//unitType.tailRegionF = () => tailRegion;
		obj = Object.assign(obj, {
			setEffects(length){
				this._segmentLength = length;
				this._segmentUnits = [];
				this._segments = [];
				this._segmentVelocities = [];
				this._lastVelocityC = new Vec2();
				for(var i = 0; i < length; i++){
					this._segments[i] = new Vec2();
					this._segmentVelocities[i] = new Vec2();
					this._segmentUnits[i] = segmentUnit.get();
					this._segmentUnits[i].setTrueParent(this);
				}
			},
			update(){
				this._lastVelocityC.set(this.vel);
				this.super$update();
				this.updateSegmentVLocal(this._lastVelocityC);
				this.updateSegmentsLocal();
			},
			updateSegmentVLocal(vec){
				for(var j = 0; j < this.getSegmentLength(); j++){
					var seg = this.getSegmentPositions();
					var segV = this.getSegmentVelocities();
					var segU = this.getSegments();
					
					segV[j].limit(this.type.speed);
					
					var angleB = j != 0 ? Angles.angle(seg[j].x, seg[j].y, seg[j - 1].x, seg[j - 1].y) : Angles.angle(seg[j].x, seg[j].y, this.x, this.y);
					var velocity = j != 0 ? segV[j - 1].len() : vec.len();
					
					var trueVel = Math.max(velocity, segV[j].len());
					
					tempVec1.trns(angleB, trueVel);
					
					segU[j].vel.set(tempVec1);
					segV[j].add(tempVec1);
					segV[j].setLength(trueVel);
				}
				for(var p = 0; p < this.getSegmentLength(); p++){
					this.getSegmentVelocities()[p].scl(Time.delta);
				}
			},
			updateSegmentsLocal(){
				var segmentOffset = (typeof(this.type.segmentOffsetF) == "function") ? this.type.segmentOffsetF() : this.type.hitsize * 2;
				tempVec1.trns(Angles.angle(this.getSegmentPositions()[0].x, this.getSegmentPositions()[0].y, this.x, this.y) + 180, segmentOffset);
				tempVec1.add(this.x, this.y);
				this.getSegmentPositions()[0].set(tempVec1);
				for(var i = 1; i < this.getSegmentLength(); i++){
					var seg = this.getSegmentPositions();
					
					var angle = Angles.angle(seg[i].x, seg[i].y, seg[i - 1].x, seg[i - 1].y);
					tempVec1.trns(angle, segmentOffset);
					seg[i].set(seg[i - 1]);
					seg[i].sub(tempVec1);
				};
				for(var v = 0; v < this.getSegmentLength(); v++){
					var seg = this.getSegmentPositions();
					var segV = this.getSegmentVelocities();
					var segU = this.getSegments();
					
					seg[v].add(segV[v]);
					var angleD = v == 0 ? Angles.angle(seg[v].x, seg[v].y, this.x, this.y) : Angles.angle(seg[v].x, seg[v].y, seg[v - 1].x, seg[v - 1].y);
					segV[v].scl(Mathf.clamp(1 - this.drag * Time.delta));
					segU[v].set(seg[v].x, seg[v].y);
					segU[v].rotation = angleD;
					segU[v].updateCustom();
				}
			},
			write(writes){
				this.super$write(writes);
				
				writes.s(this._segmentLength);
				
				for(var i = 0; i < this._segmentLength; i++){
					writes.f(this._segments[i].x);
					writes.f(this._segments[i].y);
				}
			},
			read(reads){
				this.super$read(reads);
				
				this._segmentLength = reads.s();
				
				if(typeof(this._segments) == "undefined") this._segments = [];
				
				for(var i = 0; i < this._segmentLength; i++){
					if(this._segments[i] == null) this._segments[i] = new Vec2();
					this._segments[i].x = reads.f();
					this._segments[i].y = reads.f();
				}
			},
			clipSize(){
				var segmentOffset = (typeof(this.type.segmentOffsetF) == "function") ? this.type.segmentOffsetF() : this.type.hitsize * 2;
				return this.getSegmentLength() * segmentOffset * 2;
			},
			add(){
				this.super$add();
				
				//setSegmentsLocal(this.type, this, this._segmentLength);
				var parent = this;
				for(var i = 0; i < segmentLength; i++){
					var typeS = i == segmentLength - 1 ? 1 : 0;
					//this._segments[i] = new Vec2(this.x, this.y);
					//this._segmentVelocities[i] = new Vec2();
					//this.getSegmentPositions()[i] = new Vec2(this.x, this.y);
					//this.getSegmentVelocities()[i] = new Vec2();
					//this.getSegments()[i] = segmentUnit.get();
					//this._segmentUnits[i].add();
					this.getSegments()[i].setSegmentType(typeS);
					this.getSegments()[i].type = this.type;
					this.getSegments()[i].controller = this.type.createController();
					this.getSegments()[i].set(this.type, this.type.createController());
					this.getSegments()[i].controller.unit(this.getSegments()[i]);
					this.getSegments()[i].team = this.team;
					this.getSegments()[i].afterSync();
					//this.getSegments()[i].setTrueParent(this);
					this.getSegments()[i].setParent(parent);
					this.getSegments()[i].add();
					this.getSegments()[i].heal();
					parent = this.getSegments()[i];
				}
			},
			getSegments(){
				return this._segmentUnits;
			},
			
			getSegmentPositions(){
				return this._segments;
			},
			
			getSegmentLength(){
				return this._segmentLength;
			},
			
			getSegmentVelocities(){
				return this._segmentVelocities;
			}
		});
		
		unitType.constructor = () => {
			const uSegmentLength = segmentLength;
			var unit = extend(baseClass, obj);
			unit.setEffects(uSegmentLength);
			//unit._segmentUnits = [];
			//unit._segments = [];
			//unit._segmentVelocities = [];
			//unit._segmentLength = segmentLength;
			return unit;
		}
	},
	//called when finishing setting segment weapons
	//currently unusable, uses the main unit type weapons instead.
	sortWeapons(weaponSeq){
		var mapped = new Seq();
		for(var i = 0; i < weaponSeq.size; i++){
			var w = weaponSeq.get(i);
			mapped.add(w);
			
			if(w.mirror){
				var copy = w.copy();
				copy.x *= -1;
				copy.shootX *= -1;
				copy.flipSprite = !copy.flipSprite;
				mapped.add(copy);
				
				w.reload *= 2;
				copy.reload *= 2;

				w.otherSide = mapped.size - 1;
				copy.otherSide = mapped.size - 2;
			}
		};
		weaponSeq.set(mapped);
	},
	//add() in entity
	/*setSegments(unitType, unitBase, segmentLength){
		setSegmentsLocal(unitType, unitBase, segmentLength);
	},*/
	//update(unit) in unit type
	/*updateSegments(unitBase){
		updateSegmentsLocal(unitBase);
	},*/
	//drawBody(unit) in unit type
	drawSegments(unitBase){
		var originZ = Draw.z();
		
		for(var i = 0; i < unitBase.getSegmentLength(); i++){
			Draw.z(originZ - ((i + 1) / 40));
			unitBase.getSegments()[i].drawBodyC();
			unitBase.getSegments()[i].type.drawWeapons(unitBase.getSegments()[i]);
		};
		
		Draw.z(originZ);
	}
};