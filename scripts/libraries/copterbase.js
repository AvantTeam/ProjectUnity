this.global.unity.copterBase = {
	drawRotor(unit){
		const att = unit.type.getAttributes();

		Draw.mixcol(Color.white, unit.hitTime / unit.hitDuration);

		for(var i = 0; i < att.rotor.length; i++){
			var rotor = att.rotor[i];

			var region = rotor.bladeRegion;
			var topRegion = rotor.topRegion;

			var offx = Angles.trnsx(unit.rotation - 90, rotor.x, rotor.y);
			var offy = Angles.trnsy(unit.rotation - 90, rotor.x, rotor.y);

			var w = region.getWidth() * rotor.scale * Draw.scl;
			var h = region.getHeight() * rotor.scale * Draw.scl;

			for(var j = 0; j < rotor.bladeCount; j++){
				var angle = ((unit.id * 24) + (Time.time() * rotor.speed) + ((360 / rotor.bladeCount) * j)) % 360;

				Draw.alpha(Vars.state.isPaused() ? 1 : Time.time() % 2);
				Draw.rect(region, unit.x + offx, unit.y + offy, w, h, angle);
			};

			Draw.alpha(1);
			Draw.rect(topRegion, unit.x + offx, unit.y + offy, unit.rotation - 90);
		};

		Draw.mixcol();
	},

	onFall(unit){
		const att = unit.type.getAttributes();

		unit.rotation += att.fallRotateSpeed * Mathf.signs[unit.id % 2];
	}
};
