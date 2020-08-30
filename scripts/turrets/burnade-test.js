const lib = require("unity/libraries/exp");

const burnadeTest = lib.extend(ItemTurret, ItemTurret.ItemTurretBuild, "burnade-test", {
  maxLevel: 10,
  linearInc: ["reload"],
  linearIncMul: [-5],
  linearIncStart: [60],
  init(){
		this.super$init();
		this.ammo(Items.coal, Bullets.basicFlame, Items.pyratite, Bullets.pyraFlame);
	}
}, {
  shoot(type){
    this.incExp(2);
    this.super$shoot(type);
  }
});
