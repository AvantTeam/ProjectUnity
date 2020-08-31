const recrect = extendContent(Reconstructor, "recursive-reconstructor", {
	load(){
		this.super$load();
		this.outRegion = Core.atlas.find("unity-factory-out-11");
		this.inRegion = Core.atlas.find("unity-factory-in-11");
	}
});
