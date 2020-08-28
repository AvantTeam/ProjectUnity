const copter = require("unity/libraries/copterbase");
const att = {
	init(){
		this.rotor = [{
			bladeRegion: Core.atlas.find("unity-caelifera-rotor-blade"),
			topRegion: Core.atlas.find("unity-caelifera-rotor-top"),
			x: 0,
			y: 6,
			scale: 1,
			bladeCount: 4,
			speed: 59
		}];
		this.fallRotateSpeed = 2.5;
	}
}

const caelifera = extendContent(UnitType, "caelifera", {
	init(){
		this.super$init();
		att.init();
	},

	getAttributes(){
		return att;
	},

	draw(unit){
		this.super$draw(unit);
		copter.drawRotor(unit);
	}
});

caelifera.constructor = prov(() => {
	const unit = extend(UnitEntity, {
		update(){
			this.super$update();
			if(this.dead){
				copter.onFall(this);
			}
		}
	});
	return unit;
});
