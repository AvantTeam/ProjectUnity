const sEffect = this.global.unity.status;
const effects = this.global.unity.effects;

const celsBullet = extend(BasicBulletType, {
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

celsBullet.speed = 4.7;
celsBullet.drag = 0.034;
celsBullet.damage = 36;
celsBullet.lifetime = 18;
celsBullet.hitSize = 4;
celsBullet.shootEffect = Fx.none;
celsBullet.smokeEffect = Fx.none;
//celsBullet.trailEffect = effects;
celsBullet.hitEffect = effects.hitAdvanceFlame;
celsBullet.despawnEffect = Fx.none;
celsBullet.collides = true;
celsBullet.collidesTiles = true;
celsBullet.collidesAir = true;
celsBullet.pierce = true;
celsBullet.statusDuration = 770;
celsBullet.status = sEffect.blueBurn;

const cels = extendContent(PowerTurret, "celsius", {});

cels.shootType = celsBullet;