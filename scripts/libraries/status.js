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

const blueBurnFx = new Effect(35, e => {
    Draw.color(Pal.lancerLaser, Color.valueOf("4f72e1"), e.fin());

    Angles.randLenVectors(e.id, 3, 2 + e.fin() * 7, (x, y) => {
        Fill.circle(e.x + x, e.y + y, 0.1 + e.fout() * 1.4);
    });
});

const radiationL = extendContent(StatusEffect, "radiation", {
	update(unit, time){
		this.super$update(unit, time);
		
		if(Mathf.chanceDelta(0.008 * Mathf.clamp(time / 120))){
			unit.damage(unit.maxHealth * 0.125);
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

//const blueBurnL = new StatusEffect("blue-burn");
const blueBurnL = extend(StatusEffect, "blue-burn", {});
blueBurnL.damage = 0.14;
blueBurnL.effect = blueBurnFx;
blueBurnL.initblock = () => {
    blueBurnL.opposite(StatusEffects.wet, StatusEffects.freezing);
    blueBurnL.trans(StatusEffects.tarred, (unit, time, newTime, result) => {
        unit.damagePierce(8);
        blueBurnFx.at(unit.x() + Mathf.range(unit.bounds() / 2), unit.y() + Mathf.range(unit.bounds() / 2));
        result.set(blueBurnL, Math.min(time + newTime, 400));
    });
};

const reloadFatigueL = new StatusEffect("reload-fatigue");
reloadFatigueL.reloadMultiplier = 0.75;

const endgameDisableL = new StatusEffect("endgame-disable");
endgameDisableL.speedMultiplier = 0.01;
endgameDisableL.permanent = true;
endgameDisableL.color = Color.valueOf("f53036");

module.exports = {
	//darkBurn: darkenHealth,
	radiation: radiationL,
    blueBurn: blueBurnL,
	reloadFatigue: reloadFatigueL,
	endgameDisable: endgameDisableL
};