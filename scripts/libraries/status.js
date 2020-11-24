//const fLib = this.global.unity.funclib;

//const tempCol = new Color();

/*const darkenHealth = extendContent(StatusEffect, "burn-darken", {
	draw(unit){
		var oz = Draw.z();
		Draw.z(Layer.flyingUnit + 0.01);
		tempCol.set(Color.black);
		tempCol.a = Mathf.clamp((1 - unit.healthf()) * 2);
		Draw.color(tempCol);
		fLib.simpleUnitDrawer(unit, false);
		Draw.z(oz);
	}
});
darkenHealth.damage = 2;
darkenHealth.opposite(StatusEffects.freezing);*/

const radiationL = extendContent(StatusEffect, "radiation", {
	update(unit, time){
		this.super$update(unit, time);
		
		if(Mathf.chanceDelta(0.001)){
			unit.damage(unit.maxHealth / 0.125);
		};
		for(var i = 0; i < unit.mounts.length; i++){
			var strength = Mathf.clamp(time / 120);
			if(unit.mounts[i] == null) continue;
			if(Mathf.chanceDelta(0.12)){
				unit.mounts[i].reload = Math.min(unit.mounts[i].reload + ((Time.delta * 1.5) * strength), unit.mounts[i].weapon.reload);
			};
			
			unit.mounts[i].rotation += Mathf.range(12 * strength);
		};
	}
});
radiationL.damage = 1.6;

const reloadFatigueL = new StatusEffect("reload-fatigue");
reloadFatigueL.reloadMultiplier = 0.75;

const endgameDisableL = new StatusEffect("endgame-disable");
endgameDisableL.speedMultiplier = 0.01;
endgameDisableL.permanent = true;
endgameDisableL.color = Color.valueOf("f53036");

module.exports = {
	//darkBurn: darkenHealth,
	radiation: radiationL,
	reloadFatigue: reloadFatigueL,
	endgameDisable: endgameDisableL
};