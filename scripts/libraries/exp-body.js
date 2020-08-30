//EXP library by sk7725. Recommended for turrets, works with any block.
//the fields in Block are global, but does not matter if you set them for each block every calculation - just like Tmp.
const _body = {
  /*
  update(tile){
    this.setEXPStats(tile);
    if(typeof this["customUpdate"]==="function") this.customUpdate(tile);
  },*/
  getLevel(exp){
    return Math.min(Mathf.floorPositive(Mathf.sqrt(exp)*0.1), this.maxLevel);
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
    const exp = build.totalExp();
    const lvl = this.getLevel(exp);
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
        prov(() => Tmp.cl.set(this.level0Color).lerp(this.levelMaxColor, this.getLevel(build.totalExp()) / this.maxLevel)),
        floatp(() => {
          return this.getLevel(build.totalExp()) / this.maxLevel;
        })
      )
    }));
    this.bars.add("exp", func(build => {
      return new Bar(
        prov(() => Core.bundle.get("explib.exp")),
        prov(() => Tmp.cl.set(this.exp0Color).lerp(this.expMaxColor, this.getLvlf(build.totalExp()))),
        floatp(() => {
          return this.getLvlf(build.totalExp());
        })
      )
    }));
  }
}

module.exports = {
  body: _body,
  def: {
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
  }
}
