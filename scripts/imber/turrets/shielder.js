const shieldBreak = loadSound("shield-break");

const shieldBreakFx = new Effect(5, e => {
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

function targetShield(t, b, radius){
	var shield = false;

	Groups.bullet.intersect(t.x - radius, t.y - radius, radius * 2, radius * 2, e => {
		if(e != null && e.team == b.team && e.data instanceof Array && e.data[2] == "shield"){
			shield = true;
		}
	});

	shield = !shield;

	return t.damaged() && shield;
}

const shieldBullet = extend(BasicBulletType, {
	update(b){
		if(b.data == null){
			/** The first number is the shield health, DO NOT TOUCH THE SECOND VALUE */
			b.data = [3000, 0, "shield"];
		}

		var radius = (((3-b.vel.len())*10)+1)*0.8;

		Groups.bullet.intersect(b.x-radius, b.y-radius, radius*2, radius*2, e => {
			if(e != null && e.team != b.team){
				if(e.owner instanceof Building){
					if(e.owner.block.name != "unity-shielder"){
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
			shieldBreak.at(b.x, b.y, Mathf.random(0.8, 1));
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
		var radius = ((3-b.vel.len())*10)+1;

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
shieldBullet.speed = 8;
shieldBullet.lifetime = 20000;
shieldBullet.drag = 0.03;
shieldBullet.shootEffect = Fx.none;
shieldBullet.despawnEffect = Fx.none;
shieldBullet.collides = false;
shieldBullet.hitSize = 0;
shieldBullet.hittable = false;
shieldBullet.hitEffect = Fx.hitLiquid;

const shielder = extendContent(ChargeTurret, "shielder", {});
shielder.shootType = shieldBullet;
shielder.shootSound = loadSound("shielder-shoot");
shielder.chargeEffect = new Effect(38, e => {
	Draw.color(Pal.accent);
	Angles.randLenVectors(e.id, 2, 1 + 20 * e.fout(), e.rotation, 120, new Floatc2({get(x, y){
		Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3 + 1);
	}}));
});
shielder.chargeBeginEffect = Fx.none;
shielder.consumes.add(new ConsumeLiquidFilter(liquid => liquid.temperature <= 0.5 && liquid.flammability <= 0.1, 0.4)).update(false);

shielder.buildType = () => {
	return extendContent(ChargeTurret.ChargeTurretBuild, shielder, {

		/** Make the bullet Stay in its target, not following the path */
		bullet(type, angle){
			var spdScl = Mathf.clamp(Mathf.dst(this.x + shielder.tr.x, this.y + shielder.tr.y, this.targetPos.x, this.targetPos.y) / shielder.range, 0, 1);
		
			type.create(this, this.team, this.x + shielder.tr.x, this.y + shielder.tr.y, angle, spdScl, 1);
		},

		findTarget(){
			this.target = Units.findAllyTile(this.team, this.x, this.y, shielder.range, e => targetShield(e, this, 10) && e != this);
		},

		validateTarget(){
			return this.target != null;
		}
	})
}