
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
  const ent = extend(Router.RouterBuild, itemFilter, {
    getItemColor(){
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
