//json version doesnt work at the time of the development of this
const createIconsC = (packer, block) => {
	for(var i = 0; i < block.variants; i++){
		var image = new Pixmap(32, 32);
		var shadow = Core.atlas.getPixmap(block.name + (i + 1));
		var offset = image.getWidth() / Vars.tilesize - 1;
		var color = new Color();
		
		for(var x = 0; x < image.getWidth(); x++){
			for(var y = offset; y < image.getHeight(); y++){
				shadow.getPixel(x, y - offset, color);
				
				if(color.a > 0.001){
					color.set(0, 0, 0, 0.3);
					image.draw(x, y, color);
				};
			};
		};
		
		image.draw(shadow);
		
		packer.add(MultiPacker.PageType.environment, block.name + (i + 1), image);
		packer.add(MultiPacker.PageType.editor, "editor-" + block.name + (i + 1), image);
		
		if(i == 0){
			packer.add(MultiPacker.PageType.editor, "editor-block-" + block.name + "-full", image);
			packer.add(MultiPacker.PageType.main, "block-" + block.name + "-full", image);
		};
	}
};

const umbriumOre = extendContent(OreBlock, "umbrium", {
	init(){
		this.itemDrop = Vars.content.getByName(ContentType.item, "unity-umbrium");
		this.super$init();
	},
	
	createIcons(packer){
		createIconsC(packer, this);
	}
});
umbriumOre.oreScale = 23.77;
umbriumOre.oreThreshold = 0.813;
umbriumOre.oreDefault = true;

const luminumOre = extendContent(OreBlock, "luminum", {
	init(){
		this.itemDrop = Vars.content.getByName(ContentType.item, "unity-luminum");
		this.super$init();
	},
	
	createIcons(packer){
		createIconsC(packer, this);
	}
});
luminumOre.oreScale = 23.77;
luminumOre.oreThreshold = 0.810;
luminumOre.oreDefault = true;
