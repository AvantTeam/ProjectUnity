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

const filter = extendContent(Block, "light-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
    //rest in drawconfigwhatever
	},
  drawRequestConfig(req, list){
    //req.config
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

const Short = java.lang.Short;
filter.config(Short, (build, value) => {
	build.setFilterColor(value);
});

filter.entityType = () => {
  return extend(Building, {
    _color: 0,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    calcLight(ld, i){
      //TODO: prevent dark light scientifically
      return [ld[0], ld[1], ld[2] - i, ld[3].cpy().mul(colors[this._color])];
    },
    draw(){
      Draw.rect(filter.baseRegion, this.x, this.y);
      Draw.color(colors[this._color], 0.7);
      Draw.rect(filter.lightRegion, this.x, this.y);
      Draw.color();
    },

    configured(player, value){
      this.super$configured(player, value);

      if(!Vars.headless){
        Vars.renderer.minimap.update(this.tile);
      }
    },
    config(){
      return this._color;
    },
    addColorButton(table, i){
      var button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24, () => {
        //print(i);
        this._color = i;
        this.configure(this.config());
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
  })
}


const filterInv = extendContent(Block, "light-inverted-filter", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.baseRegion, req.drawx(), req.drawy(), scl, scl);
    //rest in drawconfigwhatever
	},
  drawRequestConfig(req, list){
    //req.config
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

filterInv.config(Short, (build, value) => {
	build.setFilterColor(value);
});

filterInv.entityType = () => {
  return extend(Building, {
    _color: 0,
    getFilterColor(){
      return this._color;
    },
    setFilterColor(c){
      this._color = c;
    },
    calcLight(ld, i){
      //TODO: prevent dark light scientifically
      return [ld[0], ld[1], ld[2] - i, ld[3].cpy().mul(ncolors[this._color])];
    },
    draw(){
      Draw.rect(filterInv.baseRegion, this.x, this.y);
      Draw.color(colors[this._color], 0.7);
      Draw.rect(filter.lightRegion, this.x, this.y);
      Draw.color();
    },

    configured(player, value){
      this.super$configured(player, value);

      if(!Vars.headless){
        Vars.renderer.minimap.update(this.tile);
      }
    },
    config(){
      return this._color;
    },
    addColorButton(table, i){
      var button = table.button(Tex.whiteui, Styles.clearToggleTransi, 24, () => {
        //print(i);
        this._color = i;
        this.configure(this.config());
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
  })
}
