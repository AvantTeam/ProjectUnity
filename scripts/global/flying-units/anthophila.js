const att = {
	init(){
		this.rotor = [];
		
		this.rotor.push({
			bladeRegion: Core.atlas.find("unity-anthophila-rotor-blade2"),
			topRegion: Core.atlas.find("unity-anthophila-rotor-top2"),
			x: 0,
			y: -13,
			scale: 1,
			bladeCount: 4,
			speed: 29,
			rotOffset: 0
		});
		for(var i = 0; i < 2; i++){
			this.rotor.push({
				bladeRegion: Core.atlas.find("unity-anthophila-rotor-blade"),
				topRegion: Core.atlas.find("unity-anthophila-rotor-top"),
				x: 13 * Mathf.signs[i],
				y: 3,
				scale: 1,
				bladeCount: 3,
				speed: 29 * Mathf.signs[i],
				rotOffset: i * 180
			});
		}
		
		this.fallRotateSpeed = 2;
	}
}

const copterBase = this.global.unity.copterBase;

const anthophila = extendContent(UnitType, "anthophila", {
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

anthophila.constructor = prov(() => {
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
