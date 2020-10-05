var rotors = [];

for(var i = 0; i < 2; i++){
	rotors.push({
		x: 0,
		y: 6.5,
		scale: 1,
		bladeCount: 3,
		speed: 29 * Mathf.signs[i],
		rotOffset: 0
	});
};

const schistocerca = global.unity.copterbase.extend(UnitEntity, "schistocerca", {
	rotor: rotors,
	
	fallRotateSpeed: 2.5
}, {});
