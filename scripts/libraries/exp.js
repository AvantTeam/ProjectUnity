//EXP library by sk7725. Recommended for turrets, works with any block.
const lib = require("unity/libraries/exp-body");

//Type: The block you are extending.
//build: the building you are extending.
//name: the name of your block.
//obj: what will override the block; add exp params here.
//objb: what will override the building.
module.exports = {
  extend(Type, build, name, obj, objb){
    if(obj == undefined) obj = {};
    if(obj2 == undefined) obj2 = {};
    const block = Object.create(lib.body);
    const def = Object.create(lib.def);
    Object.assign(def, obj, block);
    //const rec=JSON.parse(JSON.stringify(recipes));
    //Object.assign(obj, block);
    const expblock = extendContent(Type, name, def);

    Object.assign(objb, {
      _exp: 0,
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
    expblock.entityType = () => {
      const ent = extendContent(build, expblock, objb);
      return ent;
    }

    return expblock;
  }
}
