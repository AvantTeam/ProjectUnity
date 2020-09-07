const sparkCraftingEffect = new Effect(70, e => {
	Draw.color(Color.valueOf("fff566"), Color.valueOf("ffc266"), e.finpow());
	Draw.alpha(e.finpow());
	Angles.randLenVectors(e.id, 3, (1 - e.finpow()) * 24, e.rotation, 360, new Floatc2({get(x, y){
		Drawf.tri(e.x + x, e.y + y, e.fout() * 8, e.fout() * 10, e.rotation);
		Drawf.tri(e.x + x, e.y + y, e.fout() * 4, e.fout() * 6, e.rotation);
	}}));
	Draw.color();
});



const sparkAlloyForge = extendContent(GenericSmelter, "spark-alloy-forge", {});

sparkAlloyForge.craftEffect = new Effect(30, e => {
	Draw.color(Pal.surge);
	
	Lines.stroke(e.fslope());
	Lines.circle(e.x, e.y, e.fin() * 20);
});

sparkAlloyForge.entityType = () => {
	return extendContent(GenericSmelter.SmelterBuild, sparkAlloyForge, {
		updateTile(){
			this.super$updateTile();
			
			if(this.consValid() && Mathf.chanceDelta(0.3)){
				sparkCraftingEffect.at(this.getX(), this.getY(), Mathf.random(360));
			}

			if(this.consValid() && Mathf.chanceDelta(0.02)){
				Lightning.create(this.team, Color.valueOf("fff566"), 5, this.x, this.y, Mathf.random(360), 5)
			}
		}
	})
};