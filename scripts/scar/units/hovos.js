const effects = this.global.unity.effects;
const ais = this.global.unity.ai;

const hovosBullet = extend(RailBulletType, {});
hovosBullet.damage = 500;
hovosBullet.speed = 59;
hovosBullet.lifetime = 8;
hovosBullet.shootEffect = effects.scarRailShoot;
hovosBullet.pierceEffect = effects.scarRailHit;
hovosBullet.updateEffect = effects.scarRailTrail;
hovosBullet.hitEffect = Fx.massiveExplosion;
hovosBullet.pierceDamageFactor = 0.3;

const hovosRailgun = new Weapon("unity-small-scar-railgun");
hovosRailgun.reload = 2 * 60;
hovosRailgun.x = 0;
hovosRailgun.y = -2;
hovosRailgun.shootY = 9;
hovosRailgun.mirror = false;
hovosRailgun.rotate = true;
hovosRailgun.shake = 2.3;
hovosRailgun.rotateSpeed = 2;
hovosRailgun.bullet = hovosBullet;

const hovos = extendContent(UnitType, "hovos", {});
hovos.speed = 0.8;
hovos.weapons.add(hovosRailgun);
hovos.health = 340;
hovos.hitSize = 17;
hovos.range = 350;
hovos.allowLegStep = true;
hovos.legMoveSpace = 0.7;
hovos.legTrns = 0.4;
hovos.legLength = 30;
hovos.legExtension = -4.3;
hovos.defaultController = ais.distanceGroundAI;
hovos.constructor = () => extend(LegsUnit, {});