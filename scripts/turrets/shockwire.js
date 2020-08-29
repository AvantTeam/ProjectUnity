const cl = this.global.unity.chainlaser;

const laserChain5 = cl.chainLaser(Color.white.cpy(), 26, 100, 0.53, StatusEffects.shocked, 60 * 3, false, null);
const laserChain4 = cl.chainLaser(Color.white.cpy(), 26, 100, 0.53, StatusEffects.shocked, 60 * 3, true, laserChain5);
const laserChain3 = cl.chainLaser(Color.white.cpy(), 26, 100, 0.53, StatusEffects.shocked, 60 * 3, true, laserChain4);
const laserChain2 = cl.chainLaser(Color.white.cpy(), 26, 100, 0.53, StatusEffects.shocked, 60 * 3, true, laserChain3);
const laserChain1 = cl.chainLaser(Color.white.cpy(), 26, 100, 0.53, StatusEffects.shocked, 60 * 3, true, laserChain2);

const shockwire = extendContent(PowerTurret, "shockwire", {});
shockwire.shootType = laserChain1;
