var rotors = [];

rotors.push({
	x: 0,
	y: -13,
	scale: 0.6,
	bladeCount: 4,
	speed: 29,
	rotOffset: 0
});
for(var i = 0; i < 2; i++){
	rotors.push({
		x: 13 * Mathf.signs[i],
		y: 3,
		scale: 1,
		bladeCount: 3,
		speed: 29 * Mathf.signs[i],
		rotOffset: i * 180
	});
};

const anthophila = global.unity.copterbase.extend(UnitEntity, "anthophila", {
	rotor: rotors,
	
	fallRotateSpeed: 2
}, {});
