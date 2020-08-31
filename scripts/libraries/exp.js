//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.

//Type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.

function clone(obj){
  if (obj === null || typeof(obj) !== 'object')
  return obj;

  var copy = obj.constructor();

  for (var attr in obj) {
    if (obj.hasOwnProperty(attr)) {
      copy[attr] = obj[attr];
    }
  }

  return copy;
}

module.exports = {
  extend(Type, build, name, obj, objb){
    if(obj == undefined) obj = {};
    if(objb == undefined) objb = {};
    //var block = JSON.parse(lbodyStr);
    //var def = JSON.parse(ldefStr);
    //print("Def: " + Object.keys(def));
    obj = Object.assign({
      //start
      maxLevel: 20,
      level0Color: Pal.accent,
      levelMaxColor: Color.white,
      exp0Color: Color.valueOf("84ff00"),
      expMaxColor: Color.white,
      //levelFunction: "Mathf.sqrt(exp*0.1)+exp*0.001",
      linearInc: [],
      linearIncStart: [],
      linearIncMul: [],
      expInc: [],
      expIncStart: [],
      expIncBase: [],
      rootInc: [],
      rootIncMul: [],
      rootIncStart: []
      //end
    }, obj, {
      //start
      getLevel(exp){
        return Math.min(Mathf.floorPositive(Mathf.sqrt(exp*0.1)), this.maxLevel);
      },
      getRequiredEXP(lvl){
        return lvl*lvl*10;
      },
      getLvlf(exp){
        var lvl = this.getLevel(exp);
        var last = this.getRequiredEXP(lvl);
        var next = this.getRequiredEXP(lvl+1);
        return (exp - last)/(next - last);
      },
      setEXPStats(build){
        var exp = build.totalExp();
        var lvl = this.getLevel(exp);
        print("Build: "+build);
        print("Exp: "+exp);
        print("Lvl: "+lvl);
        if(this.linearInc.length == 1) this[this.linearInc[0]] = Math.max(this.linearIncStart[0] + this.linearIncMul[0]*lvl, 0);
        else if(this.linearInc.length > 0) this.linearEXP(tile, lvl);
      },
      linearEXP(tile, lvl){
        for(var i=0; i<this.linearInc.length; i++){
          this[this.linearInc[i]] = Math.max(this.linearIncStart[i] + this.linearIncMul[i]*lvl, 0);
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
            prov(() => Core.bundle.get("explib.exp")),
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
    //const rec=JSON.parse(JSON.stringify(recipes));
    //Object.assign(obj, block);
    const expblock = extendContent(Type, name, obj);

    /*
    objb.totalExp = () => {

    }*/
    objb = Object.assign(objb, {
      totalExp(){
        return this._exp;
      },
      setExp(a){
        this._exp = a;
      },
      incExp(a){
        this._exp += a;
      },
      updateTile(){
        expblock.setEXPStats(this);
        if(typeof this["customUpdate"]==="function") this.customUpdate();
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
    //var b = JSON.stringify(objb);
    //print("Created Building: " + JSON.stringify(objb));
    //print("Created Building2: " + b);
    expblock.entityType = (ent) => {
      ent = extendContent(build, expblock, clone(objb));
      ent._exp = 0;
      /*
      ent.totalExp = ()=>{
        return ent._exp;
      }
      ent.setExp = (a)=>{
        ent._exp = a;
      }
      ent.incExp = (a)=>{
        ent._exp += a;
      }*/
      return ent;
    }

    return expblock;
  }
}
