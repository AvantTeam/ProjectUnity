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

const smallRingFx = new Effect(20, e => {
  if(!(e.data instanceof Unit)) return;
  if(!e.data.isValid() || e.data.dead) return;
  Draw.color(Color.white, e.color, e.fin());
  Lines.stroke(e.fout());
  Lines.circle(e.data.x, e.data.y, e.finpow() * 5);
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

//you need to add
/*
setTypeID(id){
  this.idType = id;
},
getTypeID(){
  return this.idType;
}*/
//to the UnitType, and also the following:
/*
var classid = alib.add(unittypehere, MechUnit, [abilitieshere], {unitentityextensionhere}, whethertoaddtoentitymapping);
unittypehere.setTypeID(classid);
*/

var thelib = {
  waitFx: waitFx,
  ringFx: ringFx,

  setupActive(dothis){
    if(!dothis) return;
    Vars.netServer.addPacketHandler("skilluse", (p, h) => {
      //following will be called on server
      if(p.unit() != null && p.unit().useSkills) this.syncFromServer(p.unit());
    });
    this.setupMobile(Vars.mobile);
    this.setupDesktop(!Vars.mobile);
  },

  setupMobile(dothis){
    if(!dothis) return;
    if((typeof GestureDetector) == "undefined") var GestureDetector = Packages.arc.input.GestureDetector;

    var detecc = extend(GestureDetector.GestureListener, {
      tap(x, y, count, button){
        if(count == 2){
          if(Vars.state.isMenu() || Vars.control.input.lineMode || Core.scene.hasMouse(x, y) || Vars.control.input.isPlacing() || Vars.control.input.isBreaking() || Vars.control.input.selectedUnit() != null) return false;
          this.notifyClick();
        }
        return false;
      }
    });

    Core.input.addProcessor(new GestureDetector(detecc));
  },

  setupDesktop(dothis){
    if(!dothis) return;
    var press = false;
    Events.run(Trigger.update, () => {
      if(!Vars.state.isMenu()){
        if(Core.input.keyDown(Binding.boost)){
          if(!press){
            press = true;
            this.notifyClick();
          }
        }
        else press = false;
      }
      else press = false;
    });
  },

  notifyClick(){
    //print("Notify");
    if(Vars.headless) return;
    if(!Vars.net.active()) this.syncedClick(Vars.player.unit());
    else if(!Vars.net.client()) this.syncFromServer(Vars.player.unit());
    else Call.serverPacketReliable("skilluse", "");
  },

  syncFromServer(u){
    //runs on the server. TODO: make it run syncedclick on all clients
    this.syncedClick(u);
  },

  syncedClick(u){
    //the unit u has pressed the skill button. this should run in all clients & the server (TODO)
    if(u.useSkills) u.useSkills();
  },

  add(unittype, unitentity, abarr, objb, addToMap){
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
        this._left = [];
        for(var i=0; i<ids; i++){
          this._timers.push(0);
          this._isUsed.push(false);
          this._check.push(true);
          this._left.push(1);
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
      },
      slotsLeft(id){
        return this._left[id];
      },
      useSkills(){
        //this should run on all clients (TODO)
        //print("UseSkills");
        this.abilities.each(ab => {
          print(ab);
          if(ab.isActiveSkill) ab.tryuse(this);
        });
      }
    });

    unittype.constructor = prov(unit => {
      unit = extend(unitentity, clone(objb));
      unit.setCoolTimer(abarr.length);
      return unit;
    });

    return addToMap ? addMapping(unittype.constructor) : -1;
  },

  //do not use/rename the following functions!

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

  addactive(unittype, obj, id){
    obj = Object.assign({
      //this ability recharges when idle and is used when the boost binding is clicked or the screen is double-tapped on mobile. note that this ability will be synced. override used(u).

      name: "$ability.active",
      //how mich ticks it takes for all slots to fill back up after depleting them all
      rechargeTime: 60,
      rechargeFx: ringFx,
      color: Pal.lancerLaser,
      //whether the fully recharged fx is visible to anyone else
      rechargeVisible: true,
      //whether the "not yet" fx is visible to anyone else
      chargingVisible: false,
      //ability slots, must be a positive interger. "disabled" if it is 1
      slots: 1,
      //how much ticks it takes for a single slot to passively recharge when there are still slots left
      singleRecharge: 30,
      singleRechargeFx: smallRingFx,
      //whether ai mindlessly spams the skill, or just not use it at all
      aiUse: false,

      localized(){
        return this.name;
      },
      able(u){
        //if(!Vars.mobile) return input.keyDown(Binding.boost);
        return true;
      },
      used(u){
        print("Used Ability!");
      },

      aiShouldUse(u){
        return true;//todo is shooting
      },

      notyet(u, whenready){
        if(this.chargingVisible || (u.isPlayer() && u.getPlayer() == Vars.player)) waitFx.at(u.x, u.y, whenready, this.color, [this.rechargeTime, u]);
      },
      update(u){
        //if((u.isPlayer() && u.getPlayer() == Vars.player) && u.useCheck(id, Core.input.keyDown(Binding.boost))) this.tryuse(u);
        if(!u.isPlayer() && this.aiUse && this.able(u) && this.aiShouldUse(u) && u.getCool(id) + this.rechargeTime < Time.time) this.tryuse(u);
        if(this.rechargeFx != Fx.none && u.getCool(id) + this.rechargeTime < Time.time && u.getUse(id) && (this.rechargeVisible || (u.isPlayer() && u.getPlayer() == Vars.player))) this.rechargeFx.at(u.x, u.y, 0, this.color, u);
      }
    }, obj, {
      tryuse(u){
        if(u.getCool(id) + this.rechargeTime > Time.time) this.notyet(u, u.getCool(id) + this.rechargeTime);
        else{
          u.usedCool(id, Time.time);
          this.used(u);
        }
      },

      isActiveSkill(){
        return true;
      }
    });

    unittype.abilities.add(new JavaAdapter(Ability, clone(obj)));
  }
};

//if you wont use activeskills, consider leaving this off
thelib.setupActive(true);

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

module.exports = thelib;
