const sEffect = this.global.unity.status;
const effects = this.global.unity.effects;

const kelvBullet = extend(BasicBulletType, {
	update(b){
        this.super$update(b);
        
        if(b.timer.get(0, 1)){
            effects.advanceFlameTrail.at(b.x + Mathf.range(0.6), b.y + Mathf.range(0.6), b.rotation());
		};
		
		if(Mathf.chanceDelta(0.7)){
            effects.advanceFlameSmoke.at(b.x + Mathf.range(1.7), b.y + Mathf.range(1.7), b.rotation());
		}
	},
	
	draw(b){
		Draw.color(Pal.lancerLaser, Color.valueOf("4f72e1"), b.fin());
		Fill.poly(b.x, b.y, 6, 3 + b.fin() * 4.1, b.rotation() + b.fin() * 270);
		Draw.reset();
	}
});

kelvBullet.speed = 4.7;
kelvBullet.drag = 0.016;
kelvBullet.damage = 16;
kelvBullet.lifetime = 32;
kelvBullet.hitSize = 4;
kelvBullet.shootEffect = Fx.none;
kelvBullet.smokeEffect = Fx.none;
//kelvBullet.trailEffect = effects;
kelvBullet.hitEffect = effects.hitAdvanceFlame;
kelvBullet.despawnEffect = Fx.none;
kelvBullet.collides = true;
kelvBullet.collidesTiles = true;
kelvBullet.collidesAir = true;
kelvBullet.pierce = true;
kelvBullet.statusDuration = 770;
kelvBullet.status = sEffect.blueBurn;

const kelvin = extendContent(PowerTurret, "kelvin", {});

kelvin.shootType = kelvBullet;