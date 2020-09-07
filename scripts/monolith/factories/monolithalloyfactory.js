const effect = new Effect(60, e => {
	Draw.color(Pal.lancerLaser);
	
	Lines.stroke(e.fout() * 3 * e.data);
	
	Lines.circle(e.x, e.y, e.finpow() * 24 * e.data);
});

const monolithAlloyForge = extendContent(GenericSmelter, "monolith-alloy-forge", {});
monolithAlloyForge.effectTimer = monolithAlloyForge.timers++;
monolithAlloyForge.entityType = () => {
	var build = extendContent(GenericSmelter.SmelterBuild, monolithAlloyForge, {
		updateTile(){
			this.super$updateTile();
			
			if(this.consValid()){
				this.heat = Mathf.lerpDelta(this.heat, this.efficiency(), 0.02);
			}else{
				this.heat = Mathf.lerpDelta(this.heat, 0, 0.02);
			};
			
			if(!Mathf.zero(this.heat)){
				if(this.timer.get(monolithAlloyForge.effectTimer, 45)) effect.at(this.x, this.y, this.rotation, this.heat);
				
				if(Mathf.chanceDelta(this.heat * 0.5)){
					this.lightning.setLength(this.heat * 4);
					this.lightning.create(this, this.x, this.y, Mathf.randomSeed(Time.time() + this.id, 360));
				};
			};
		}
	});
	var lightning = extend(LightningBulletType, {
		setLength(len){
			this.lightningLength = len;
		},
		
		getLength(){
			return this.lightningLength;
		}
	});
	lightning.lightningLengthRand = 3;
	
	build.lightning = lightning;
	build.heat = 0;
	
	return build;
};
