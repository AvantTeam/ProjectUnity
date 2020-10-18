const shieldBullet = extend(BasicBulletType, {
	update(b){
		if(b.data == null){
			b.data = 0;
		}

		var radius = ((3-b.vel.len())*10)*0.8
		Groups.bullet.intersect(b.x-radius, b.y-radius, radius*2, radius*2, e => {
			if(e != null && e.team != b.team && e.owner /*ehem*/){
				b.data = 1;
				e.remove();
			}
		});

		if(b.data > 0){
        	b.data -= 1 / 5 * Time.delta;
        }
	},

	draw(b){
		Draw.z(Layer.shields);
		Draw.color(b.team.color, Color.white, Mathf.clamp(b.data));

		if(Core.settings.getBool("animatedshields")){
			Fill.poly(b.x, b.y, 6, ((3-b.vel.len())*10));
		} else {
			Lines.stroke(1.5);
            Draw.alpha(0.09);
            Fill.poly(b.x, b.y, 6, ((3-b.vel.len())*10));
            Draw.alpha(1);
            Lines.poly(b.x, b.y, 6, ((3-b.vel.len())*10));
            Draw.reset();
		}
		Draw.z(Layer.block);

		Draw.color();
	}
});
shieldBullet.damage = 0;
shieldBullet.speed = 3;
shieldBullet.lifetime = 2000;
shieldBullet.drag = 0.01;
shieldBullet.shootEffect = Fx.none;
shieldBullet.despawnEffect = Fx.none;
shieldBullet.collides = false;
shieldBullet.hitSize = 0;
shieldBullet.hittable = false;
shieldBullet.hitEffect = Fx.hitLiquid;

const shielder = extendContent(PowerTurret, "shielder", {});
shielder.shootType = shieldBullet;
shielder.shootSound = Sounds.thruster;
shielder.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.4)).update(false);