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
