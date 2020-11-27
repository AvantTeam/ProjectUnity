const shildB = this.global.unity.shieldbullet;
const shieldBreak = loadSound("shield-break");

function targetShield(t, b, radius){
	var shield = false;

	Groups.bullet.intersect(t.x - radius, t.y - radius, radius * 2, radius * 2, e => {
		if(e != null && e.team == b.team && Array.isArray(e.data) && e.data.length == 3 && e.data[2] == "shield"){
			shield = true;
		}
	});

	shield = !shield;

	return t.damaged() && shield;
}

const shieldBullet = shildB.newShieldBullet(10, 3000, shieldBreak);

const shielder = extendContent(PowerTurret, "shielder", {
  setStats(){
    this.super$setStats();
    
    this.stats.remove(Stat.targetsAir);
    this.stats.remove(Stat.targetsGround);
    this.stats.remove(Stat.damage);
    this.stats.remove(Stat.booster);
    this.stats.add(Stat.input, new BoosterListValue(shielder.reloadTime, shielder.consumes.get(ConsumeType.liquid).amount, shielder.coolantMultiplier, false, l => shielder.consumes.liquidfilters.get(l.id)));
  }
});
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

		/** Make the bullet stay in its target, not following the path */
		bullet(type, angle){
			//var spdScl = Mathf.clamp(Mathf.dst(this.x + shielder.tr.x, this.y + shielder.tr.y, this.targetPos.x, this.targetPos.y) / type.range(), shielder.range / type.range());
      //rhinoJS has a stroke because there's also a value on bullets called range. I'ma pr a fix for that.
      
      var spdScl = Mathf.clamp(Mathf.dst(this.x + shielder.tr.x, this.y + shielder.tr.y, this.targetPos.x, this.targetPos.y) / shielder.range, 0, 1);

			type.create(this, this.team, this.x + shielder.tr.x, this.y + shielder.tr.y, angle, spdScl, 1);
		},

		findTarget(){
			this.target = Units.findAllyTile(this.team, this.x, this.y, shielder.range, e => targetShield(e, this, 10) && e != this);
		},

		validateTarget(){
			return this.target != null;
		},
    
    canControl(){
      return false;
    }
	})
}
