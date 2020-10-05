var rotors = [];
	
for(var i = 0; i < 2; i++){
	for(var j = 0; j < 2; j++){
		rotors.push({
			x: Mathf.signs[i] * 22.5,
			y: 21.25,
			scale: 1,
			bladeCount: 3,
			speed: 19 * Mathf.signs[i] * Mathf.signs[j],
			rotOffset: 0
		});
	};
};

for(var i = 0; i < 2; i++){
	for(var j = 0; j < 2; j++){
		rotors.push({
			x: Mathf.signs[i] * 17.25,
			y: 1,
			scale: 0.8,
			bladeCount: 2,
			speed: 23 * Mathf.signs[i] * Mathf.signs[j],
			rotOffset: 0
		});
	};
};

const lepidoptera = global.unity.copterbase.extend(UnitEntity, "lepidoptera", {
	rotor: rotors,
	
	fallRotateSpeed: 0.8
}, {});
