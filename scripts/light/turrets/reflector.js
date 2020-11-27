const lightHit = new Effect(18, e => {
	Draw.color(Color.white);

	Lines.stroke(0.8 + e.fout() * 1.5);
	Angles.randLenVectors(e.id, 7, e.fin() * 18, e.rotation, 360, new Floatc2({get(x, y){
		var ang = Mathf.angle(x, y);
		Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 11 + 5);
	}}));
});

const lightCharge = new Effect(38, e => {
	Draw.color(Color.white);
	Angles.randLenVectors(e.id, 3, e.fout() * 20 + 1, e.rotation, 120, new Floatc2({get(x, y){
		var ang = Mathf.angle(x, y);
		Lines.lineAngle(e.x + x, e.y + y, ang, e.fslope() * 5 + 1);
	}}));
});

const lightChargeBegin = new Effect(120, e => {
	Draw.color(Color.lightGray);
	Fill.circle(e.x, e.y, e.fin() * 3);

	Draw.color();
	Fill.circle(e.x, e.y, e.fin() * 2);
});

const lightOvoid = extend(BulletType, {
	draw(b){
		b.data.draw(this.color, 3);

		Draw.color(this.color);
		Fill.circle(b.x, b.y, 3);
	},

	update(b){
		this.super$update(b);

		b.data.update(b.x, b.y);
	},

	init(b){
		if(!b) return;

		b.data = new Trail(16);
	}
});
//TODO make damage, epilepsy effects, and things based on the light input (unsure)
lightOvoid.damage = 160;
lightOvoid.lifetime = 820;
lightOvoid.speed = 6.8;
lightOvoid.color = Color.white.cpy();
lightOvoid.hitEffect = lightHit;
lightOvoid.shootEffect = Fx.none;
lightOvoid.despawnEffect = lightHit;
lightOvoid.hittable = false;

//TODO lightConsumer after formatting
const reflector = extendContent(PowerTurret, "reflector", {
	//TODO epic stuff and effects (?)
  setStats(){
    this.super$setStats();
    
    this.stats.remove(Stat.booster);
    this.stats.add(Stat.input, new BoosterListValue(reflector.reloadTime, reflector.consumes.get(ConsumeType.liquid).amount, reflector.coolantMultiplier, false, l => reflector.consumes.liquidfilters.get(l.id)));
  }
});
reflector.shootType = lightOvoid;
reflector.shootSound = Sounds.laser;
reflector.chargeEffect = lightCharge;
reflector.chargeTime = 120;
reflector.chargeBeginEffect = lightChargeBegin;
reflector.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).update(false);