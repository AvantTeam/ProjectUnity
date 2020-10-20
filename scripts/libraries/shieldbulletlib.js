/** Fix it yourself */
/*module.exports = {
	newShieldBullet = (owner, maxRadius, health, breakDuration, breakSound, lifetime) => {
		const shieldBreakFx = new Effect(breakDuration, e => {
			Draw.z(Layer.shields);
			Draw.color(e.color);
			var radius = e.data * e.fout();

			if(Core.settings.getBool("animatedshields")){
				Fill.poly(e.x, e.y, 6, radius);
			} else {
				Lines.stroke(1.5);
				Draw.alpha(0.09);
				Fill.poly(e.x, e.y, 6, radius);
				Draw.alpha(1);
				Lines.poly(e.x, e.y, 6, radius);
				Draw.reset();
			}
			Draw.z(Layer.block);

			Draw.color();
		});

		const shieldBullet = extend(BasicBulletType, {
			update(b){
				if(b.data == null){
					
					b.data = [health, 0];
				}

				var radius = (((3-b.vel.len())*maxradius)+1)*0.8;

				Groups.bullet.intersect(b.x-radius, b.y-radius, radius*2, radius*2, e => {
					if(e != null && e.team != b.team){
						if(e.owner instanceof Building){
							if(e.owner.block.name != owner.name){
								b.data[0] -= e.damage;
								b.data[1] = 1;
								e.remove();
							}
						} else {
							b.data[0] -= e.damage;
							b.data[1] = 1;
							e.remove();
						}
				
					}
				});

				if(b.data[0] <= 0){
					breakSound.at(b.x, b.y, Mathf.random(0.8, 1));
					shieldBreakFx.at(b.x, b.y, 0, b.team.color, radius);
					b.remove();
				}

				if(b.data[1] > 0){
					b.data[1] -= 1 / 5 * Time.delta;
				}
			},

			draw(b){
				Draw.z(Layer.shields);
				Draw.color(b.team.color, Color.white, b.data != null ? Mathf.clamp(b.data[1]) : 0);
				var radius = ((3-b.vel.len())*maxradius)+1;

				if(Core.settings.getBool("animatedshields")){
					Fill.poly(b.x, b.y, 6, radius);
				} else {
					Lines.stroke(1.5);
					Draw.alpha(0.09 + Mathf.clamp(0.08 * b.data[1]));
					Fill.poly(b.x, b.y, 6, radius);
					Draw.alpha(1);
					Lines.poly(b.x, b.y, 6, radius);
					Draw.reset();
				}
				Draw.z(Layer.block);

				Draw.color();
			}
		});

		shieldBullet.damage = 0;
		shieldBullet.speed = 3;
		shieldBullet.lifetime = lifetime;
		shieldBullet.drag = 0.012;
		shieldBullet.shootEffect = Fx.none;
		shieldBullet.despawnEffect = Fx.none;
		shieldBullet.collides = false;
		shieldBullet.hitSize = 0;
		shieldBullet.hittable = false;
		shieldBullet.hitEffect = Fx.hitLiquid;

		return shieldBullet;
	}
};*/