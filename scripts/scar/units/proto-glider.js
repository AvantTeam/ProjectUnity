var laser = extend(LaserBulletType, {});
laser.damage = 170;
laser.sideAngle = 25;
laser.sideWidth = 3;
laser.sideLength = 20;
laser.width = 25;
laser.length = 150;
laser.shootEffect = Fx.shockwave;
laser.colors = [Color.valueOf("f53036"), Color.valueOf("ff786e")];

var main_cannon = new Weapon("unity-proto-glider-cannon");

main_cannon.reload = 50;
main_cannon.x = 0;
main_cannon.y = 0;
main_cannon.mirror = false;
main_cannon.rotate = true;
main_cannon.shake = 3;
main_cannon.rotateSpeed = 2;
main_cannon.bullet = laser;

var sides = [
	{x: 16, y: 5},
	{x: -16, y: 5},
	{x: 16, y: -5},
	{x: -16, y: -5},
	{x: 9, y: 0},
	{x: -9, y: 0},
];

const protoGlider = extendContent(UnitType, "proto-glider", {
	load(){
		this.super$load();
		this.region = Core.atlas.find(this.name);
	},

	drawPayload(unit) {
        if(unit.payloads.size > 0) {
           	for (var i = unit.payloads.size - 1; i >= 0; i--) {
           		if (sides[i]) {
           			var side = sides[i];
           			var offsetx = Angles.trnsx(unit.rotation, side.y, side.x);
           			var offsety = Angles.trnsy(unit.rotation, side.y, side.x);
	           		var pay = unit.payloads.get(i);
	           		pay.set(unit.x + offsetx, unit.y + offsety, unit.rotation);
	           		pay.draw();
	            }
            }
        }
	},	
});
//Groups.unit.each(u=>{u.team = Team.sharded})
protoGlider.constructor = () => {
	const unit = extend(BuilderPayloadUnit, {})
	return unit;
}

protoGlider.weapons.add(main_cannon);

protoGlider.speed = 3;
protoGlider.drag = 0.07;
protoGlider.accel = 0.03;
protoGlider.engineSize = 3;
protoGlider.hitSize = 25;
protoGlider.range = 155;
protoGlider.payloadCapacity = 64 * (2.5 * 2.5);
protoGlider.flying = true;