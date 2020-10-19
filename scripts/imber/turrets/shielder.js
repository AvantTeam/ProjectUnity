const shieldBullet = extend(BasicBulletType, {
	update(b){
		if(b.data == null){
			b.data = [800, 0];
		}

		var radius = ((3-b.vel.len())*10)*0.8
		Groups.bullet.intersect(b.x-radius, b.y-radius, radius*2, radius*2, e => {
			if(e != null && e.team != b.team ){
				if(e.owner instanceof Building){
					if(e.owner.block.name != "unity-shielder"){
						b.data[0] -= (e.damage/3)
						b.data[1] = 1;
						e.remove();
					}
				} else {
					b.data[0] -= (e.damage/3)
					b.data[1] = 1;
					e.remove();
				}
				
			}
		});

		if(b.data[0] <= 0){
        	b.remove();
        }

		if(b.data[1] > 0){
        	b.data[1] -= 1 / 5 * Time.delta;
        }
	},

	draw(b){
		Draw.z(Layer.shields);
		Draw.color(b.team.color, Color.white, b.data != null ? Mathf.clamp(b.data[1]) : 0);

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
shieldBullet.lifetime = 20000;
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