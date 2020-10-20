const recrect = extendContent(Reconstructor, "recursive-reconstructor", {
	load(){
		this.super$load();
		this.outRegion = Core.atlas.find("unity-factory-out-11");
		this.inRegion = Core.atlas.find("unity-factory-in-11");
	},

	icons(){
		return [
			Core.atlas.find("unity-recursive-reconstructor"),
			Core.atlas.find("unity-factory-out-11"),
			Core.atlas.find("unity-recursive-reconstructor-top")
		]
	}
});
