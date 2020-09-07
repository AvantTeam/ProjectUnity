//TODO finish it 
const chargeTriangles = new Effect(96, e => {
	Draw.color(Pal.surge);
	
	Angles.randLenVectors(e.id, 5, (1 - e.finpow()) * 24, e.rotation, 360, new Floatc2({get(x, y){
		Drawf.tri(e.x + x, e.y + y, e.fout() * 10, e.fout() * 11, e.rotation);
		Drawf.tri(e.x + x, e.y + y, e.fout() * 8, e.fout() * 9, e.rotation);
	}}));
});

const chargeBeginTriangles = new Effect(190, e => {
	Draw.color(Pal.surge);
	
	Drawf.tri(e.x, e.y, e.fin() * 16, e.fin() * 20, e.rotation);
});

const unecessaryCircle = new Effect(30, e => {
	Draw.color(Pal.surge);
	
	Lines.stroke(e.fout() * 2.8);
	Lines.circle(e.x, e.y, e.fout() * 50);
});

const fragAppear = new Effect(12, e => {
	Draw.z(Layer.bullet - 0.01);
	
	Draw.color(Color.white);
	Drawf.tri(e.x, e.y, e.fin() * 12, e.fin() * 13, e.rotation);
	
	Draw.z();
});

const fragDisappear = new Effect(12, e => {
	Draw.z(Layer.bullet - 0.01);
	
	Draw.color(Pal.surge, Color.white, e.fin());
	Drawf.tri(e.x, e.y, e.fout() * 10, e.fout() * 11, e.rotation);
	
	Draw.z();
});

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
plasmaFrag.speed = 4;
plasmaFrag.drag = 0.05;
plasmaFrag.damage = 20;
plasmaFrag.colors = [Pal.surge, Color.valueOf("f2e87b"), Color.valueOf("d89e6b"), Color.white];
plasmaFrag.hitColor = plasmaFrag.colors[1];
plasmaFrag.shootEffect = fragAppear;
plasmaFrag.hitEffect = fragDisappear;
plasmaFrag.despawnEffect = fragDisappear;

const plasma = extend(BulletType, {
	init(b){
		//this.super$init(b);

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

		unecessaryCircle.at(b.x, b.y, b.rotation(), this.hitColor);
		
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
plasma.damage = 280;
plasma.colors = [Pal.surge, Color.valueOf("f2e87b"), Color.valueOf("d89e6b"), Color.white];
plasma.hitColor = plasma.colors[1];
plasma.fragBullet = plasmaFrag;
plasma.fragBullets = 10;

//plasma.strokes = [1.1, 0.9, 0.7, 0.5];

const plasmaTurret = extendContent(ChargeTurret, "plasma", {});
plasmaTurret.shootType = plasma;
plasmaTurret.chargeMaxDelay = 190;
plasmaTurret.chargeBeginEffect = chargeBeginTriangles;
plasmaTurret.chargeEffect = chargeTriangles;
plasmaTurret.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();