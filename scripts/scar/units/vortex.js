const effects = this.global.unity.effects;

//const tempColor = new Color();

const cutEffect = new Effect(3 * 60, e => {
	if(e.data == null) return;
	if(e.data[0] instanceof Unit && !e.data[0].added) return;
	var unit = e.data[0];
	var a = e.data[1];
	var b = e.data[2];
	//tempColor.set(Pal.bulletYellow);
	//tempColor.a = e.fout();
	Draw.color(Pal.lightFlame);
	
	Lines.stroke(0.5 + (e.fout() * 1.5));
	Lines.line(unit.x + a.x, unit.y + a.y, unit.x + b.x, unit.y + b.y);
});

const vortexLaser = extend(ContinuousLaserBulletType, {
	update(b){
		if(b.data instanceof Array && b.data[0] == true){
			if(b.owner != null && !b.owner.dead){
				b.data[1].update(b.owner.aimX, b.owner.aimY);
				if(Mathf.within(b.owner.aimX, b.owner.aimY, b.owner.x, b.owner.y, 280)){
					
					var dstDamage = Mathf.dst(b.owner.aimX, b.owner.aimY, b.data[2].x, b.data[2].y) / Time.delta;
					var size = 4;
					var sx = b.owner.aimX;
					var sy = b.owner.aimY;
					
					Units.nearbyEnemies(b.team, sx - size, sy - size, size * 2, size * 2, e => {
						if(Mathf.within(sx, sy, e.x, e.y, e.hitSize) && dstDamage > 2 && !e.dead){
							e.damage(dstDamage * 8);
							cutEffect.at(e.x, e.y, 0, [e, new Vec2(sx - e.x, sy - e.y), new Vec2(b.data[2].x - e.x, b.data[2].y - e.y)]);
						};
					});
					
					var build = Vars.world.buildWorld(sx, sy);
					if(build != null && build.team != b.team && dstDamage > 2){
						build.damage(dstDamage * 5);
						cutEffect.at(build.x, build.y, 0, [build, new Vec2(sx - build.x, sy - build.y), new Vec2(b.data[2].x - build.x, b.data[2].y - build.y)]);
					};
				};
				
				b.data[2].set(b.owner.aimX, b.owner.aimY);
			};
		}else{
			this.super$update(b);
		};
	},
	init(b){
		if(b == undefined) return;
		if(b.owner instanceof Unit && b.owner.controller instanceof Player){
			b.data = [true, new Trail(4), new Vec2(b.owner.aimX, b.owner.aimY)];
		}else{
			if(b.owner != null && !b.owner.dead){
				b.data = [false, new Trail(4), new Vec2()];
			};
		};
	},
	draw(b){
		if(b.data instanceof Array && b.data[0] == true){
			if(Mathf.within(b.owner.aimX, b.owner.aimY, b.owner.x, b.owner.y, 280)) b.data[1].draw(Color.valueOf("f53036"), 3);
		}else{
			this.super$draw(b);
		};
	}
});
vortexLaser.largeHit = false;
vortexLaser.damage = 60;
vortexLaser.lifetime = 5 * 60;
vortexLaser.length = 190;
vortexLaser.width = 5;
vortexLaser.incendChance = 0;
vortexLaser.hitEffect = effects.coloredHitSmall;
vortexLaser.lightColor = Color.valueOf("f5303690");
vortexLaser.hitColor = Color.valueOf("f5303690");
vortexLaser.colors = [Color.valueOf("f5303690"), Color.valueOf("ff786e"), Color.white];
vortexLaser.strokes = [1.5, 1, 0.3];

const vortexWeaponLaser = new Weapon("");
vortexWeaponLaser.mirror = false;
vortexWeaponLaser.x = 0;
vortexWeaponLaser.bullet = vortexLaser;
//vortexWeaponLaser.minShootVelocity = 5;
vortexWeaponLaser.continuous = true;
vortexWeaponLaser.reload = (1.2 * 60) + vortexLaser.lifetime;

const vortex = extendContent(UnitType, "vortex", {});
vortex.constructor = () => extend(UnitEntity, {});
vortex.weapons.add(vortexWeaponLaser);
vortex.health = 1200;
vortex.rotateSpeed = 12.5;
vortex.faceTarget = true;
vortex.flying = true;
vortex.speed = 9.1;
vortex.drag = 0.019;
vortex.accel = 0.028;
vortex.hitSize = 11;
vortex.engineOffset = 14;