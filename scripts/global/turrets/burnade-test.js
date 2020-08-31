const lib = require("unity/libraries/exp");

const burnadeTest = lib.extend(ItemTurret, ItemTurret.ItemTurretBuild, "burnade-test", {
  maxLevel: 10,
  //Which fields to increase in what way.
  expFields: [
    {
      type: "linear",
      field: "reloadTime",
      start: 60,
      intensity: -5
    }
  ],
  //The original Block extension object.
  init(){
		this.super$init();
		this.ammo(Items.coal, Bullets.basicFlame, Items.pyratite, Bullets.pyraFlame);
	}
}, {
  //The original Building extension object.
  shoot(type){
    //Increment EXP, replace this with whenever you want the block to gain EXP.
    this.incExp(20);
    print("Reload: " + burnadeTest.reloadTime);
    this.super$shoot(type);
  },
  levelUp(level){
    print("You leveled up to level " + level);
  }
});
