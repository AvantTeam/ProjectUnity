
const itemFilter = extendContent(Router, "light-item-filter", {
  load(){
    this.super$load();
    this.baseRegion = Core.atlas.find(this.name + "-base");
    this.lightRegion = Core.atlas.find("unity-light-center");
  },
  lightRepeater(){
    return true;
  }
});

itemFilter.entityType = () => {
  const ent = extendContent(Router.RouterBuild, itemFilter, {
    getItemColor(){
      if(this.items.first() == null) return Color.white;
      return this.items.first().color;
    },
    calcLight(ld, i){
      var tc = ld[3].cpy().mul(this.getItemColor());
      var val = Mathf.floorPositive(tc.value()*ld[1]);
      if(val < 0.1) return null;
      return [ld[0], val, ld[2] - i, tc];
    },
    draw(){
      Draw.rect(itemFilter.baseRegion, this.x, this.y);
      Draw.color(this.getItemColor(), 0.7);
      Draw.z(Layer.effect + 2);
      Draw.rect(itemFilter.lightRegion, this.x, this.y);
      Draw.color();
      Draw.reset();
    }
  });
  return ent;
}

const conslib = require("unity/libraries/light/lightConsumer");

const panel = conslib.extend(SolarGenerator, SolarGenerator.SolarGeneratorBuild, "light-panel", {
  lightStrength: 80,
  scaleStatus: true,
  //The original Block extension object.
  load(){
    this.super$load();
    this.lightRegion = Core.atlas.find("unity-light-center");
  }
}, {
  //The original Building extension object.
  draw(){
    Draw.z(Layer.block);
    Draw.rect(panel.region, this.x, this.y);
    Draw.z(Layer.effect - 2);
    Draw.color(this.lightSumColor(), this.lightStatus());
    Draw.blend(Blending.additive);
    Draw.rect(panel.lightRegion, this.x, this.y);
    Draw.color();
    Draw.blend();
    Draw.reset();
  },
  updateTile(){
    this.productionEfficiency = this.enabled?this.lightStatus():0;
  }
});
