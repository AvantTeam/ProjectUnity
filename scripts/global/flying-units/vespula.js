var rotors = [];
	
for(var i = 0; i < 2; i++){
	for(var j = 0; j < 2; j++){
		rotors.push({
			x: 15 * Mathf.signs[i],
			y: 6.75,
			scale: 1,
			bladeCount: 4,
			speed: 29 * Mathf.signs[i] * Mathf.signs[j],
			rotOffset: j * 180
		});
	};
};

const vespula = global.unity.copterbase.extend(UnitEntity, "vespula", {
	rotor: rotors,
	
	fallRotateSpeed: 1.2
}, {});
