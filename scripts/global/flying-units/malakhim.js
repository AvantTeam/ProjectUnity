const ais = this.global.unity.ai;

const malakhimBullet = new LaserBoltBulletType(5.2, 10);
malakhimBullet.lifetime = 35;
malakhimBullet.healPercent = 5.5;
malakhimBullet.collidesTeam = true;
malakhimBullet.backColor = Pal.heal;
malakhimBullet.frontColor = Color.white;

const malakhimWeapon = new Weapon("heal-weapon-mount");
malakhimWeapon.rotate = true;
malakhimWeapon.x = 11;
malakhimWeapon.y = -7;
malakhimWeapon.reload = 10;
malakhimWeapon.bullet = malakhimBullet;

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