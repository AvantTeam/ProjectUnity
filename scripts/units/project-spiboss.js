const prspiboss = extendContent(UnitType, "project-spiboss", {
	load() {
		this.super$load();
		this.region = Core.atlas.find(this.name);
	}
});
var sapBull1 = extend(SapBulletType, {});
sapBull1.sapStrength = 0.8;
sapBull1.length = 90;
sapBull1.damage = 25;
sapBull1.shootEffect = Fx.shootSmall;
sapBull1.hitColor = sapBull1.color = Color.valueOf("bf92f9");
sapBull1.despawnEffect = Fx.none;
sapBull1.width = 0.7;
sapBull1.lifetime = 35;
sapBull1.knockback = -1.5;
var sapBull2 = extend(SapBulletType, {});
sapBull2.sapStrength = 0.9;
sapBull2.length = 90;
sapBull2.damage = 30;
sapBull2.shootEffect = Fx.shootSmall;
sapBull2.hitColor = sapBull1.color = Color.valueOf("bf92f9");
sapBull2.despawnEffect = Fx.none;
sapBull2.width = 0.75;
sapBull2.lifetime = 35;
sapBull2.knockback = -1.5;
var weap1 = new Weapon("large-purple-mount");
var weap2 = new Weapon("mount-purple-weapon");
var weap3 = new Weapon("mount-purple-weapon");
var weap4 = new Weapon("spiroct-weapon");
var weap5 = new Weapon("spiroct-weapon");
weap1.reload = 50;
weap1.x = 5;
weap1.y = -2;
weap1.rotate = true;
weap1.shake = 1;
weap1.rotateSpeed = 1;
weap1.bullet = Bullets.artilleryPlastic;
weap2.reload = 20;
weap2.x = 5;
weap2.y = 10;
weap2.rotate = true;
weap2.shake = 1;
weap2.rotateSpeed = 5;
weap2.bullet = sapBull1;
weap3.reload = 20;
weap3.x = 10;
weap3.y = 8;
weap3.rotate = true;
weap3.shake = 1;
weap3.rotateSpeed = 5;
weap3.bullet = sapBull1;
weap4.reload = 23;
weap4.x = 12;
weap4.y = 2;
weap4.rotate = true;
weap4.shake = 1;
weap4.rotateSpeed = 3;
weap4.bullet = sapBull2;
weap5.reload = 23;
weap5.x = 18;
weap5.y = 0;
weap5.rotate = true;
weap5.shake = 1;
weap5.rotateSpeed = 3;
weap5.bullet = sapBull2;
prspiboss.constructor = () => {
	const unit = extend(LegsUnit, {})

	return unit;
}
var weaps = [weap1, weap2, weap3, weap4, weap5];
//prspiboss.defaultController = new BuilderAI();
for(var i = 0; i<weaps.length; i++){
	weaps[i].alternate = false;
	weaps[0].alternate = true;
	prspiboss.weapons.add(weaps[i]);
};