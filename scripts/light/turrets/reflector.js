/*const mirrorBeam = extend(BulletType, {
	draw(b){
		if(b.data instanceof Position){
			var data = b.data;
			Tmp.v1.set(data);
			
			Draw.color(this.color);
			Drawf.laser(b.team, Core.atlas.find("laser"), Core.atlas.find("laser-end"), b.x, b.y, Tmp.v1.x, Tmp.v1.y, b.fout() * this.width);
			Draw.reset();
			
			Drawf.light(Team.derelict, b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, b.fout() * 15 + 5, this.lightColor, 0.6);
		}
	},
	
	hit(b, x, y){
		this.super$hit(b, b.x, b.y);
		//mirrorBeam.create(b, b.x, b.y, b.rotation() * 2);
	},
	
	init(b){
		if(!b) return;
		this.super$init(b);
		
		var target = Damage.linecast(b, b.x, b.y, b.rotation(), this.length);
		b.data = target;
		
		if(target instanceof Hitboxc){
			var hit = target;

			hit.collision(b, hit.x, hit.y);
			b.collision(hit, hit.x, hit.y);
		}else if(target instanceof Building){
			var tile = target;

			if(tile.collide(b)){
				tile.collision(b);
				this.hit(b, tile.x, tile.y);
			}
		}else{
			b.data = new Vec2().trns(b.rotation(), this.length).add(b.x, b.y);
		}
	}
});
//TODO make damage, length, and things based on the light input (unsure)
mirrorBeam.damage = 69;
mirrorBeam.speed = 0.0001;
mirrorBeam.color = Color.white.cpy();
mirrorBeam.shootEffect = Fx.none;
mirrorBeam.despawnEffect = Fx.none;
mirrorBeam.width = 0.82;
mirrorBeam.length = 120;

//TODO lightConsumer after formatting
const reflector = extendContent(ChargeTurret, "reflector", {
	//TODO epic stuff and effects (?)
});
reflector.shootType = mirrorBeam;
reflector.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).update(false);*/