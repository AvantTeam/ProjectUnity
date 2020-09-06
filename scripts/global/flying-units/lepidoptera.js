const att = {
	init(){
		this.rotor = [];
		
		for(var i = 0; i < 2; i++){
			for(var j = 0; j < 2; j++){
				this.rotor.push({
					bladeRegion: Core.atlas.find("unity-lepidoptera-rotor-blade"),
					topRegion: Core.atlas.find("unity-lepidoptera-rotor-top"),
					x: 20 * Mathf.signs[i],
					y: -3,
					scale: 1.3,
					bladeCount: 4,
					speed: 19 * Mathf.signs[i] * Mathf.signs[j],
					rotOffset: 0
				});
			};
		};
		
		for(var i = 0; i < 2; i++){
			for(var j = 0; j < 2; j++){
				this.rotor.push({
					bladeRegion: Core.atlas.find("unity-lepidoptera-rotor-blade"),
					topRegion: Core.atlas.find("unity-lepidoptera-rotor-top"),
					x: 8.5 * Mathf.signs[i],
					y: -20,
					scale: 0.60,
					bladeCount: 3,
					speed: 24 * Mathf.signs[i] * Mathf.signs[j],
					rotOffset: 0
				});
			};
		};
		
		for(var i = 0; i < 2; i++){
			for(var j = 0; j < 2; j++){
				this.rotor.push({
					bladeRegion: Core.atlas.find("unity-lepidoptera-rotor-blade"),
					topRegion: Core.atlas.find("unity-lepidoptera-rotor-top"),
					x: 25 * Mathf.signs[i],
					y: 22,
					scale: 0.85,
					bladeCount: 2,
					speed: 29 * Mathf.signs[i] * Mathf.signs[j],
					rotOffset: 0
				});
			};
		};
		
		this.fallRotateSpeed = 0.5;
	}
};

const copterBase = this.global.unity.copterbase;

const lepidoptera = extendContent(UnitType, "lepidoptera", {
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

lepidoptera.constructor = () => {
	const unit = extend(UnitEntity, {
		update(){
			this.super$update();
			
			if(this.dead){
				copterBase.onFall(this);
			}
		}
	});
	
	return unit;
};
