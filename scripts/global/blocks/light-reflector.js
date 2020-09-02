const ref = [[6, 5, 4, -1, 2, 1, 0, -1], [-1, 7, 6, 5, -1, 3, 2, 1], [2, -1, 0, 7, 6, -1, 4, 3], [4, 3, -1, 1, 0, 7, -1, 5]]

const reflector = extendContent(Block, "light-reflector", {
  drawRequestRegion(req, list) {
		const scl = Vars.tilesize * req.animScale;
		Draw.rect(this.angleRegion[req.rotation], req.drawx(), req.drawy(), scl, scl);
	},
  load(){
    this.super$load();
    this.angleRegion = [];
    this.angleRegion.push(Core.atlas.find(this.name));
    for(var i=1; i<4; i++){
      this.angleRegion.push(Core.atlas.find(this.name) + "-" + i);
    }
  },
  lightReflector(){
    return true;
  }
});

reflector.entityType = () => {
  return extend(Building, {
    calcReflection(dir){
      return ref[this.rotation][dir];
    },
    draw(){
      Draw.rect(reflector.angleRegion[this.rotation], this.x, this.y);
    }
  })
}
