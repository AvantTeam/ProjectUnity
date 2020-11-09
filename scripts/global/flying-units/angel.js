const ais = this.global.unity.ai;
const tempVec = new Vec2();

const consumeInterval = 5;

const angel = extendContent(UnitType, "angel", {});
angel.health = 90;
angel.engineOffset = 9.7;
angel.flying = true;
angel.speed = 4.3;
angel.accel = 0.08;
angel.drag = 0.01;
angel.range = 40;
angel.commandLimit = 0;
angel.ammoType = AmmoTypes.power;
angel.hitSize = 9;
angel.constructor = () => {
	return extend(UnitEntity, {});
};
angel.defaultController = ais.unitHealerAI;