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

reflector.buildType = () => {
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

reflector90.buildType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref2[this.rotation%2][dir];
    },
    draw(){
      Draw.rect(reflector90.angleRegion[this.rotation%2], this.x, this.y);
    }
  })
}

const h = [1, 1, 1, 1, 1, 1, 1, 1];//tmp
const Integer = java.lang.Integer;
const refomni = [ref[1], h, ref[2], h, ref[3], h, ref[0], h];

const omnimirror = extendContent(Block, "light-omnimirror", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
    if(req.config != null){
      this.drawRequestConfig(req, list);
    }
	},
  drawRequestConfig(req, list){
    const scl = Vars.tilesize * req.animScale;
    Draw.rect(this.topRegion, req.drawx(), req.drawy(), scl, scl, (req.config == null)?(0 + req.rotation*90):(req.config*22.5 + req.rotation*90));
  },
  load(){
    this.super$load();
    this.baseRegion = Core.atlas.find(this.name + "-base");
    this.topRegion = Core.atlas.find(this.name + "-mirror");
  },
  lightReflector(){
    return true;
  }
});
omnimirror.lastConfig = new Integer(0);
omnimirror.config(Integer, (build, value) => {
  build.addAngle(value);
});
omnimirror.buildType = () => {
  const ent = extend(Building, {
    _rotconf: 0,
    addAngle(a){
      this._rotconf += a;
      this._rotconf = this._rotconf % 8;
    },

    calcReflection(dir){
      return refomni[(this.rotation*4+this._rotconf)%8][dir];
    },
    draw(){
      Draw.rect(omnimirror.baseRegion, this.x, this.y);
      Draw.rect(omnimirror.topRegion, this.x, this.y, this._rotconf*22.5 + this.rotation*90);
    },

    config(){
      return new Integer(this._rotconf);
    },

    buildConfiguration(table){
      table.button(Icon.leftOpen, Styles.clearTransi, 34, () => {
        this.configure(new Integer(1));
      }).size(40);
      table.button(Icon.rightOpen, Styles.clearTransi, 34, () => {
        this.configure(new Integer(-1));
      }).size(40);
    },

    read(stream, version){
      this.super$read(stream, version);
      this._rotconf = stream.b();
    },
    write(stream){
      this.super$write(stream);
      stream.b(this._rotconf % 8);
    }
  });
  return ent;
}


const colors = [Color.white, Color.red, Color.green, Color.blue];
const ncolors = [Color.black, Color.cyan, Color.magenta, Color.yellow];

const filter = extendContent(Block, "light-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl);
    /*
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();*/
    //rest in drawconfigwhatever
    if(req.config != null){
      this.drawRequestConfig(req, list);
    }
	},
  drawRequestConfig(req, list){
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },
  /*
  drawRequestConfigTop(req, list){
    //req.config
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },*/
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

filter.buildType = () => {
  const ent = extend(Building, {
    _color: 0,
    _cont: null,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    getTrueColor(){
      if(this._cont == null) return colors[this._color];
      else{
        if(!this._cont.isValid()){
          this._cont = null;
          return colors[this._color];
        }
        else return this._cont.getFilterColor();
      }
    },
    setCont(b){
      this._cont = b;
    },
    calcLight(ld, i){
      var tc = ld[3].cpy().mul(this.getTrueColor());
      var val = Mathf.floorPositive(tc.value()*ld[1]);
      if(val < 0.1) return null;
      return [ld[0], val, ld[2] - i, tc];
    },
    draw(){
      Draw.rect(filter.baseRegion, this.x, this.y);
      Draw.color(this.getTrueColor(), 0.7);
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
  ent._cont = null;
  ent._color = 0;
  return ent;
}

//Inverse filter
const filterInv = extendContent(Block, "light-inverted-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.region, req.drawx(), req.drawy(), scl, scl);
    if(req.config != null){
      this.drawRequestConfig(req, list);
    }
	},
  drawRequestConfig(req, list){
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },
  /*
  drawRequestConfigTop(req, list){
    if(req.config == null) return;
    const scl = Vars.tilesize * req.animScale;
    Draw.color(colors[req.config], 0.7);
    Draw.rect(this.lightRegion, req.drawx(), req.drawy(), scl, scl);
    Draw.color();
  },*/
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

filterInv.buildType = () => {
  const ent = extend(Building, {
    _color: 0,
    _cont: null,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    getTrueColor(){
      if(this._cont == null) return ncolors[this._color];
      else{
        if(!this._cont.isValid()){
          this._cont = null;
          return ncolors[this._color];
        }
        else return this._cont.getFilterColor().cpy().inv();
      }
    },
    getRawColor(){
      if(this._cont == null) return colors[this._color];
      else{
        if(!this._cont.isValid()){
          this._cont = null;
          return colors[this._color];
        }
        else return this._cont.getFilterColor();
      }
    },
    setCont(b){
      this._cont = b;
    },
    calcLight(ld, i){
      //TODO: prevent dark light scientifically
      var tc = ld[3].cpy().mul(this.getTrueColor());
      var val = Mathf.floorPositive(tc.value()*ld[1]);
      if(val < 0.1) return null;
      return [ld[0], val, ld[2] - i, tc];
    },
    draw(){
      Draw.rect(filterInv.baseRegion, this.x, this.y);
      Draw.color(this.getRawColor(), 0.7);
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
  ent._cont = null;
  return ent;
}


const divisor = extendContent(Block, "light-divisor", {
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
  lightDivisor(){
    return true;
  }
});

divisor.buildType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref1[this.rotation%2][dir];
    },
    draw(){
      Draw.rect(divisor.angleRegion[this.rotation%2], this.x, this.y);
    }
  })
}

const divisor90 = extendContent(Block, "light-divisor-1", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.angleRegion[req.rotation%2], req.drawx(), req.drawy(), scl, scl);
	},
  load(){
    this.super$load();
    this.angleRegion = [];
    this.angleRegion.push(Core.atlas.find(divisor.name) + "-" + 1);
    this.angleRegion.push(Core.atlas.find(divisor.name) + "-" + 3);
  },
  lightDivisor(){
    return true;
  }
});

divisor90.buildType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref2[this.rotation%2][dir];
    },
    draw(){
      Draw.rect(divisor90.angleRegion[this.rotation%2], this.x, this.y);
    }
  })
}
