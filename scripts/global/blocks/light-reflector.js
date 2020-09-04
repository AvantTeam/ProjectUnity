const ref = [[6, 5, 4, -1, 2, 1, 0, -1], [-1, 7, 6, 5, -1, 3, 2, 1], [2, -1, 0, 7, 6, -1, 4, 3], [4, 3, -1, 1, 0, 7, -1, 5]];
const ref1 = [ref[0], ref[2]];
const ref2 = [ref[1], ref[3]];

const reflector = extendContent(Block, "light-reflector", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.angleRegion[req.rotation%2], req.drawx(), req.drawy(), scl, scl);
	},
  load(){
    this.super$load();
    this.angleRegion = [];
    this.angleRegion.push(Core.atlas.find(this.name));
    this.angleRegion.push(Core.atlas.find(this.name) + "-" + 2);
  },
  lightReflector(){
    return true;
  }
});

reflector.entityType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref1[this.rotation%2][dir];
    },
    draw(){
      Draw.rect(reflector.angleRegion[this.rotation%2], this.x, this.y);
    }
  })
}

const reflector90 = extendContent(Block, "light-reflector-1", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.angleRegion[req.rotation%2], req.drawx(), req.drawy(), scl, scl);
	},
  load(){
    this.super$load();
    this.angleRegion = [];
    this.angleRegion.push(Core.atlas.find(reflector.name) + "-" + 1);
    this.angleRegion.push(Core.atlas.find(reflector.name) + "-" + 3);
  },
  lightReflector(){
    return true;
  }
});

reflector90.entityType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref2[this.rotation%2][dir];
    },
    draw(){
      Draw.rect(reflector90.angleRegion[this.rotation%2], this.x, this.y);
    }
  })
}


const colors = [Color.white, Color.red, Color.green, Color.blue];
const ncolors = [Color.black, Color.cyan, Color.magenta, Color.yellow];

const Integer = java.lang.Integer;

const filter = extendContent(Block, "light-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl);
    /*
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();*/
    //rest in drawconfigwhatever
	},
  drawRequestConfig(req, list){
    this.drawRequestConfigTop(req, list);
  },
  drawRequestConfigTop(req, list){
    //req.config
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },
  minimapColor(tile){
    return colors[tile.bc().getFilterColor()].rgba();
  },

  load(){
    this.super$load();
    this.baseRegion = Core.atlas.find(this.name + "-base");
    this.lightRegion = Core.atlas.find("unity-light-center");
  },
  lightRepeater(){
    return true;
  }
});

filter.config(Integer, (build, value) => {
	build.setFilterColor(value);
  if(!Vars.headless){
    Vars.renderer.minimap.update(build.tile);
  }
});
filter.configClear((build) => {
  build.setFilterColor(0);
});

filter.entityType = () => {
  const ent = extend(Building, {
    _color: 0,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    calcLight(ld, i){
      var tc = ld[3].cpy().mul(colors[this._color]);
      var val = Mathf.floorPositive(tc.value()*ld[1]);
      if(val < 5) return null;
      return [ld[0], val, ld[2] - i, tc];
    },
    draw(){
      Draw.rect(filter.baseRegion, this.x, this.y);
      Draw.color(colors[this._color], 0.7);
      Draw.z(Layer.effect + 2);
      Draw.rect(filter.lightRegion, this.x, this.y);
      Draw.color();
      Draw.reset();
    },

    /*
    configured(player, value){
      this.super$configured(player, value);
      this._color = value;
      //print("Configured: "+value);

      if(!Vars.headless){
        Vars.renderer.minimap.update(this.tile);
      }
    },*/
    config(){
      return new Integer(this._color);
    },
    addColorButton(table, i){
      var button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24, () => {
        //print(i);
        this._color = i;
        this.configure(new Integer(i));
        Vars.control.input.frag.config.hideConfig();
      }).size(40).get();
      button.update(() => {
        button.setChecked(i == this._color);
      });
      button.getStyle().imageUpColor = colors[i];
    },
    buildConfiguration(table){
      //Back to the fking UI again. No, this situation is not "nikko" at all
      for(var i=0; i<4; i++){
        this.addColorButton(table, i);
      }
    },
    read(stream, version){
      this.super$read(stream, version);
      this._color = stream.b();
    },
    write(stream){
      this.super$write(stream);
      stream.b(this._color);
    }
  });
  ent._color = 0;
  return ent;
}

//Inverse filter
const filterInv = extendContent(Block, "light-inverted-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl);
	},
  drawRequestConfig(req, list){
    this.drawRequestConfigTop(req, list);
  },
  drawRequestConfigTop(req, list){
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },
  minimapColor(tile){
    return ncolors[tile.bc().getFilterColor()].rgba();
  },

  load(){
    this.super$load();
    this.baseRegion = Core.atlas.find(this.name + "-base");
    this.lightRegion = Core.atlas.find("unity-light-center");
  },
  lightRepeater(){
    return true;
  }
});

filterInv.config(Integer, (build, value) => {
	build.setFilterColor(value);
  if(!Vars.headless){
    Vars.renderer.minimap.update(build.tile);
  }
});
filterInv.configClear((build) => {
  build.setFilterColor(0);
});

filterInv.entityType = () => {
  const ent = extend(Building, {
    _color: 0,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    calcLight(ld, i){
      //TODO: prevent dark light scientifically
      var tc = ld[3].cpy().mul(ncolors[this._color]);
      var val = Mathf.floorPositive(tc.value()*ld[1]);
      if(val < 5) return null;
      return [ld[0], val, ld[2] - i, tc];
    },
    draw(){
      Draw.rect(filterInv.baseRegion, this.x, this.y);
      Draw.color(colors[this._color], 0.7);
      Draw.z(Layer.effect + 2);
      Draw.rect(filter.lightRegion, this.x, this.y);
      Draw.color();
      Draw.reset();
    },

    config(){
      return new Integer(this._color);
    },
    addColorButton(table, i){
      var button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24, () => {
        //print(i);
        this._color = i;
        this.configure(new Integer(i));
        Vars.control.input.frag.config.hideConfig();
      }).size(40).get();
      button.update(() => {
        button.setChecked(i == this._color);
      });
      button.getStyle().imageUpColor = colors[i];
    },
    buildConfiguration(table){
      //Back to the fking UI again. No, this situation is not "nikko" at all
      for(var i=0; i<4; i++){
        this.addColorButton(table, i);
      }
    },
    read(stream, version){
      this.super$read(stream, version);
      this._color = stream.b();
    },
    write(stream){
      this.super$write(stream);
      stream.b(this._color);
    }
  });
  ent._color = 0;
  return ent;
}
