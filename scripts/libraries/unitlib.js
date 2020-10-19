const addMappingc = provider => {
	for(var i = 0; i < EntityMapping.idMap.length; i++){
		if(EntityMapping.idMap[i] == undefined){
			//print("EntityMapping: (" + i + "): " + provider);
			EntityMapping.idMap[i] = provider;
			return i;
		}
	};
	print("EntityMap is Filled!");
	return 3;
};

module.exports = {
	addMapping(provider){
		return addMappingc(provider);
	},
	
	sortWeapons(weaponSeq){
		var mapped = new Seq();
		for(var i = 0; i < weaponSeq.size; i++){
			var w = weaponSeq.get(i);
			mapped.add(w);

			if(w.mirror){
				var copy = w.copy();
				copy.x *= -1;
				copy.shootX *= -1;
				copy.flipSprite = !copy.flipSprite;
				mapped.add(copy);

				w.reload *= 2;
				copy.reload *= 2;

				w.otherSide = mapped.size - 1;
				copy.otherSide = mapped.size - 2;
			}
		};
		weaponSeq.set(mapped);
	},
};