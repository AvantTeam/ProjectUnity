const effects = this.global.unity.effects;

const plasmaFrag = extend(BulletType, {
	init(b){
		if(!b) return;
		
		this.lifetime = 230 + Mathf.random(40);

		if(typeof(b) === "undefined") return;
    
    	b.data = new Trail(10);
		//print("Frag lifetime: " + this.lifetime);
	},
	
	draw(b){
		b.data.draw(this.colors[0], 8 * 0.5);

		Draw.color(this.colors[0]);
		Drawf.tri(b.x, b.y, 10, 11, b.rotation());
	},
	
	update(b){
		this.super$update(b);
		
		b.data.update(b.x, b.y);

		var target = Units.closestTarget(b.team, b.x, b.y, 8 * Vars.tilesize);
		if(target != null && b.timer.get(1, 12)){
			Lightning.create(b.team, Pal.surge, 56, b.x, b.y, b.angleTo(target), b.dst(target) / Vars.tilesize + 2);
		}
	}
});
plasmaFrag.speed = 4.5;
plasmaFrag.drag = 0.05;
plasmaFrag.damage = 90;
plasmaFrag.collides = false;
plasmaFrag.colors = [Pal.surge, Color.valueOf("f2e87b"), Color.valueOf("d89e6b"), Color.white];
plasmaFrag.hitColor = plasmaFrag.colors[1];
plasmaFrag.shootEffect = effects.imberPlasmaFragAppear;
plasmaFrag.hitEffect = effects.imberPlasmaFragDisappear;
plasmaFrag.despawnEffect = effects.imberPlasmaFragDisappear;

const plasma = extend(BulletType, {
	init(b){
    	if(typeof(b) === "undefined") return;
    
    	b.data = new Trail(10);
  	},

	draw(b){
		b.data.draw(this.colors[0], 13 * 0.5);

		Draw.color(this.colors[0]);
		Drawf.tri(b.x, b.y, 16, 20, b.rotation());
	},
	
	update(b){
		this.super$update(b);

		b.data.update(b.x, b.y);
	},

	despawned(b){
		this.hit(b);
	},
	
	hit(b, x, y){
		this.super$hit(b, b.x, b.y);

		effects.imberTriangleHit.at(b.x, b.y, b.rotation(), this.hitColor);
		
		/*for(var i = 0; i < 10; i++){
			var sr = 8 //scatter range
			//print("Bullet despawned at:" + b.x + ", " + b.y);
			//print("Frag created at:" + (b.x + Mathf.range(sr) * 8) + ", " + (b.y + Mathf.range(sr) * 8));
			plasmaFrag.create(b, b.x + Mathf.range(sr) * 8, b.y + Mathf.range(sr) * 8, Mathf.random(360));
		}*/
	}
});
plasma.lifetime = 190;
plasma.speed = 4;
plasma.damage = 380;
plasma.colors = [Pal.surge, Color.valueOf("f2e87b"), Color.valueOf("d89e6b"), Color.white];
plasma.hitColor = plasma.colors[1];
plasma.fragBullet = plasmaFrag;
plasma.fragBullets = 8;

const plasmaTurret = extendContent(ChargeTurret, "plasma", {
	shouldTurn(){
		return true;
	}
});
plasmaTurret.shootType = plasma
plasmaTurret.shootSound = Sounds.shotgun;
plasmaTurret.shootEffect = effects.imberShootTriangle;
plasmaTurret.chargeBeginEffect = effects.imberChargeBeginTriangles;
plasmaTurret.chargeEffect = effects.imberChargeTriangles;
plasmaTurret.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();