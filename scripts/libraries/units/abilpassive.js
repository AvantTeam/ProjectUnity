const waitFx = new Effect(30, e => {
  if(!Array.isArray(e.data)) return;
  if(e.data[1] == null || !e.data[1].isValid() || e.data[1].dead) return;
  Draw.color(e.color);
  Lines.stroke(e.fout() * 1.5);
  Lines.polySeg(60, 0, 60 * ((e.rotation - Time.time) / e.data[0]), e.data[1].x, e.data[1].y, 8, 0);
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

module.exports = {
  add(unittype, obj){
    if(obj == undefined) obj = {};
    obj = Object.assign({
      //this ability recharges when idle and used when tryuse(u) is called or able(u) is true. override used(u).
      name: "$ability.none",
      rechargeTime: 120,
      rechargeFx: Fx.none,
      color: Pal.lancerLaser,
      autoTry: false,
      checkOnce: true,

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
        waitFx.at(u.x, u.y, whenready, this.color, [this.rechargeTime, u]);
      },
      update(u){
        if(this.checkOnce){
          if(this.able(u)){
            if(!this.check){
              this.tryuse(u);
              this.check = true;
            }
          }
          else{
            this.check = false;
          }
        }
        if(this.timer + this.rechargeTime > Time.time){
          if(this.autoTry && this.able(u)) this.tryuse(u);
          if(this.isUsed){
            this.isUsed = false;
            this.rechargeFx.at(u.x, u.y, this.color);
          }
        }

      }
    }, obj, {
      timer: 0,
      isUsed: false,
      check: true,
      tryuse(u){
        if(this.timer + this.rechargeTime > Time.time) this.notyet(u, this.timer + this.rechargeTime);
        else{
          this.timer = Time.time;
          this.isUsed = true;
          this.used(u);
        }
      }
    });

    unittype.abilities.add(new JavaAdapter(Ability, clone(obj)));
  }
}
