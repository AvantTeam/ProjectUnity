var rotors = [];

for(var i = 0; i < 2; i++){
	for(var j = 0; j < 2; j++){
		rotors.push({
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
		rotors.push({
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
		rotors.push({
			x: 25 * Mathf.signs[i],
			y: 22,
			scale: 0.85,
			bladeCount: 2,
			speed: 29 * Mathf.signs[i] * Mathf.signs[j],
			rotOffset: 0
		});
	};
};

const lepidoptera = this.global.unity.copterbase.extend(UnitEntity, "lepidoptera", {
	rotor: rotors,
	
	fallRotateSpeed: 0.5
}, {});
