const att = {
	init(){
		this.rotor = [];
		
		for(var i = 0; i < 2; i++){
			for(var j = 0; j < 2; j++){
				this.rotor.push({
					bladeRegion: Core.atlas.find("unity-vespula-rotor-blade"),
					topRegion: Core.atlas.find("unity-vespula-rotor-top"),
					x: 15 * Mathf.signs[i],
					y: 6.75,
					scale: 1,
					bladeCount: 4,
					speed: 29 * Mathf.signs[i] * Mathf.signs[j],
					rotOffset: j * 180
				});
			};
		};
		
		this.fallRotateSpeed = 1.2;
	}
};

const copterBase = this.global.unity.copterbase;

const vespula = extendContent(UnitType, "vespula", {
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

vespula.constructor = prov(() => {
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
