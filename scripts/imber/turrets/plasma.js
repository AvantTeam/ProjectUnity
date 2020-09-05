//TODO finish it 

const plasmaBeam = extend(ContinuousLaserBulletType, {
	update(b){
		this.super$update(b);
		
		if(Mathf.chanceDelta(0.43)){
			Lightning.create(b, Pal.surge, 23, b.x, b.y, b.rotation(), 25 + Mathf.random(4));
		}
	}
});
plasmaBeam.damage = 40;
plasmaBeam.incendChance = 0.2;
plasmaBeam.colors = [Pal.surge, Color.valueOf("d89e6b"), Color.valueOf("f2e87b"), Color.white];
plasmaBeam.strokes = [1.2, 1, 0.7, 0.3];

const plasma = extendContent(LaserTurret, "plasma", {});
plasma.shootType = plasmaBeam;
plasma.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.52)).boost();