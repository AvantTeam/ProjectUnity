const craftingEffect = new Effect(67, 35, e => {
	Tmp.v1.trns(e.rotation + ((Mathf.randomSeed(e.id) + 4) * e.fin() * 80), (Mathf.randomSeed(e.id * 126) + 1) * 34 * (1 - e.finpow()));
	Draw.color(Color.valueOf("ff9c5a"));
	Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fslope() * 3, 45);
	Draw.color();
});

const darkAlloyForge = extendContent(GenericSmelter, "dark-alloy-forge", {});
darkAlloyForge.entityType = () => {
	return extendContent(GenericSmelter.SmelterBuild, darkAlloyForge, {
		updateTile(){
			this.super$updateTile();
			
			if(this.consValid() && Mathf.chanceDelta(0.76)){
				craftingEffect.at(this.getX(), this.getY(), Mathf.random(360));
			}
		}
	})
};