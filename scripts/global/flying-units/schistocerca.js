const att = {
	init(){
		this.rotor = [];
		
		for(var i = 0; i < 2; i++){
			this.rotor.push({
				bladeRegion: Core.atlas.find("unity-schistocerca-rotor-blade"),
				topRegion: Core.atlas.find("unity-schistocerca-rotor-top"),
				x: 0,
				y: 6.5,
				scale: 1,
				bladeCount: 3,
				speed: 29 * Mathf.signs[i],
				rotOffset: 0
			});
		}
		
		this.fallRotateSpeed = 2.5;
	}
}

const copterBase = this.global.unity.copterbase;

const schistocerca = extendContent(UnitType, "schistocerca", {
	init(){
		this.super$init();
		att.init();
	},
	
	getAttributes(){
		return att;
	},
	
	draw(unit){
		this.super$draw(unit);
		copterBase.drawRotor(unit);
	}
});

schistocerca.constructor = prov(() => {
	const unit = extend(UnitEntity, {
		update(){
			this.super$update();
			
			if(this.dead){
				copterBase.onFall(this);
			}
		}
	});
	
	return unit;
});
