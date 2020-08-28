const loaderBlock = extendContent(Block, "loader-block", {
	load(){
		this.region = Core.atlas.white();
	},
	init(){
		this.super$init();
		print("Loaded Init");
	},
	isHidden(){
		return true;
	}
});

this.global.loader = {};