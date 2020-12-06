const waitFx = new Effect(30, e => {
  if(!Array.isArray(e.data)) return;
  if(e.data[1] == null || !e.data[1].isValid() || e.data[1].dead) return;
  Draw.z(Layer.effect - 0.00001);
  Draw.color(e.color);
  Lines.stroke(e.fout() * 1.5);
  Lines.polySeg(60, 0, 60 * (1 - (e.rotation - Time.time) / e.data[0]), e.data[1].x, e.data[1].y, 8, 0);
});

const ringFx = new Effect(25, e => {
  if(!(e.data instanceof Unit)) return;
  if(!e.data.isValid() || e.data.dead) return;
  Draw.color(Color.white, e.color, e.fin());
  Lines.stroke(e.fout() * 1.5);
  Lines.circle(e.data.x, e.data.y, 8);
});

//deepcopy no work
const clone = obj => {
    if(obj === null || typeof(obj) !== 'object') return obj;
    var copy = obj.constructor();
    for(var attr in obj) {
        if(obj.hasOwnProperty(attr)) {
            copy[attr] = obj[attr];
        }
    };
    return copy;
}

const addMapping = provider => {
  var i = 0;
	for(i = 0; i < EntityMapping.idMap.length; i++){
		if(EntityMapping.idMap[i] == undefined){
			//print("EntityMapping: (" + i + "): " + provider);
			EntityMapping.idMap[i] = provider;
			break;
		}
	}
  return EntityMapping.idMap.indexOf(provider);
};

module.exports = {
  waitFx: waitFx,
  ringFx: ringFx,

  addpassive(unittype, obj, id){
    obj = Object.assign({
      //this ability recharges when idle and used automatically when fully recharged and able(u) is true. override used(u).

      name: "$ability.passive",
      rechargeTime: 120,

      localized(){
        return this.name;
      },
      able(u){
        return true;
      },
      used(u){
        print("Used Ability!");
      },

      update(u){
        if(u.getCool(id) + this.rechargeTime < Time.time && this.able(u)) this.tryuse(u);
      }
    }, obj, {
      tryuse(u){
        u.setCool(id, Time.time);
        this.used(u);
      }
    });

    unittype.abilities.add(new JavaAdapter(Ability, clone(obj)));
  },

  addconditional(unittype, obj, id){
    obj = Object.assign({
      //this ability recharges when idle and is attempted when able(u) *turns* true. override used(u).

      name: "$ability.conditional",
      rechargeTime: 60,
      rechargeFx: ringFx,
      color: Pal.lancerLaser,
      //whether the fully recharged fx is visible to anyone else
      rechargeVisible: true,
      //whether the "not yet" fx is visible to anyone else
      chargingVisible: false,

      localized(){
        return this.name;
      },
      able(u){
        return true;
      },
      used(u){
        print("Used Ability!");
      },

      notyet(u, whenready){
        if(this.chargingVisible || (u.isPlayer() && u.getPlayer() == Vars.player)) waitFx.at(u.x, u.y, whenready, this.color, [this.rechargeTime, u]);
      },
      update(u){
        if(u.useCheck(id, this.able(u))) this.tryuse(u);

        if(this.rechargeFx != Fx.none && u.getCool(id) + this.rechargeTime < Time.time && u.getUse(id) && (this.rechargeVisible || (u.isPlayer() && u.getPlayer() == Vars.player))) this.rechargeFx.at(u.x, u.y, 0, this.color, u);
      }
    }, obj, {
      tryuse(u){
        if(u.getCool(id) + this.rechargeTime > Time.time) this.notyet(u, u.getCool(id) + this.rechargeTime);
        else{
          u.usedCool(id, Time.time);
          this.used(u);
        }
      }
    });

    unittype.abilities.add(new JavaAdapter(Ability, clone(obj)));
  },

  add(unittype, unitentity, abarr, objb){
    if(objb == undefined) objb = {};
    for(var i=0; i<abarr.length; i++){
      this["add" + abarr[i].type](unittype, abarr[i], i);
    }

    objb = Object.assign({
      classId(){
        return this.type.getTypeID();
      }
    }, objb, {
      setCoolTimer(ids){
        this._timers = [];
        this._isUsed = [];
        this._check = [];
        for(var i=0; i<ids; i++){
          this._timers.push(0);
          this._isUsed.push(false);
          this._check.push(true);
        }
      },
      getCool(id){
        return this._timers[id];
      },
      setCool(id, a){
        this._timers[id] = a;
      },
      usedCool(id, a){
        this._timers[id] = a;
        this._isUsed[id] = true;
      },
      getUse(id){
        if(this._isUsed[id]){
          this._isUsed[id] = false;
          return true;
        }
        return false;
      },
      useCheck(id, b){
        if(b){
          if(!this._check[id]){
            this._check[id] = true;
            return true;
          }
        }
        else this._check[id] = false;
        return false;
      }
    });

    unittype.constructor = prov(unit => {
      unit = extend(unitentity, clone(objb));
      unit.setCoolTimer(abarr.length);
      return unit;
    });

    return addMapping(unittype.constructor);
  }
}




/*
might have a use later h
mech.alpha-mech.name = Alpha
mech.alpha-mech.weapon = Heavy Repeater
mech.alpha-mech.ability = Regeneration
mech.delta-mech.name = Delta
mech.delta-mech.weapon = Arc Generator
mech.delta-mech.ability = Discharge
mech.tau-mech.name = Tau
mech.tau-mech.weapon = Restruct Laser
mech.tau-mech.ability = Repair Burst
mech.omega-mech.name = Omega
mech.omega-mech.weapon = Swarm Missiles
mech.omega-mech.ability = Armored Configuration
mech.dart-ship.name = Dart
mech.dart-ship.weapon = Repeater
mech.javelin-ship.name = Javelin
mech.javelin-ship.weapon = Burst Missiles
mech.javelin-ship.ability = Discharge Booster
mech.trident-ship.name = Trident
mech.trident-ship.weapon = Bomb Bay
mech.glaive-ship.name = Glaive
mech.glaive-ship.weapon = Flame Repeater
*/
