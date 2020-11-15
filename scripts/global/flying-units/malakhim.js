const ais = this.global.unity.ai;

const malakhimWeapon = new Weapon("heal-weapon-mount");
malakhimWeapon.rotate = true;
malakhimWeapon.x = 11;
malakhimWeapon.y = -7;
malakhimWeapon.reload = 10;
malakhimWeapon.bullet = Bullets.healBullet;

const malakhim = extendContent(UnitType, "malakhim", {
	healStrength(){
		return 15;
	}
});
malakhim.weapons.add(malakhimWeapon);
malakhim.health = 170;
malakhim.engineOffset = 11.7;
malakhim.flying = true;
malakhim.speed = 3.9;
malakhim.accel = 0.08;
malakhim.drag = 0.01;
malakhim.range = 50;
malakhim.commandLimit = 2;
malakhim.ammoType = AmmoTypes.power;
malakhim.hitSize = 10.5 * 1.7;
malakhim.constructor = () => {
	return extend(UnitEntity, {});
};
malakhim.defaultController = ais.unitHealerAI;