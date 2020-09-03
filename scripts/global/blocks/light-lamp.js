const lib = require("unity/libraries/light/lightSource");

const lamp = lib.extend(GenericCrafter, GenericCrafter.GenericCrafterBuild, "light-lamp", {
  lightLength: 30,
  //The original Block extension object.
  drawRequestRegion(req, list){
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, req.rotation*90);
	},
  load(){
    this.super$load();
    this.baseRegion = Core.atlas.find(this.name + "-base");
    this.topRegion = Core.atlas.find(this.name + "-top");
    this.lightRegion = Core.atlas.find("unity-light-center");
  }
}, {
  //The original Building extension object.
  draw(){
    Draw.z(Layer.block);
    Draw.rect(lamp.baseRegion, this.x, this.y);
    if(this.lightPower() > lamp.lightStrength/2){
      Draw.z(Layer.effect - 2);
      Draw.rect(lamp.lightRegion, this.x, this.y);
    }
    Draw.z(Layer.effect + 2);
    Draw.rect(lamp.topRegion, this.x, this.y, this.rotation*90);
    Draw.reset();
  }
});
