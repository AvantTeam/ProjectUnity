const caelifera = global.unity.copterbase.extend(UnitEntity, "caelifera", {
	rotor: [{
		x: 0,
		y: 6,
		scale: 1,
		bladeCount: 4,
		speed: 29,
		rotOffset: 0
	}],
	
	fallRotateSpeed: 2.5
}, {});
