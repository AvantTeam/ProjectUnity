
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
    Draw.color(this.lightSumColor(), this.lastLightStatus());
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

const infl = conslib.extend(SolarGenerator, SolarGenerator.SolarGeneratorBuild, "light-influencer", {
  lightStrength: 1,
  scaleStatus: true,
  //The original Block extension object.
  load(){
    this.super$load();
    this.topRegion = Core.atlas.find(this.name + "-top");
  }
}, {
  //The original Building extension object.
  _connected: [false, false, false, false],
  draw(){
    Draw.z(Layer.block);
    Draw.rect(infl.region, this.x, this.y);
    Draw.z(Layer.effect - 2);
    Draw.color(this.lightSumColor(), 1);
    Draw.blend(Blending.additive);
    Draw.rect(infl.topRegion, this.x, this.y);
    for(var i=0; i<4; i++){
      if(this._connected[i]) Drawf.tri(this.x + Geometry.d4x[i]*2, this.y + Geometry.d4y[i]*2, 3, 6, i*90);
    }
    Draw.color();
    Draw.blend();
    Draw.reset();
  },
  onProximityUpdate(){
    this.super$onProximityUpdate();
    for(var i=0; i<4; i++){
      var build = this.tile.getNearbyEntity(i);
      if(build != null && build.block.name == "unity-light-filter"){
        build.setCont(this);
        this._connected[i] = true;
      }
      else this._connected[i] = false;
    }
  },
  getFilterColor(){
    return this.lightSumColor();
  },
  updateTile(){
    this.productionEfficiency = 0;
  }
});
