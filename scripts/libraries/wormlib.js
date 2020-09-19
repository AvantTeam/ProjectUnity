//Segmented Unit Library. by Eye of Darkness.
//UnitType must have the function: segmentRegionF, tailRegionF (TextureRegion), setTypeID, getTypeID segmentOffsetF (Number) and getSegmentWeapon (Seq)
//TODO: clean up, remove unnessasary functions, test controlling, segment weapons, collision, test damage, and serverside testing
const tempVec1 = new Vec2();
const tempVec2 = new Vec2();

var segmentID = 3;

var mainID = 3;

const addMapping = provider => {
	for(var i = 0; i < EntityMapping.idMap.length; i++){
		if(EntityMapping.idMap[i] == undefined){
			//print("EntityMapping: (" + i + "): " + provider);
			EntityMapping.idMap[i] = provider;
			break;
		}
	}
};

const setUndefined = mount => {
	if(mount.reload == undefined) mount.reload = 0.0;
	if(mount.rotation == undefined) mount.rotation = 0.0;
	if(mount.targetRotation == undefined) mount.targetRotation = 0.0;
	if(mount.aimX == undefined) mount.aimX = 0.0;
	if(mount.aimY == undefined) mount.aimY = 0.0;
	if(mount.side == undefined) mount.side = false;
};

/*const drawWeaponsC = unitBase => {
	
};*/

//sk's object cloner.
const clone = obj => {
	if(obj === null || typeof(obj) !== "object") return obj;

	var copy = obj.constructor();

	for(var attr in obj){
		if(obj.hasOwnProperty(attr)){
			copy[attr] = obj[attr];
		}
	};

	return copy;
};

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

		setStats(type){
			this.type = type;
			this.maxHealth = type.health;
			this.drag = type.drag;
			this.armor = type.armor;
			this.hitSize = type.hitsize;
			this.hovering = type.hovering;
			if(this.controller == null) this.controller(type.createController());
			if(this.mounts.length != type.weapons.size) this.setupWeapons(type);
		},

		remove(){
			if(!this.added) return;

			Groups.all.remove(this);
			Groups.unit.remove(this);
			Groups.sync.remove(this);
			Groups.draw.remove(this);

			this.added = false;

			//this.controller.removed(this.self());
			this.controller.removed(this);

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
				if(this.controller.unit() != this) this.controller.unit(this);
			}else if(this.getTrueParent() != null){
				this.getTrueParent().controller = next;
				if(this.getTrueParent().controller.unit() != this.getTrueParent()) this.getTrueParent().controller.unit(this.getTrueParent());
			}
		},
		
		isPlayer(){
			if(this.getTrueParent() == null) return false;
			return this.getTrueParent().controller instanceof Player;
		},
		
		getPlayer(){
			if(this.getTrueParent() == null) return null;
			return this.isPlayer() ? this.getTrueParent().controller : null;
		},
		
		classId(){
			return segmentID;
		},

		heal(amount){
			if(amount == undefined){
				this.dead = false;
				this.health = this.maxHealth;
				return;
			};

			this.health += amount;
			this.clampHealth();
		},
		
		kill(){
			if(this.dead || Vars.net.client()) return;
			if(this.getTrueParent() == null) Call.unitDeath(this.getTrueParent().id);
			Call.unitDeath(this.id);
		},

		setSegmentType(val){
			this._segmentType = val;
		},

		/*setupWeapons(def){

		},*/

		setupWeapons(def){
			if(!(typeof(def.getSegmentWeapon) == "function")){
				this.super$setupWeapons(def);
			}else{
				//weapons must be sorted.
				/*this.mounts = [];
				//this.mounts = java.util.Arrays.copyOf(this.mounts, def.getSegmentWeapon().size);
				for(var i = 0; i < def.getSegmentWeapon().size; i++){
					this.mounts[i] = new WeaponMount(def.getSegmentWeapon().get(i));
				};*/
				var tmpSeq = new Seq();
				
				for(var i = 0; i < def.getSegmentWeapon().size; i++){
					tmpSeq.add(new WeaponMount(def.getSegmentWeapon().get(i)));
				};
				
				this.mounts = tmpSeq.toArray(WeaponMount);
			};
			/*for(var i = 0; i < this.mounts.length; i++){
				var mount = this.mounts[i];
				if(mount == null) continue;
				//print(mount);
				//setUndefined(mount);
			}*/
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

		drawShadowC(){
			var region = this._segmentType == 0 ? this.type.segmentRegionF() : this.type.tailRegionF();

			Draw.color(UnitType.shadowColor);

			var e = Math.max(this.elevation, this.type.visualElevation);
			Draw.rect(region, this.x + (UnitType.shadowTX * e), this.y + (UnitType.shadowTY * e), this.rotation - 90);
			Draw.color();
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

//EntityMapping.idMap.push(segmentUnit);
addMapping(segmentUnit);

segmentID = EntityMapping.idMap.indexOf(segmentUnit) != -1 ? EntityMapping.idMap.indexOf(segmentUnit) : 3;

const defaultUnit = prov(s => {
	s = extend(UnitEntity, {
		getSegmentLength(){
			return typeof(this.type.segmentLength) == "function" ? this.type.segmentLength() : 9;
		},
		setEffects(){
			this._segmentUnits = [];
			this._segments = [];
			this._segmentVelocities = [];
			this._lastVelocityC = new Vec2();
			for(var i = 0; i < this.getSegmentLength(); i++){
				this._segments[i] = new Vec2(this.x, this.y);
				this._segmentVelocities[i] = new Vec2();
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
		//TODO: save segment position
		/*write(writes){
			this.super$write(writes);

			for(var i = 0; i < this.getSegmentLength(); i++){
				writes.f(this.getSegmentPositions()[i].x);
				writes.f(this.getSegmentPositions()[i].y);
			}
		},
		read(reads){
			this.super$read(reads);

			for(var i = 0; i < this.getSegmentLength(); i++){
				//if(this.getSegmentPositions()[i] == null) this.getSegmentPositions()[i] = new Vec2();
				this.getSegmentPositions()[i].x = reads.f();
				this.getSegmentPositions()[i].y = reads.f();
			}
		},*/
		classId(){
			return mainID;
		},
		clipSize(){
			var segmentOffset = (typeof(this.type.segmentOffsetF) == "function") ? this.type.segmentOffsetF() : this.type.hitsize * 2;
			return this.getSegmentLength() * segmentOffset * 2;
		},

		drawOcclusionC(){
			for(var i = 0; i < this.getSegmentLength(); i++){
				this.type.drawOcclusion(this.getSegments()[i]);
			}
		},

		add(){
			this.super$add();
			
			this.setEffects();

			var parent = this;
			for(var i = 0; i < this.getSegmentLength(); i++){
				var typeS = i == this.getSegmentLength() - 1 ? 1 : 0;
				this.getSegments()[i] = segmentUnit.get();
				this.getSegments()[i].elevation = this.elevation;
				this.getSegments()[i].setSegmentType(typeS);
				this.getSegments()[i].type = this.type;
				this.getSegments()[i].controller = this.type.createController();
				this.getSegments()[i].controller.unit(this.getSegments()[i]);
				this.getSegments()[i].team = this.team;
				this.getSegments()[i].setTrueParent(this);
				this.getSegments()[i].setParent(parent);
				this.getSegments()[i].add();
				this.getSegments()[i].afterSync();
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

		getSegmentVelocities(){
			return this._segmentVelocities;
		}
	});
	//s.setEffects();
	return s;
});

//EntityMapping.idMap.push(defaultUnit);
addMapping(defaultUnit);

mainID = EntityMapping.idMap.indexOf(defaultUnit) != -1 ? EntityMapping.idMap.indexOf(defaultUnit) : 3;
//print(mainID);

module.exports = {
	//its recommended to set custom to false to not fill the EntityMapping array. only use for units with special effects
	setUniversal(unitType, baseClass, custom, obj){
		//unitType.segmentRegion = null;
		//unitType.tailRegion = null;
		//unitType.segmentRegionF = () => segmentRegion;
		//unitType.tailRegionF = () => tailRegion;
		if(custom){
			obj = Object.assign({
				getSegmentLength(){
					return 9;
				}
			}, obj, {
				setEffects(){
					this._segmentUnits = [];
					this._segments = [];
					this._segmentVelocities = [];
					this._lastVelocityC = new Vec2();
					for(var i = 0; i < this.getSegmentLength(); i++){
						this._segments[i] = new Vec2();
						this._segmentVelocities[i] = new Vec2();
						//this._segmentUnits[i] = segmentUnit.get();
						//this._segmentUnits[i].setTrueParent(this);
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
	
					for(var i = 0; i < this.getSegmentLength(); i++){
						writes.f(this.getSegmentPositions()[i].x);
						writes.f(this.getSegmentPositions()[i].y);
					}
				},
				read(reads){
					this.super$read(reads);
	
					for(var i = 0; i < this.getSegmentLength(); i++){
						//if(this.getSegmentPositions()[i] == null) this.getSegmentPositions()[i] = new Vec2();
						this.getSegmentPositions()[i].x = reads.f();
						this.getSegmentPositions()[i].y = reads.f();
					}
				},
				
				clipSize(){
					var segmentOffset = (typeof(this.type.segmentOffsetF) == "function") ? this.type.segmentOffsetF() : this.type.hitsize * 2;
					return this.getSegmentLength() * segmentOffset * 2;
				},
	
				drawOcclusionC(){
					for(var i = 0; i < this.getSegmentLength(); i++){
						this.type.drawOcclusion(this.getSegments()[i]);
					}
				},
	
				add(){
					this.super$add();
	
					var parent = this;
					for(var i = 0; i < this.getSegmentLength(); i++){
						var typeS = i == this.getSegmentLength() - 1 ? 1 : 0;
						this.getSegmentPositions()[i].set(this.x, this.y);
						this.getSegments()[i] = segmentUnit.get();
						this.getSegments()[i].setSegmentType(typeS);
						this.getSegments()[i].elevation = this.elevation;
						this.getSegments()[i].type = this.type;
						this.getSegments()[i].controller = this.type.createController();
						this.getSegments()[i].controller.unit(this.getSegments()[i]);
						this.getSegments()[i].team = this.team;
						this.getSegments()[i].setTrueParent(this);
						this.getSegments()[i].setParent(parent);
						this.getSegments()[i].add();
						this.getSegments()[i].afterSync();
						this.getSegments()[i].heal();
						parent = this.getSegments()[i];
					}
				},
				
				classId(){
					return (typeof(this.type.getTypeID) == "function") ? this.type.getTypeID() : this.super$classId();
				},
				
				getSegments(){
					return this._segmentUnits;
				},
	
				getSegmentPositions(){
					return this._segments;
				},
	
				getSegmentVelocities(){
					return this._segmentVelocities;
				}
			});
			
			/*var sf = prov(unit => {
				unit = extend(baseClass, clone(obj));
				unit.setEffects();
				return unit;
			});
			
			EntityMapping.idMap.push(sf);
			
			var se = EntityMapping.idMap.indexOf(sf) != -1 ? EntityMapping.idMap.indexOf(sf) : 3;
			
			if(typeof(unitType.setTypeID) == "function") unitType.setTypeID(se);*/
			
			/*unitType.constructor = prov(unit => {
				unit = extend(baseClass, clone(obj));
				unit.setEffects();
				return unit;
			});*/
			
			unitType.constructor = prov(unit => {
				unit = extend(baseClass, clone(obj));
				unit.setEffects();
				return unit;
			});
			
			//EntityMapping.idMap.push(unitType.constructor);
			addMapping(unitType.constructor);
			
			var se = EntityMapping.idMap.indexOf(unitType.constructor) != -1 ? EntityMapping.idMap.indexOf(unitType.constructor) : 3;
			
			if(typeof(unitType.setTypeID) == "function") unitType.setTypeID(se);
		}else{
			unitType.constructor = defaultUnit;
		}
	},
	
	addConstructor(provider){
		addMapping(provider);
	},
	//called when finishing setting segment weapons.
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

	//drawBody(unit) in unit type
	drawSegments(unitBase){
		var originZ = Draw.z();

		if(typeof(unitBase.getSegmentLength) != "function") return;
		for(var i = 0; i < unitBase.getSegmentLength(); i++){
			Draw.z(originZ - ((i + 1) / 60));
			unitBase.getSegments()[i].drawBodyC();
			//drawWeaponsC(unitBase.getSegments()[i]);
			unitBase.getSegments()[i].type.drawWeapons(unitBase.getSegments()[i]);
		};

		Draw.z(originZ);
	},

	//drawShadow(unit) in unit type
	drawShadowSegments(unitBase){
		if(typeof(unitBase.getSegmentLength) != "function") return;
		for(var i = 0; i < unitBase.getSegmentLength(); i++){
			unitBase.getSegments()[i].drawShadowC();
		};
	},

	//drawOcclusion(unit) in unit type
	drawOcclusionSegments(unitBase){
		if(typeof(unitBase.drawOcclusionC) == "function") unitBase.drawOcclusionC();
	}
};
