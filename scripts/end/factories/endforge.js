const tempVec = new Vec2();

const forgeAbsorbEffect = new Effect(124, e => {
	//var angle = Mathf.randomSeedRange(e.id, 360);
	var angle = e.rotation;
	var slpe = (0.5 - Math.abs(e.finpow() - 0.5)) * 2;
	
	tempVec.trns(angle, (1 - e.finpow()) * 110);
	Draw.color(Color.valueOf("ff786e"));
	Lines.stroke(1.5);
	Lines.lineAngleCenter(e.x + tempVec.x, e.y + tempVec.y, angle, slpe * 8);
	Draw.reset();
});

const endForge = extendContent(GenericCrafter, "end-forge", {
	load(){
		this.super$load();
		this.circuitRegion = Core.atlas.find(this.name + "-lights");
		this.topRegion = Core.atlas.find(this.name + "-top");
		this.smallLightRegion = Core.atlas.find(this.name + "-top-small");
	}
});
endForge.topRegion = null;
endForge.smallLightRegion = null;
endForge.buildType = () => {
	return extendContent(GenericCrafter.GenericCrafterBuild, endForge, {
		updateTile(){
			if(this.consValid() && Mathf.chanceDelta(0.7 * this.warmup)){
				forgeAbsorbEffect.at(this.x, this.y, Mathf.random(360));
			};
			
			this.super$updateTile();
		},
		
		draw(){
			this.super$draw();
			
			if(this.warmup <= 0.0001) return;
			Draw.blend(Blending.additive);
			Draw.color(1, Mathf.absin(Time.time, 5, 0.5) + 0.5, Mathf.absin(Time.time + (90 * Mathf.radDeg), 5, 0.5) + 0.5, this.warmup);
			Draw.rect(endForge.circuitRegion, this.x, this.y);
			
			var b = (Mathf.absin(Time.time, 8, 0.25) + 0.75) * this.warmup;

			Draw.color(1, b, b, b);
			Draw.rect(endForge.topRegion, this.x, this.y);
			
			for(var i = 0; i < 4; i++){
				var ang = i * 90;
				for(var s = 0; s < 2; s++){
					var ofst = (360 / 8) * ((i * 2) + s);
					var reg = endForge.smallLightRegion;
					var sgn = Mathf.signs[s];
					var colA = (Mathf.absin(Time.time + (ofst * Mathf.radDeg), 8, 0.25) + 0.75) * this.warmup;
					var colB = (Mathf.absin(Time.time + ((90 + ofst) * Mathf.radDeg), 8, 0.25) + 0.75) * this.warmup;
					
					Draw.color(1, colA, colB, this.warmup);
					Draw.rect(reg, this.x, this.y, reg.width * sgn * Draw.scl, reg.height * Draw.scl, -ang);
				};
			};
			
			Draw.blend();
			Draw.color();
		}
	});
};