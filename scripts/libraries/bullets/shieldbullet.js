const effects = this.global.unity.effects;

module.exports = {
	newShieldBullet(maxRadius, health, breakSound){
    const shieldBullet = extend(BasicBulletType, {
      update(b){
          if(b.data == null){
              /** The first number is the shield health, DO NOT TOUCH THE SECOND VALUE! */
              b.data = [health, 0, "shield"];
          }
          var radius = (((3 - b.vel.len()) * maxRadius) + 1) * 0.8;
          Groups.bullet.intersect(b.x - radius, b.y - radius, radius * 2, radius * 2, e => {
              if(e != null && e.team != b.team){
                  if(e.owner instanceof Building){
                      if(e.owner.block.name != b.owner.name){
                        b.data[0] -= e.damage;
                        b.data[1] = 1;
                        e.remove();
                      }
                  }else{
                      b.data[0] -= e.damage;
                      b.data[1] = 1;
                      e.remove();
                  }
              }
          });
          if(b.data[0] <= 0){
              breakSound.at(b.x, b.y, Mathf.random(0.8, 1));
              effects.imberShieldBreakFx.at(b.x, b.y, 0, b.team.color, radius);
              b.remove();
          }
          if(b.data[1] > 0){
              b.data[1] -= 1 / 5 * Time.delta;
          }
      },
      
      draw(b){
          Draw.z(Layer.shields);
          Draw.color(b.team.color, Color.white, b.data != null ? Mathf.clamp(b.data[1]) : 0);
          var radius = (((3 - b.vel.len()) * maxRadius) + 1) * 0.8;
          if(Core.settings.getBool("animatedshields")){
              Fill.poly(b.x, b.y, 6, radius);
          }else{
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
    shieldBullet.speed = 8;
    shieldBullet.drag = 0.03;
		shieldBullet.lifetime = 20000;
		shieldBullet.shootEffect = Fx.none;
		shieldBullet.despawnEffect = Fx.none;
		shieldBullet.collides = false;
		shieldBullet.hitSize = 0;
		shieldBullet.hittable = false;
		shieldBullet.hitEffect = Fx.hitLiquid;

		return shieldBullet;
	}
};