//TODO: research from item terminum
const terminalCrucible = extendContent(GenericSmelter, "terminal-crucible", {
	load(){
		this.super$load();

		this.circuitRegion = Core.atlas.find(this.name + "-lights");
	}
});
terminalCrucible.circuitRegion = null;
terminalCrucible.flameColor = Color.valueOf("f53036");
terminalCrucible.buildType = () => extendContent(GenericSmelter.SmelterBuild, terminalCrucible, {
	draw(){
		terminalCrucible.drawer.draw(this);

		if(this.warmup > 0){
			Draw.blend(Blending.additive);
			Draw.color(1, Mathf.absin(Time.time(), 5, 0.5) + 0.5, Mathf.absin(Time.time + (90 * Mathf.radDeg), 5, 0.5) + 0.5, this.warmup);
			Draw.rect(terminalCrucible.circuitRegion, this.x, this.y);

			var b = (Mathf.absin(Time.time(), 8, 0.25) + 0.75) * this.warmup;

			Draw.color(1, b, b, b);
			Draw.rect(terminalCrucible.topRegion, this.x, this.y);
			Draw.blend();

			Draw.color();
		}
	}
});
