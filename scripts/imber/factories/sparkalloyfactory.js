const effects = this.global.unity.effects;

const sparkAlloyForge = extendContent(GenericSmelter, "spark-alloy-forge", {});
sparkAlloyForge.craftEffect = effects.imberCircleSparkCraftingEffect;
sparkAlloyForge.buildType = () => {
	return extendContent(GenericSmelter.SmelterBuild, sparkAlloyForge, {
		updateTile(){
			this.super$updateTile();

			if(this.consValid() && Mathf.chanceDelta(0.3)){
				effects.imberSparkCraftingEffect.at(this.getX(), this.getY(), Mathf.random(360));
			}

			if(this.consValid() && Mathf.chanceDelta(0.02)){
				Lightning.create(this.team, Color.valueOf("fff566"), 5, this.x, this.y, Mathf.random(360), 5)
			}
		}
	})
};
