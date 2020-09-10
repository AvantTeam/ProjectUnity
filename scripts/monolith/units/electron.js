const elecBullet = extend(BasicBulletType, {
	init(b){
		if(typeof(b) !== "undefined"){
			b.data = new Trail(6);
		};
	},
	
	draw(b){
		b.data.draw(this.frontColor, this.width);
		
		Draw.color(this.frontColor);
		Fill.circle(b.x, b.y, this.width)
		Draw.color();
	},
	
	update(b){
		this.super$update(b);
		
		b.data.update(b.x, b.y);
	},
	
	hit(b, x, y){
		this.super$hit(b, b.x, b.y);
		
		b.data.clear();
	}
});
elecBullet.frontColor = Pal.lancerLaser;
elecBullet.backColor = Pal.lancerLaser.cpy().mul(0.7);
elecBullet.width = elecBullet.height = 2;
elecBullet.weaveScale = 3;
elecBullet.weaveMag = 5;
elecBullet.homingPower = 1;
elecBullet.speed = 3.5;
elecBullet.lifetime = 60;
elecBullet.damage = 3;
elecBullet.shootEffect = Fx.hitLancer;

const elecWeap = extendContent(Weapon, "unity-electron-shotgun", {});
elecWeap.mirror = true;
elecWeap.reload = 60;
elecWeap.recoil = 2.5;
elecWeap.x = 5.25;
elecWeap.y = -0.25;
elecWeap.shots = 12;
elecWeap.spacing = 0.5;
elecWeap.inaccuracy = 0.5;
elecWeap.velocityRnd = 0.2
elecWeap.shotDelay = 0;
elecWeap.shootSound = Sounds.shootBig;
elecWeap.bullet = elecBullet;

const electron = extendContent(UnitType, "electron", {});
electron.weapons.add(elecWeap);
electron.constructor = () => extend(MechUnit, {});
