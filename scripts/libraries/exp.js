//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.

//Type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.

function clone(obj){
  if(obj === null || typeof(obj) !== 'object')
  return obj;

  var copy = obj.constructor();

  for(var attr in obj){
    if(obj.hasOwnProperty(attr)){
      copy[attr] = obj[attr];
    }
  }

  return copy;
}

module.exports = {
  extend(Type, build, name, obj, objb){
    if(obj == undefined) obj = {};
    if(objb == undefined) objb = {};
    obj = Object.assign({
      //start
      maxLevel: 20,
      level0Color: Pal.accent,
      levelMaxColor: Pal.surge,
      exp0Color: Color.valueOf("84ff00"),
      expMaxColor: Color.valueOf("90ff00"),
      expFields: [],
      hasLevelEffect: true,
      levelUpFx: Fx.upgradeCore,
      levelUpSound: Sounds.message,
      //type, field, start, intensity
      //below are legacy arrays
      linearInc: [],
      linearIncStart: [],
      linearIncMul: [],
      expInc: [],
      expIncStart: [],
      expIncMul: [],
      rootInc: [],
      rootIncMul: [],
      rootIncStart: [],
      hasLevelFunction: false,
      hasCustomUpdate: false
      //end
    }, obj, {
      //start
      getLevel(exp){
        return Math.min(Mathf.floorPositive(Mathf.sqrt(exp * 0.1)), this.maxLevel);
      },

      getRequiredEXP(lvl){
        return lvl * lvl * 10;
      },

      getLvlf(exp){
        var lvl = this.getLevel(exp);
        if(lvl >= this.maxLevel) return 1;
        var last = this.getRequiredEXP(lvl);
        var next = this.getRequiredEXP(lvl + 1);
        return (exp - last)/(next - last);
      },

      setEXPStats(build){
        var exp = build.totalExp();
        var lvl = this.getLevel(exp);
        if(this.linearInc.length == 1) this[this.linearInc[0]] = Math.max(this.linearIncStart[0] + this.linearIncMul[0] * lvl, 0);
        else if(this.linearInc.length > 0) this.linearEXP(tile, lvl);
        if(this.expInc.length == 1) this[this.expInc[0]] = Math.max(this.expIncStart[0] + Mathf.pow(this.expIncMul[0], lvl), 0);
        else if(this.expInc.length > 0) this.expEXP(tile, lvl);
        if(this.rootInc.length == 1) this[this.rootInc[0]] = Math.max(this.rootIncStart[0] + Mathf.sqrt(this.rootIncMul[0] * lvl), 0);
        else if(this.rootInc.length > 0) this.rootEXP(tile, lvl);
      },

      linearEXP(tile, lvl){
        for(var i = 0; i < this.linearInc.length; i++){
          this[this.linearInc[i]] = Math.max(this.linearIncStart[i] + this.linearIncMul[i] * lvl, 0);
        }
      },

      expEXP(tile, lvl){
        for(var i = 0; i < this.expInc.length; i++){
          this[this.expInc[i]] = Math.max(this.expIncStart[i] + Mathf.pow(this.expIncMul[i], lvl), 0);
        }
      },

      rootEXP(tile, lvl){
        for(var i = 0; i < this.rootInc.length; i++){
          this[this.rootInc[i]] = Math.max(this.rootIncStart[i] + Mathf.sqrt(this.rootIncMul[i] * lvl), 0);
        }
      },

      setBars(){
        this.super$setBars();
        this.bars.add("level", func(build => {
          return new Bar(
            prov(() => Core.bundle.get("explib.level") + " " + this.getLevel(build.totalExp())),
            prov(() => Tmp.c1.set(this.level0Color).lerp(this.levelMaxColor, this.getLevel(build.totalExp()) / this.maxLevel)),
            floatp(() => {
              return this.getLevel(build.totalExp()) / this.maxLevel;
            })
          )
        }));
        this.bars.add("exp", func(build => {
          return new Bar(
            prov(() => (build.totalExp()<this.maxExp) ? Core.bundle.get("explib.exp") : Core.bundle.get("explib.max")),
            prov(() => Tmp.c1.set(this.exp0Color).lerp(this.expMaxColor, this.getLvlf(build.totalExp()))),
            floatp(() => {
              return this.getLvlf(build.totalExp());
            })
          )
        }));
      }
      //end
    });
    print("Created Block: " + Object.keys(obj));
    const expblock = extendContent(Type, name, obj);
    expblock.maxExp = expblock.getRequiredEXP(expblock.maxLevel);

    for(var i = 0; i < expblock.expFields.length; i++){
      var tobj = expblock.expFields[i];
      if(tobj.type == undefined) tobj.type = "linear";
      expblock[tobj.type + "Inc"].push(tobj.field);
      expblock[tobj.type + "IncStart"].push((tobj.start == undefined) ? 0 : tobj.start);
      expblock[tobj.type + "IncMul"].push((tobj.intensity == undefined) ? 1 : tobj.intensity);
    }

    expblock.hasLevelFunction = (typeof objb["levelUp"] === "function");
    expblock.hasCustomUpdate = (typeof objb["customUpdate"] === "function");

    objb = Object.assign(objb, {
      totalExp(){
        return this._exp;
      },

      totalLevel(){
        return expblock.getLevel(this._exp);
      },

      expf(){
        return expblock.getLvlf(this._exp);
      },

      levelf(){
        return this._exp / expblock.maxExp;
      },

      setExp(a){
        this._exp = a;
      },

      incExp(a){
        if(this._exp >= expblock.maxExp) return;
        this._exp += a;
        if(this._exp > expblock.maxExp) this._exp = expblock.maxExp;
        if(!expblock.hasLevelEffect) return;
        if(expblock.getLevel(this._exp - a) != expblock.getLevel(this._exp)){
          expblock.levelUpFx.at(this.x, this.y, expblock.size);
          expblock.levelUpSound.at(this.x, this.y);
        }
        if(expblock.hasLevelFunction) this.levelUp(expblock.getLevel(this._exp));
      },

      updateTile(){
        expblock.setEXPStats(this);
        if(expblock.hasCustomUpdate) this.customUpdate();
        else this.super$updateTile();
      },

      read(stream, version){
        this.super$read(stream, version);
        this._exp = stream.i();
      },

      write(stream){
        this.super$write(stream);
        stream.i(this._exp);
      }
    });
    //Extend Building
    print("Prep Building: " + Object.keys(objb));
    expblock.entityType = (ent) => {
      ent = extendContent(build, expblock, clone(objb));
      ent._exp = 0;
      return ent;
    }

    return expblock;
  }
}
